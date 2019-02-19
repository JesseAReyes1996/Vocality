package edu.jreye039.vocality;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

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
    private RecyclerView.Adapter songsAdapter;

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
        //get username and password
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

        if(!result.equals("no results")){
            //empty the songs array
            songs = new ArrayList<>();

            String[] rows = result.split("><");
            Log.d("ROWS", rows[0]);
            //iterate through the results
            for(String row : rows){
                String[] columns = row.split("<>");
                //add the title/artist
                songs.add(new SearchSongsItem(columns[1],columns[0]));
            }

            //update the recycler view with the new search results
            songsAdapter = new SearchSongsAdapter(songs);
            songsRecyclerView.setAdapter(songsAdapter);
        }
    }
}
