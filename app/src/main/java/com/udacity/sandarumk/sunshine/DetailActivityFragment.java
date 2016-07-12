package com.udacity.sandarumk.sunshine;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * A placeholder fragment containing a simple view.
 */
public class DetailActivityFragment extends Fragment {

    public DetailActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootview = inflater.inflate(R.layout.fragment_detail, container, false);
        TextView textView = (TextView) rootview.findViewById(R.id.text_view_detail_Weather);
        textView.setText("Modaya");
        //textView.setText(getActivity().getIntent().getExtras().getString(Intent.EXTRA_TEXT));
        return rootview;
    }
}
