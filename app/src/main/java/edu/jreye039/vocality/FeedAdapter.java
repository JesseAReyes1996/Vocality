package edu.jreye039.vocality;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

public class FeedAdapter extends RecyclerView.Adapter<FeedAdapter.FeedViewHolder> {

    private ArrayList<FeedItem> mFeedList;

    public static class FeedViewHolder extends RecyclerView.ViewHolder {
        public TextView mCoverTitle;
        public TextView mUser;

        public FeedViewHolder(@NonNull View itemView) {
            super(itemView);
            mCoverTitle = itemView.findViewById(R.id.coverTitleTextView);
            mUser = itemView.findViewById(R.id.coverAuthorTextView);
        }
    }

    public FeedAdapter(ArrayList<FeedItem> feedList){
        mFeedList = feedList;
    }

    @NonNull
    @Override
    public FeedViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.feed_item, viewGroup, false);
        FeedViewHolder fvh = new FeedViewHolder(v);
        return fvh;
    }

    @Override
    public void onBindViewHolder(@NonNull FeedViewHolder feedViewHolder, int i) {
        FeedItem currentItem = mFeedList.get(i);

        //in the event that the title is too long and will overlap the buttons
        if(currentItem.getmCoverTitle().length() >= 25){
            feedViewHolder.mCoverTitle.setTextSize(18);
        }
        feedViewHolder.mCoverTitle.setText(currentItem.getmCoverTitle());
        feedViewHolder.mUser.setText("covered by " + currentItem.getmUsername());

    }

    @Override
    public int getItemCount() {
        return mFeedList.size();
    }
}
