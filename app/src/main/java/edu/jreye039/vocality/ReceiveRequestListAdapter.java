package edu.jreye039.vocality;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

public class ReceiveRequestListAdapter extends BaseAdapter {
    LayoutInflater mInflater;
    String[] requestListUsername;
    String username;

    public ReceiveRequestListAdapter(Context c, String[] users, String user_name){
        requestListUsername = users;
        mInflater = (LayoutInflater) c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        username = user_name;
    }

    @Override
    public int getCount() {
        return requestListUsername.length;
    }

    @Override
    public Object getItem(int position) {
        return requestListUsername[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int i, View convertView, ViewGroup parent) {
        View v = mInflater.inflate(R.layout.receive_request_layout,null);
        Button acceptRequestBtn = (Button)v.findViewById(R.id.acceptRequestBtn);
        Button refuseRequestBtn = (Button)v.findViewById(R.id.refuseRequestBtn);
        TextView requestNameTextView = (TextView)v.findViewById(R.id.requestedNameTextView);

        requestNameTextView.setText(requestListUsername[i]);
        acceptRequestBtn.setOnClickListener(new View.OnClickListener() {
            private Context context;
            @Override
            public void onClick(View v) {
                RequestAcceptBackgroundWorker acceptBackgroundWorker = new RequestAcceptBackgroundWorker(this.context);
                acceptBackgroundWorker.execute(username,requestListUsername[i]);
            }
        });
        refuseRequestBtn.setOnClickListener(new View.OnClickListener() {
            private Context context;
            @Override
            public void onClick(View v) {
                RequestRefuseBackgroundWorker refuseBackgroundWorker = new RequestRefuseBackgroundWorker(this.context);
                refuseBackgroundWorker.execute(username,requestListUsername[i]);
                //openDialog();
            }
        });
        return v;
    }
}
