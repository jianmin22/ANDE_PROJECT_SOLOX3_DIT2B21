package com.example.solox3_dit2b21;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

public class ReadingFragment extends Fragment {

    private static final String ARG_TEXT = "page_text";

    public static ReadingFragment newInstance(String text) {
        ReadingFragment fragment = new ReadingFragment();
        Bundle args = new Bundle();
        args.putString(ARG_TEXT, text);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_page, container, false);
        TextView textView = view.findViewById(R.id.pageContent);
        String text = getArguments().getString(ARG_TEXT);
        textView.setText(text);
        return view;
    }
}
