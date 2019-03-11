package edu.jreye039.vocality;

import android.content.Context;
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

public class DisplayCommentsBackgroundWorker extends AsyncTask<String, Void, String> {

    Context context;

    private RecyclerView commentsRecyclerView;
    private CommentsAdapter commentsAdapter;

    private ArrayList<CommentItem> commentsList = new ArrayList<>();

    DisplayCommentsBackgroundWorker(Context ctx, RecyclerView rv){
        context = ctx;
        commentsRecyclerView = rv;
    }

    String postID;

    @Override
    protected void onPreExecute() {}

    @Override
    protected String doInBackground(String... params) {

        postID = params[0];

        try{
            //create a POST request to the given URL
            URL url = new URL("http://jesseareyes1996.hostingerapp.com/vocality_display_comments.php");
            //open the connection
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setRequestMethod("POST");
            httpURLConnection.setDoOutput(true);
            httpURLConnection.setDoInput(true);

            //initiate the POST request for username and password
            OutputStream outputStream = httpURLConnection.getOutputStream();
            BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
            String post_data = URLEncoder.encode("postid", "UTF-8") + "=" + URLEncoder.encode(postID, "UTF-8");
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
        //contains the results with the format [title, comments, id]
        final String[] rows;

        if(!result.equals("no results")){
            //empty the recordings array
            commentsList = new ArrayList<>();

            rows = result.split("><");
            //iterate through the comments
            for(String row : rows){
                String[] columns = row.split("<>");
                //add each comment with user's username
                commentsList.add(new CommentItem(columns[0],columns[1]));
            }

            //update the recycler view with the post's comments
            commentsAdapter = new CommentsAdapter(commentsList);
            commentsRecyclerView.setAdapter(commentsAdapter);
        }
    }
}
