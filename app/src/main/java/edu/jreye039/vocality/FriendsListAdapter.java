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
    List<String> friendsListUsername;

    public FriendsListAdapter(Context c, List<String> users){
        friendsListUsername = users;
        mInflater = (LayoutInflater) c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return friendsListUsername.size();
    }

    @Override
    public Object getItem(int position) {
        return friendsListUsername.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int i, View convertView, ViewGroup parent) {
        View v = mInflater.inflate(R.layout.friends_list_layout,null);
        TextView friendsListName=(TextView)v.findViewById(R.id.friendsInvitationNameTextView);

        friendsListName.setText(friendsListUsername.get(i));
        return v;
    }
}
