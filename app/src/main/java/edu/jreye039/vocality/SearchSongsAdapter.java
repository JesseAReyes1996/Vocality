package edu.jreye039.vocality;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

public class SearchSongsAdapter extends RecyclerView.Adapter<SearchSongsAdapter.SearchSongsViewHolder> {

    private ArrayList<SearchSongsItem> mSearchSongsList;

    public static class SearchSongsViewHolder extends RecyclerView.ViewHolder {

        public TextView title;
        public TextView artist;


        public SearchSongsViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.titleTextView);
            artist = itemView.findViewById(R.id.artistTextView);
        }
    }

    public SearchSongsAdapter(ArrayList<SearchSongsItem> SearchSongsList){
        mSearchSongsList = SearchSongsList;
    }

    @NonNull
    @Override
    public SearchSongsViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.song_item, viewGroup, false);
        SearchSongsViewHolder svh = new SearchSongsViewHolder(v);
        return svh;
    }

    @Override
    public void onBindViewHolder(@NonNull SearchSongsViewHolder searchSongsViewHolder, int i) {
        SearchSongsItem currentItem = mSearchSongsList.get(i);

        searchSongsViewHolder.title.setText(currentItem.getmSongTitle());
        searchSongsViewHolder.artist.setText(currentItem.getmArtist());

    }

    @Override
    public int getItemCount() {
        return mSearchSongsList.size();
    }
}
