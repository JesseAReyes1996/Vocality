package edu.jreye039.vocality;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class NewRecordingFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_new_recording, container, false);

        //start new recording activity
        Intent startIntent = new Intent(getActivity().getApplicationContext(), NewRecording.class);
        getActivity().startActivity(startIntent);

        return v;
    }
}
