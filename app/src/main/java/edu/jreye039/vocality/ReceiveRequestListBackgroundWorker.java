package edu.jreye039.vocality;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.widget.ListView;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLEncoder;

public class ReceiveRequestListBackgroundWorker extends AsyncTask<String, Void,String> {
    Context context;
    ReceiveRequestListBackgroundWorker(Context ctx){ context = ctx;}

    String username;

    @Override
    protected void onPreExecute() {}

    @Override
    protected String doInBackground(String... params) {
        username = params[0];

        try{
            URL url = new URL("http://jesseareyes1996.hostingerapp.com/vocality_request_list.php");
            //open the connection
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setRequestMethod("POST");
            httpURLConnection.setDoOutput(true);
            httpURLConnection.setDoInput(true);

            //initiate the POST request for username
            OutputStream outputStream = httpURLConnection.getOutputStream();
            BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
            String post_data = URLEncoder.encode("username", "UTF-8") + "=" + URLEncoder.encode(username, "UTF-8");

            //send the POST request to the server
            bufferedWriter.write(post_data);
            bufferedWriter.flush();
            bufferedWriter.close();
            outputStream.close();

            InputStream inputStream = httpURLConnection.getInputStream();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, "iso-8859-1"));
            String line = "";
            String result = "";
            while((line = bufferedReader.readLine()) != null ){
                result += line;
            }
            bufferedReader.close();
            inputStream.close();
            //close the connection
            httpURLConnection.disconnect();

            return result;
        } catch (ProtocolException e) {
            e.printStackTrace();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
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
        String[] a =result.split("<br>");
        ListView friendsInvitationListView = ((Activity)context).findViewById(R.id.friendsInvitationListView);
        ReceiveRequestListAdapter receiveRequestListAdapter = new ReceiveRequestListAdapter(this.context, a, username);
        friendsInvitationListView.setAdapter(receiveRequestListAdapter);
    }
}
