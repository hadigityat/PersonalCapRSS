package com.can.personalcaprss;

import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.util.Xml;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link WorkerFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class WorkerFragment extends Fragment {
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_FEED_URL = "url";

    private String mURL;

    private IOnFragmentInteractionListener mListener;

    public WorkerFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param url Feed URL to fetch RSS feed items.
     * @return A new instance of fragment WorkerFragment.
     */
    public static WorkerFragment newInstance(String url) {
        WorkerFragment fragment = new WorkerFragment();
        Bundle args = new Bundle();
        args.putString(ARG_FEED_URL, url);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mURL = getArguments().getString(ARG_FEED_URL);
        }

        // Retain this Fragment so that it will not be destroyed when an orientation
        // change happens
        setRetainInstance(true);

        new GetFeedTask(mListener).execute(mURL);
    }

    public void onRefresh() {
        new GetFeedTask(mListener).execute(mURL);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof IOnFragmentInteractionListener) {
            mListener = (IOnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }


}
