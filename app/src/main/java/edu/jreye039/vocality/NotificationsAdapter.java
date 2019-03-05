package edu.jreye039.vocality;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class NotificationsAdapter extends RecyclerView.Adapter<NotificationsAdapter.NotificationsViewHolder> {

    private ArrayList<NotificationsItem> mNotificationsList;

    public static class NotificationsViewHolder extends RecyclerView.ViewHolder{
        public ImageView mProfilePictureImageView;
        public TextView mNotificationsTextView;

        public NotificationsViewHolder(@NonNull View itemView) {
            super(itemView);
            mProfilePictureImageView = itemView.findViewById(R.id.profilepictureImageView);
            mNotificationsTextView = itemView.findViewById(R.id.notificationsTextView);
        }
    }

    public NotificationsAdapter(ArrayList<NotificationsItem> notificationsList){
        mNotificationsList = notificationsList;
    }

    @NonNull
    @Override
    public NotificationsViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.notifications_item, viewGroup, false);
        NotificationsViewHolder nvh = new NotificationsViewHolder(v);
        return nvh;
    }

    @Override
    public void onBindViewHolder(@NonNull NotificationsViewHolder notificationsViewHolder, int i) {
        NotificationsItem currentItem = mNotificationsList.get(i);

        notificationsViewHolder.mProfilePictureImageView.setImageResource(currentItem.getProfilePicture());
        notificationsViewHolder.mNotificationsTextView.setText(currentItem.getNotification());
    }

    @Override
    public int getItemCount() {
        return mNotificationsList.size();
    }
}
