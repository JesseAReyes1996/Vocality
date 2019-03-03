package edu.jreye039.vocality;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class FeedAdapter extends RecyclerView.Adapter<FeedAdapter.FeedViewHolder> {

    private ArrayList<FeedItem> mFeedList;

    private OnItemClickListener mListener;

    public interface OnItemClickListener{
        void onPlayClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener){ mListener = listener; }

    public static class FeedViewHolder extends RecyclerView.ViewHolder {
        public TextView mCoverTitle;
        public TextView mUser;
        public TextView mLikes;
        public TextView mComments;

        public ImageView mLikeImage;
        public ImageView mCommentImage;
        public ImageView mPlayImage;

        public FeedViewHolder(@NonNull View itemView, final FeedAdapter.OnItemClickListener listener) {
            super(itemView);
            mCoverTitle = itemView.findViewById(R.id.coverTitleTextView);
            mUser = itemView.findViewById(R.id.coverAuthorTextView);
            mLikes = itemView.findViewById(R.id.likesTextView);
            mComments = itemView.findViewById(R.id.commentsTextView);

            mLikeImage = itemView.findViewById(R.id.likeBtn);
            mCommentImage = itemView.findViewById(R.id.commentBtn);
            mPlayImage = itemView.findViewById(R.id.playBtn);

            mPlayImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(listener != null){
                        int position = getAdapterPosition();
                        if(position != RecyclerView.NO_POSITION){
                            listener.onPlayClick(position);
                        }
                    }
                }
            });
        }
    }

    public FeedAdapter(ArrayList<FeedItem> feedList){
        mFeedList = feedList;
    }

    @NonNull
    @Override
    public FeedViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.feed_item, viewGroup, false);
        FeedViewHolder fvh = new FeedViewHolder(v, mListener);
        return fvh;
    }

    @Override
    public void onBindViewHolder(@NonNull FeedViewHolder feedViewHolder, int i) {
        FeedItem currentItem = mFeedList.get(i);

        //in the event that the title is too long and will overlap the buttons
        if(currentItem.getmCoverTitle().length() >= 25){
            feedViewHolder.mCoverTitle.setTextSize(16);
            if(currentItem.getmCoverTitle().length() >= 27){
                feedViewHolder.mCoverTitle.setTextSize(14);
            }
        }

        feedViewHolder.mCoverTitle.setText(currentItem.getmCoverTitle());
        feedViewHolder.mUser.setText("covered by " + currentItem.getmUsername());

        if(currentItem.getmLikes() == 0){
            feedViewHolder.mLikes.setVisibility(View.GONE);
        }
        else{
            feedViewHolder.mLikes.setText("Likes " + Integer.toString(currentItem.getmLikes()));
        }

        if(currentItem.getmComments() == 0){
            feedViewHolder.mComments.setVisibility(View.GONE);
        }
        else{
            feedViewHolder.mComments.setText("Comments " + Integer.toString(currentItem.getmComments()));
        }

    }

    @Override
    public int getItemCount() {
        return mFeedList.size();
    }
}
