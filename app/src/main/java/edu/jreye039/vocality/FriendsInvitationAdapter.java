package edu.jreye039.vocality;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

public class FriendsInvitationAdapter extends BaseAdapter {
    LayoutInflater mInflater;
    List<String> friendsInvitationList;

    FriendsInvitationAdapter(Context c, List<String> username){
        friendsInvitationList = username;
        mInflater = (LayoutInflater) c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return friendsInvitationList.size();
    }

    @Override
    public Object getItem(int position) {
        return friendsInvitationList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = mInflater.inflate(R.layout.friends_invitation_layout,null);
        TextView friendsInvitationNameTextView = (TextView)v.findViewById(R.id.friendsInvitationNameTextView);

        friendsInvitationNameTextView.setText(friendsInvitationList.get(position));
        return v;
    }
}
