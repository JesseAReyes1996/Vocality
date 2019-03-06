package edu.jreye039.vocality;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.net.UrlQuerySanitizer;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.util.Pair;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import org.apache.http.params.HttpParams;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

public class ProfileActivity extends AppCompatActivity implements View.OnClickListener{
    private static final int RESULT_LOAD_IMAGE = 1;

    ImageView imageToUpload, downloadedImage;
    Button bUploadImage, bDownloadImage;
    TextView nameTextView;
    private RecyclerView RecordRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        imageToUpload = (ImageView) findViewById(R.id.imageToUpload);
        //downloadedImage = (ImageView) findViewById(R.id.downloadedImage);

        bUploadImage = (Button) findViewById(R.id.bUploadImage);
        //bDownloadImage = (Button) findViewById(R.id.bDownloadImage);
        nameTextView = (TextView) findViewById(R.id.nameTextView);

        imageToUpload.setOnClickListener(this);
        bUploadImage.setOnClickListener(this);
        //bDownloadImage.setOnClickListener(this);

        SharedPreferences userInfo = getSharedPreferences("userInfo", Context.MODE_PRIVATE);
        String username = userInfo.getString("username", "");

        nameTextView.setText(username);

        RecordRecyclerView = (RecyclerView) findViewById(R.id.RecordRecyclerView);
        RecordRecyclerView.setHasFixedSize(true);

        RecordRecyclerView.setLayoutManager(new LinearLayoutManager(this));//getActivity().getApplicationContext()


        //display the recordings in the RecyclerView
        ProfileBackgroundWorker backgroundWorker = new ProfileBackgroundWorker(this, RecordRecyclerView);
        backgroundWorker.execute(username);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){

            case R.id.bUploadImage:
                Bitmap image = ((BitmapDrawable) imageToUpload.getDrawable()).getBitmap();

                break;
            //case R.id.bDownloadImage:

            //break;

            case R.id.imageToUpload:
                Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(galleryIntent, RESULT_LOAD_IMAGE);
                break;

        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && data != null) {
            Uri selectedImage = data.getData();
            imageToUpload.setImageURI(selectedImage);
        }
    }

    /*private class UploadImage extends AsyncTask<Void, Void, Void>{
        Bitmap image;
        String name;

        public UploadImage(Bitmap image, String name){
            this.image = image;
            this.name = name;
        }
        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
        }

        @Override
        protected Void doInBackground(Void... params) {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            image.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
            String encodedImage = Base64.encodeToString(byteArrayOutputStream.toByteArray(), Base64.DEFAULT);

            ArrayList<Pair<String, String>> dataToSend = new ArrayList<>();
            dataToSend.add(new Pair<String, String>("image", encodedImage));
            dataToSend.add(new Pair<String, String>("name", name));

            return null;
        }
    }

    private Htt getHttpRequestParams(){
        HttpParams httpRequestParams = new BasicHttpParams();
    }*/

}

