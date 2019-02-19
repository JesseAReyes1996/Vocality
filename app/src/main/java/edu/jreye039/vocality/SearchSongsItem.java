package edu.jreye039.vocality;

public class SearchSongsItem {

    private String mSongTitle;
    private String mArtist;

    public SearchSongsItem(String title, String artist){
        mSongTitle = title;
        mArtist = artist;
    }

    public String getmSongTitle(){
        return mSongTitle;
    }

    public String getmArtist(){
        return mArtist;
    }
}
