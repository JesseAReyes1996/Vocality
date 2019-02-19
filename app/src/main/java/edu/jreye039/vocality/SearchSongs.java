package edu.jreye039.vocality;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class SearchSongs extends AppCompatActivity{

    EditText query;
    Button search;

    private RecyclerView songsRecyclerView;
    private RecyclerView.LayoutManager songsLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_songs);

        //for retrieving the user's query
        query = (EditText) findViewById(R.id.searchEditText);
        search = (Button) findViewById(R.id.searchBtn);

        //initialize the recycler view for the search results
        songsRecyclerView = findViewById(R.id.searchSongsRecyclerView);
        songsRecyclerView.setHasFixedSize(true);
        songsLayoutManager = new LinearLayoutManager(this);
        songsRecyclerView.setLayoutManager(songsLayoutManager);
    }

    public void search(View view){
        //the user's requested song
        String song = query.getText().toString().toLowerCase();

        if(!song.equals("")){
            //search the database for the user's requested song
            SearchSongsBackgroundWorker backgroundWorker = new SearchSongsBackgroundWorker(this, songsRecyclerView);
            backgroundWorker.execute(song);
        }
    }
}
