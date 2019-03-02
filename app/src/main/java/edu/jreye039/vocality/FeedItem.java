package edu.jreye039.vocality;

import android.widget.ImageButton;
import android.widget.ImageView;

public class FeedItem {

    private String mCoverTitle;
    private String mUsername;
    private int mLikes;
    private int mComments;

    public FeedItem(String coverTitle, String username, int numLikes, int numComments){
        mCoverTitle = coverTitle;
        mUsername = username;
        mLikes = numLikes;
        mComments = numComments;
    }

    public String getmCoverTitle(){
        return mCoverTitle;
    }

    public String getmUsername(){ return mUsername; }

    public int getmLikes(){ return mLikes; }

    public int getmComments(){ return mComments; }
}
