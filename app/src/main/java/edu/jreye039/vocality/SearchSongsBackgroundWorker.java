package edu.jreye039.vocality;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;

import java.io.BufferedReader;
import java.io.BufferedWriter;
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

public class SearchSongsBackgroundWorker extends AsyncTask<String, Void, String> {
    Context context;

    private RecyclerView songsRecyclerView;
    private SearchSongsAdapter songsAdapter;

    private ArrayList<SearchSongsItem> songs = new ArrayList<>();

    SearchSongsBackgroundWorker(Context ctx, RecyclerView rv){
        context = ctx;
        songsRecyclerView = rv;
    }

    String songRequested;

    @Override
    protected void onPreExecute() {
        //clear the recycler view
        songsAdapter = new SearchSongsAdapter(songs);
        songsRecyclerView.setAdapter(songsAdapter);
    }

    @Override
    protected String doInBackground(String... params) {
        //the user's requested song
        songRequested = params[0];

        try{
            //create a POST request to the given URL
            URL url = new URL("http://jesseareyes1996.hostingerapp.com/vocality_search_songs.php");
            //open the connection
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setRequestMethod("POST");
            httpURLConnection.setDoOutput(true);
            httpURLConnection.setDoInput(true);

            //initiate the POST request for the user's requested song
            OutputStream outputStream = httpURLConnection.getOutputStream();
            BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
            String post_data = URLEncoder.encode("song", "UTF-8") + "=" + URLEncoder.encode(songRequested, "UTF-8");
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

        //contains the results with the format ['artist', 'title', 'aws_s3_link']
        final String[] rows;

        if(!result.equals("no results")){

            //empty the songs array
            songs = new ArrayList<>();

            rows = result.split("><");
            //iterate through the results
            for(String row : rows){
                String[] columns = row.split("<>");
                //add the title/artist
                songs.add(new SearchSongsItem(columns[1],columns[0]));
            }

            //update the recycler view with the new search results
            songsAdapter = new SearchSongsAdapter(songs);
            songsRecyclerView.setAdapter(songsAdapter);

            //set an OnClickListener on the displayed items
            songsAdapter.setOnItemClickListener(new SearchSongsAdapter.OnItemClickListener() {
                @Override
                public void onItemClick(int position) {
                    //picks the song the user clicked on
                    String chosenItem = rows[position];
                    String[] columns = chosenItem.split("<>");
                    //holds the AWS S3 link for the chosen song
                    String s3_key = columns[2];

                    //take the user to record their song
                    Intent startIntent = new Intent(context, NewRecording.class);
                    startIntent.putExtra("AWS_S3_KEY", s3_key);
                    context.startActivity(startIntent);

                    Activity thisActivity = (Activity) context;
                    thisActivity.finish();
                }
            });
        }
    }
}
