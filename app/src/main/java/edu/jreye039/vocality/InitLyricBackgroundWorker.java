package edu.jreye039.vocality;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.animation.AnimationUtils;
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
import java.util.ArrayList;
import java.util.List;

public class InitLyricBackgroundWorker extends AsyncTask<String, Void, String> {
    Context context;
    String songname;
    public AsyncResponse delegate = null;

    private LyricProcess mLrcProcess;
    private List<LyricInfo> lrcList = new ArrayList<LyricInfo>();
    InitLyricBackgroundWorker(Context ctx){ context = ctx; }

    @Override
    protected String doInBackground(String... params) {
        songname = params[0];
        try{
            URL url = new URL("http://jesseareyes1996.hostingerapp.com/vocality_lyric_context.php");
            //open the connection
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setRequestMethod("POST");
            httpURLConnection.setDoOutput(true);
            httpURLConnection.setDoInput(true);

            //initiate the POST request for username
            OutputStream outputStream = httpURLConnection.getOutputStream();
            BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
            String post_data = URLEncoder.encode("songname", "UTF-8") + "=" + URLEncoder.encode(songname, "UTF-8");

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
    protected void onPostExecute(String result) {
        mLrcProcess = new LyricProcess();
        result = result.replace("[", "\n");
        result = result.replace("]", "@");
        mLrcProcess.readLRC(result);
        lrcList = mLrcProcess.getLrcList();
        delegate.processFinish(lrcList);
    }
}
