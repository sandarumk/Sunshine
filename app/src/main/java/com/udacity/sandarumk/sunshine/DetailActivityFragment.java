package com.udacity.sandarumk.sunshine;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

/**
 * A placeholder fragment containing a simple view.
 */
public class DetailActivityFragment extends Fragment {

    public DetailActivityFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (container== null){
            Toast.makeText(getContext(),"Container is null",Toast.LENGTH_SHORT).show();
        }
        Toast.makeText(getContext(),"Container is not null",Toast.LENGTH_SHORT).show();
        View rootview = inflater.inflate(R.layout.fragment_detail, container, false);
        TextView textView = (TextView) rootview.findViewById(R.id.text_view_detail_Weather);
        textView.setText("Modaya");
        //textView.setText(getActivity().getIntent().getExtras().getString(Intent.EXTRA_TEXT));
        return rootview;
    }
}
