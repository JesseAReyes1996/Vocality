package edu.jreye039.vocality;

public class CommentItem {
    private String mUsername;
    private String mContent;

    public CommentItem(String username, String content){
        mUsername = username;
        mContent = content;
    }

    public String getmUsername() {
        return mUsername;
    }

    public String getmContent() {
        return mContent;
    }
}
