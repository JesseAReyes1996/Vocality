package edu.jreye039.vocality;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.v7.widget.RecyclerView;
import android.widget.Toast;

import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferObserver;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3Client;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;

public class FeedBackgroundWorker extends AsyncTask<String, Void, String> {

    Context context;

    private RecyclerView feedRecyclerView;
    private FeedAdapter feedAdapter;

    private ArrayList<FeedItem> userRecordings = new ArrayList<>();

    FeedBackgroundWorker(Context ctx, RecyclerView rv){
        context = ctx;
        feedRecyclerView = rv;
    }

    //username of the currently logged in user
    String username;

    //temp directory/files to store the S3 objects
    File outputDir;
    File tempRecording;
    File tempAccompaniment;

    MediaPlayer mediaPlayerRecording;
    MediaPlayer mediaPlayerAccompaniment;
    //to check whether the two MediaPlayers are both ready
    boolean bothPrepared = false;

    boolean isPlaying = false;
    //to know what recording was played last
    int lastPlayed = -1;

    @Override
    protected void onPreExecute() {
        //clear the recycler view
        feedAdapter = new FeedAdapter(userRecordings);
        feedRecyclerView.setAdapter(feedAdapter);

        //create the temp files
        outputDir = context.getCacheDir();
        try {
            tempRecording = File.createTempFile("tempRecording", ".3gp", outputDir);
            tempAccompaniment = File.createTempFile("tempAccompaniment", ".3gp", outputDir);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected String doInBackground(String... params) {
        //the user's username
        username = params[0];

        try{
            //create a POST request to the given URL
            URL url = new URL("http://jesseareyes1996.hostingerapp.com/vocality_feed.php");
            //open the connection
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setRequestMethod("POST");
            httpURLConnection.setDoOutput(true);
            httpURLConnection.setDoInput(true);

            //initiate the POST request for the user's requested song
            OutputStream outputStream = httpURLConnection.getOutputStream();
            BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
            String post_data = URLEncoder.encode("username", "UTF-8") + "=" + URLEncoder.encode(username, "UTF-8");
            //send the POST request to the server
            bufferedWriter.write(post_data);
            bufferedWriter.flush();
            bufferedWriter.close();
            outputStream.close();

            //receive the result
            InputStream inputStream = httpURLConnection.getInputStream();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, "iso-8859-1"));
            String result = "";
            String line = "";
            while((line = bufferedReader.readLine()) != null){
                result += line;
            }
            bufferedReader.close();
            inputStream.close();
            //close the connection
            httpURLConnection.disconnect();

            return result;

        }catch(MalformedURLException e){
            e.printStackTrace();
        }catch(IOException e){
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onProgressUpdate(Void... values) {
        super.onProgressUpdate(values);
    }

    @Override
    protected void onPostExecute(String result) {

        //contains the results with the format [title, username, aws_s3_key, aws_backing_track, private, likes, comments, id]
        final String[] rows;

        if(!result.equals("no results")){

            //empty the recordings array
            userRecordings = new ArrayList<>();

            rows = result.split("><");
            //iterate through the results
            for(String row : rows){
                String[] columns = row.split("<>");
                //if the recording is not private
                if(columns[4].equals("0")){
                    //add the title/user who recorded
                    userRecordings.add(new FeedItem(columns[0], columns[1], Integer.parseInt(columns[5]), Integer.parseInt(columns[6])));
                }
            }

            //update the recycler view with the new search results
            feedAdapter = new FeedAdapter(userRecordings);
            feedRecyclerView.setAdapter(feedAdapter);

            //set an OnClickListener on the displayed items' play button
            feedAdapter.setOnItemClickListener(new FeedAdapter.OnItemClickListener() {
                @Override
                public void onLikeClick(int position) {
                    //the recording the user liked
                    String chosenRecording = rows[position];
                    String[] columns = chosenRecording.split("<>");

                    //TODO
                    Toast.makeText(context, "LIKING " + position, Toast.LENGTH_SHORT).show();

                    FeedLikesBackgroundWorker backgroundWorker = new FeedLikesBackgroundWorker(context);

                    //get username of user liking
                    SharedPreferences userInfo = context.getSharedPreferences("userInfo", Context.MODE_PRIVATE);
                    String userLiking = userInfo.getString("username", "");

                    //username of user liking, id of post liking
                    backgroundWorker.execute(userLiking, columns[7]);
                }

                @Override
                public void onCommentClick(int position) {
                    //TODO
                    Toast.makeText(context, "COMMENTING " + position, Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onPlayClick(int position) {
                    //picks the song the user clicked on
                    String chosenRecording = rows[position];
                    String[] columns = chosenRecording.split("<>");
                    //holds the AWS S3 link for the chosen song
                    String recordingKey = columns[2];
                    String accompanimentKey = columns[3];

                    //when the user taps the play button of the currently playing recording
                    if(isPlaying && lastPlayed == position){
                        isPlaying = false;
                        lastPlayed = -1;
                        if(mediaPlayerRecording != null){
                            mediaPlayerRecording.stop();
                            mediaPlayerRecording.release();
                        }

                        if(mediaPlayerAccompaniment != null){
                            mediaPlayerAccompaniment.stop();
                            mediaPlayerAccompaniment.release();
                        }
                    }
                    //either starts a recording if nothing is currently being playing or if
                    //a recording is being played, ensures only a different recording can be started
                    else if(!isPlaying && lastPlayed != position){
                        isPlaying = true;
                        lastPlayed = position;

                        //download the file
                        credentialsProvider();
                        setTransferUtility();

                        downloadFileFromS3(recordingKey, tempRecording);
                        downloadFileFromS3(accompanimentKey, tempAccompaniment);

                        final Handler downloadDelay = new Handler();
                        downloadDelay.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                //set up the user's audio
                                mediaPlayerRecording = new MediaPlayer();
                                try{
                                    mediaPlayerRecording.setDataSource(tempRecording.toString());
                                    mediaPlayerRecording.prepareAsync();

                                    mediaPlayerRecording.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                                        @Override
                                        public void onPrepared(MediaPlayer mp) {
                                            if(!bothPrepared){
                                                bothPrepared = true;
                                            }
                                            else if(bothPrepared){
                                                mediaPlayerAccompaniment.start();
                                                mediaPlayerRecording.start();
                                                bothPrepared = false;
                                            }
                                        }
                                    });

                                }catch(IOException e){
                                    e.printStackTrace();
                                }

                                //set up the accompaniment's audio
                                mediaPlayerAccompaniment = new MediaPlayer();
                                try{
                                    mediaPlayerAccompaniment.setDataSource(tempAccompaniment.toString());
                                    mediaPlayerAccompaniment.prepareAsync();

                                    mediaPlayerAccompaniment.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                                        @Override
                                        public void onPrepared(MediaPlayer mp) {
                                            if(!bothPrepared){
                                                bothPrepared = true;
                                            }
                                            else if(bothPrepared){
                                                mediaPlayerRecording.start();
                                                mediaPlayerAccompaniment.start();
                                                bothPrepared = false;
                                            }
                                        }
                                    });
                                }catch(IOException e){
                                    e.printStackTrace();
                                }
                            }
                        }, 1000);
                    }

                    //when the user taps the play button of a different recording
                    else if(isPlaying && lastPlayed != position){
                        lastPlayed = position;
                        if(mediaPlayerRecording != null){
                            mediaPlayerRecording.stop();
                            mediaPlayerRecording.release();
                        }

                        if(mediaPlayerAccompaniment != null){
                            mediaPlayerAccompaniment.stop();
                            mediaPlayerAccompaniment.release();
                        }
                        //download the file
                        credentialsProvider();
                        setTransferUtility();

                        downloadFileFromS3(recordingKey, tempRecording);
                        downloadFileFromS3(accompanimentKey, tempAccompaniment);

                        final Handler downloadDelay = new Handler();
                        downloadDelay.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                //set up the user's audio
                                mediaPlayerRecording = new MediaPlayer();
                                try{
                                    mediaPlayerRecording.setDataSource(tempRecording.toString());
                                    mediaPlayerRecording.prepareAsync();

                                    mediaPlayerRecording.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                                        @Override
                                        public void onPrepared(MediaPlayer mp) {
                                            if(!bothPrepared){
                                                bothPrepared = true;
                                            }
                                            else if(bothPrepared){
                                                mediaPlayerAccompaniment.start();
                                                mediaPlayerRecording.start();
                                                bothPrepared = false;
                                            }
                                        }
                                    });

                                }catch(IOException e){
                                    e.printStackTrace();
                                }

                                //set up the accompaniment's audio
                                mediaPlayerAccompaniment = new MediaPlayer();
                                try{
                                    mediaPlayerAccompaniment.setDataSource(tempAccompaniment.toString());
                                    mediaPlayerAccompaniment.prepareAsync();

                                    mediaPlayerAccompaniment.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                                        @Override
                                        public void onPrepared(MediaPlayer mp) {
                                            if(!bothPrepared){
                                                bothPrepared = true;
                                            }
                                            else if(bothPrepared){
                                                mediaPlayerRecording.start();
                                                mediaPlayerAccompaniment.start();
                                                bothPrepared = false;
                                            }
                                        }
                                    });
                                }catch(IOException e){
                                    e.printStackTrace();
                                }
                            }
                        }, 1000);
                    }
                    else if(!isPlaying && lastPlayed == position){
                        isPlaying = true;
                        final Handler downloadDelay = new Handler();
                        downloadDelay.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                //set up the user's audio
                                mediaPlayerRecording = new MediaPlayer();
                                try{
                                    mediaPlayerRecording.setDataSource(tempRecording.toString());
                                    mediaPlayerRecording.prepareAsync();

                                    mediaPlayerRecording.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                                        @Override
                                        public void onPrepared(MediaPlayer mp) {
                                            if(!bothPrepared){
                                                bothPrepared = true;
                                            }
                                            else if(bothPrepared){
                                                mediaPlayerAccompaniment.start();
                                                mediaPlayerRecording.start();
                                                bothPrepared = false;
                                            }
                                        }
                                    });

                                }catch(IOException e){
                                    e.printStackTrace();
                                }

                                //set up the accompaniment's audio
                                mediaPlayerAccompaniment = new MediaPlayer();
                                try{
                                    mediaPlayerAccompaniment.setDataSource(tempAccompaniment.toString());
                                    mediaPlayerAccompaniment.prepareAsync();

                                    mediaPlayerAccompaniment.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                                        @Override
                                        public void onPrepared(MediaPlayer mp) {
                                            if(!bothPrepared){
                                                bothPrepared = true;
                                            }
                                            else if(bothPrepared){
                                                mediaPlayerRecording.start();
                                                mediaPlayerAccompaniment.start();
                                                bothPrepared = false;
                                            }
                                        }
                                    });
                                }catch(IOException e){
                                    e.printStackTrace();
                                }
                            }
                        }, 1000);
                    }
                }
            });
        }
    }

    //AWS S3
    AmazonS3Client s3;
    TransferUtility transferUtility;

    public void credentialsProvider(){
        //initialize the Amazon Cognito credentials provider
        CognitoCachingCredentialsProvider credentialsProvider = new CognitoCachingCredentialsProvider(
                context,
                "us-west-2:af04cf4e-54ef-4854-97aa-8613ba65e9a9", //identity pool ID
                Regions.US_WEST_2 // Region
        );
        setAmazonS3Client(credentialsProvider);
    }

    public void setAmazonS3Client(CognitoCachingCredentialsProvider credentialsProvider){
        //create an S3 client
        s3 = new AmazonS3Client(credentialsProvider);

        //set the region of your S3 bucket
        s3.setRegion(Region.getRegion(Regions.US_WEST_2));

    }

    public void setTransferUtility(){

        transferUtility = new TransferUtility(s3, context);
    }

    public void downloadFileFromS3(String s3_key, File destination){

        TransferObserver transferObserver = transferUtility.download(
                "vocality", /* The bucket to download from */
                s3_key,            /* The key for the object to download */
                destination        /* The file to download the object to */
        );
    }
}
