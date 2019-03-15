package edu.jreye039.vocality;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.view.View;
import android.widget.TextView;

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

public class RegisterBackgroundWorker extends AsyncTask<String, Void, String> {
    Context context;
    RegisterBackgroundWorker(Context ctx){
        context = ctx;
    }

    String username;

    @Override
    protected void onPreExecute() {}

    @Override
    protected String doInBackground(String... params) {
        //get username and password
        username = params[0];
        String password = params[1];

        try{
            //create a POST request to the given URL
            URL url = new URL("http://jesseareyes1996.hostingerapp.com/vocality_createAccount.php");
            //open the connection
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setRequestMethod("POST");
            httpURLConnection.setDoOutput(true);
            httpURLConnection.setDoInput(true);

            //initiate the POST request for username and password
            OutputStream outputStream = httpURLConnection.getOutputStream();
            BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
            String post_data = URLEncoder.encode("username", "UTF-8") + "=" + URLEncoder.encode(username, "UTF-8") + "&" + URLEncoder.encode("password", "UTF-8") + "=" + URLEncoder.encode(password, "UTF-8");
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
        if(result.equals("taken")){
            TextView usernameExistsTextView = (TextView) ((Activity) context).findViewById(R.id.usernameExistsTextView);
            usernameExistsTextView.setVisibility(View.VISIBLE);
        }
        else if(result.equals("created")){
            TextView usernameExistsTextView = (TextView) ((Activity) context).findViewById(R.id.usernameExistsTextView);
            usernameExistsTextView.setVisibility(View.INVISIBLE);

            //set the user's info
            SharedPreferences userInfo = context.getSharedPreferences("userInfo", Context.MODE_PRIVATE);
            SharedPreferences.Editor edit = userInfo.edit();
            edit.putString("username", username);
            edit.apply();

            //take the user to the main feed
            Intent startIntent = new Intent(context, Main.class);
            context.startActivity(startIntent);

            Activity finisher = (Activity) context;
            finisher.finish();
        }
    }
}
