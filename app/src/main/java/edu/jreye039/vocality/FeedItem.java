package edu.jreye039.vocality;

import android.widget.ImageButton;
import android.widget.ImageView;

public class FeedItem {

    private String mCoverTitle;
    private String mUsername;

    public FeedItem(String coverTitle, String username){
        mCoverTitle = coverTitle;
        mUsername = username;
    }

    public String getmCoverTitle(){
        return mCoverTitle;
    }

    public String getmUsername(){
        return mUsername;
    }
}
