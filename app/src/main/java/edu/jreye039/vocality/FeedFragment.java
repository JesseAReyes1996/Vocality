package edu.jreye039.vocality;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

public class FeedFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_feed, container, false);
        // 1. get a reference to recyclerView
        RecyclerView recyclerView = (RecyclerView) rootView.findViewById(R.id.feedRecyclerView);
        recyclerView.setHasFixedSize(true);

        // 2. set layoutManger
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));//getActivity().getApplicationContext()

        // this is data for recycler view
        ArrayList<FeedItem> exampleList = new ArrayList<>();
        exampleList.add(new FeedItem("Car Man Driver Guy","cornflower"));
        exampleList.add(new FeedItem("H","creeper"));
        exampleList.add(new FeedItem("See Ya Later Boi","greg"));
        exampleList.add(new FeedItem("Upside Down Mowgu","jaja"));
        exampleList.add(new FeedItem("Faded","jesse"));
        exampleList.add(new FeedItem("It's My Destiny","lastfire"));
        exampleList.add(new FeedItem("Dancing Keyblade","lyn"));
        exampleList.add(new FeedItem("Knight Man","mark"));
        exampleList.add(new FeedItem("Teemo Time","mp3"));
        exampleList.add(new FeedItem("Self Improvement Shriek","nerve"));
        exampleList.add(new FeedItem("Plant Man Murder Beard","toastus"));


        // 3. create an adapter
        FeedAdapter mFeedAdapter = new FeedAdapter(exampleList); //TODO move this to a background worker eventually

        // 4. set adapter
        recyclerView.setAdapter(mFeedAdapter);

        return rootView;
    }
}
