package edu.jreye039.vocality;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.w3c.dom.Comment;

import java.util.ArrayList;

public class CommentsAdapter extends RecyclerView.Adapter<CommentsAdapter.CommentsViewHolder> {

    private ArrayList<CommentItem> mCommentsList;

    public static class CommentsViewHolder extends RecyclerView.ViewHolder {
        public TextView mUsername;
        public TextView mContent;

        public CommentsViewHolder(@NonNull View itemView) {
            super(itemView);
            mUsername = itemView.findViewById(R.id.usernameTextView);
            mContent = itemView.findViewById(R.id.contentTextView);
        }
    }

    public CommentsAdapter(ArrayList<CommentItem> commentsList){
        mCommentsList = commentsList;
    }

    @NonNull
    @Override
    public CommentsViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.comment_item, viewGroup, false);
        CommentsViewHolder cvh = new CommentsViewHolder(v);
        return cvh;
    }

    @Override
    public void onBindViewHolder(@NonNull CommentsViewHolder commentsViewHolder, int i) {
        CommentItem currentItem = mCommentsList.get(i);

        commentsViewHolder.mUsername.setText(currentItem.getmUsername());
        commentsViewHolder.mContent.setText(currentItem.getmContent());
    }

    @Override
    public int getItemCount() {
        return mCommentsList.size();
    }
}
