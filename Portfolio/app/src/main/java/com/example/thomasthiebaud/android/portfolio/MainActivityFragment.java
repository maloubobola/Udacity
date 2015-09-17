package com.example.thomasthiebaud.android.portfolio;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment implements View.OnClickListener {

    public MainActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        rootView.findViewById(R.id.spotify_streamer_button).setOnClickListener(this);
        rootView.findViewById(R.id.scores_app_button).setOnClickListener(this);
        rootView.findViewById(R.id.library_app_button).setOnClickListener(this);
        rootView.findViewById(R.id.build_it_bigger_button).setOnClickListener(this);
        rootView.findViewById(R.id.xyz_reader_button).setOnClickListener(this);
        rootView.findViewById(R.id.capstone_button).setOnClickListener(this);

        return rootView;
    }

    @Override
    public void onClick(View v) {
        String displayText = "This button will launch ";

        switch (v.getId()) {
            case R.id.spotify_streamer_button:
                displayText += "the spotify streamer app.";
                break;
            case R.id.scores_app_button:
                displayText += "the scores app.";
                break;
            case R.id.library_app_button:
                displayText += "the library app.";
                break;
            case R.id.build_it_bigger_button:
                displayText += "the \"build it bigger\" app.";
                break;
            case R.id.xyz_reader_button:
                displayText += "the xyz reader app.";
                break;
            case R.id.capstone_button:
                displayText += "my custom app.";
                break;
            default:
                displayText = "You succesfully clicked on an unknow button !";
                break;
        }

        Toast.makeText(getContext(),displayText,Toast.LENGTH_LONG).show();
    }
}
