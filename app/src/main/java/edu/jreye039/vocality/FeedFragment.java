package edu.jreye039.vocality;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class FeedFragment extends Fragment {

    private RecyclerView feedRecyclerView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_feed, container, false);

        feedRecyclerView = (RecyclerView) rootView.findViewById(R.id.feedRecyclerView);
        feedRecyclerView.setHasFixedSize(true);

        feedRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        SharedPreferences userInfo = this.getActivity().getSharedPreferences("userInfo", Context.MODE_PRIVATE);
        String username = userInfo.getString("username", "");

        //display the recordings in the RecyclerView
        FeedBackgroundWorker backgroundWorker = new FeedBackgroundWorker(getActivity(), feedRecyclerView);
        backgroundWorker.execute(username);

        return rootView;
    }
}
