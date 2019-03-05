package edu.jreye039.vocality;

public class NotificationsItem {
    private int mProfilePictureImageView;
    private String mNotificationsTextView;

    public NotificationsItem(int profilePicture, String notification){
        mProfilePictureImageView = profilePicture;
        mNotificationsTextView = notification;
    }

    public int getProfilePicture(){
        return mProfilePictureImageView;
    }

    public String getNotification(){
        return mNotificationsTextView;
    }
}
