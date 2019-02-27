package edu.jreye039.vocality;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

public class FriendsListAdapter extends BaseAdapter {
    LayoutInflater mInflater;
    String[] friendsListUsername;

    public FriendsListAdapter(Context c, String[] users){
        friendsListUsername = users;
        mInflater = (LayoutInflater) c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return friendsListUsername.length;
    }

    @Override
    public Object getItem(int position) {
        return friendsListUsername[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int i, View convertView, ViewGroup parent) {
        View v = mInflater.inflate(R.layout.friends_list_layout,null);
        TextView friendsListName = (TextView)v.findViewById(R.id.friendsInvitationNameTextView);

        friendsListName.setText(friendsListUsername[i]);
        return v;
    }
}
