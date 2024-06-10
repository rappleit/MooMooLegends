package com.csd.moomoolegends;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

public class dairy extends Fragment {
    TextView textView;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Initialize view
        View view =inflater.inflate(R.layout.weekly_dairy, container, false);

        // Assign variable
        textView=view.findViewById(R.id.text_view);

        // Get Title
        String sTitle=getArguments().getString("title");

        // Set title on text view
        textView.setText(sTitle);

        // return view
        return view;
    }
}
