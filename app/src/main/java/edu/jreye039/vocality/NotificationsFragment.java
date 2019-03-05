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

import java.util.ArrayList;

public class NotificationsFragment extends Fragment {

    private RecyclerView notificationsRecyclerView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_notifications, container, false);

        notificationsRecyclerView = (RecyclerView) rootView.findViewById(R.id.notificationsRecyclerView);
        notificationsRecyclerView.setHasFixedSize(true);

        notificationsRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        ArrayList<NotificationsItem> items = new ArrayList<>();
        items.add(new NotificationsItem(R.drawable.ic_person_black_24dp,"I like your stuff"));
        items.add(new NotificationsItem(R.drawable.ic_person_black_24dp,"I like your stuff"));
        items.add(new NotificationsItem(R.drawable.ic_person_black_24dp,"I like your stuff"));
        /*SharedPreferences userInfo = this.getActivity().getSharedPreferences("userInfo", Context.MODE_PRIVATE);
        String username = userInfo.getString("username", "");

        //display the recordings in the RecyclerView
        NotificationsBackgroundWorker backgroundWorker = new NotificationsBackgroundWorker(getActivity(), notificationsRecyclerView);
        backgroundWorker.execute(username);*/

        notificationsRecyclerView.setAdapter(new NotificationsAdapter(items));

        return rootView;
    }
}
