package edu.jreye039.vocality;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

public class SearchFragment extends Fragment {

    private Button friendbtn;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ///
        View v = inflater.inflate(R.layout.fragment_search, container, false);
        Intent startIntent = new Intent(getActivity().getApplicationContext(), FriendsList.class);
        getActivity().startActivity(startIntent);

        /*friendbtn = (Button) v.findViewById(R.id.friendbtn);

        friendbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent startIntent = new Intent(getActivity(), FriendsList.class);
                getActivity().startActivity(startIntent);
            }
        });*/

        return v;
        ///
    }
}
