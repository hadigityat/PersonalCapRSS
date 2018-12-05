package com.can.personalcaprss;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FeedListItemFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FeedListItemFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_FEED_URL = "url";

    // TODO: Rename and change types of parameters
    private String mUrl;

    WebView mWebView;

    public FeedListItemFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param url feed url to load.
     * @return A new instance of fragment FeedListItemFragment.
     */
    public static FeedListItemFragment newInstance(String url) {
        FeedListItemFragment fragment = new FeedListItemFragment();
        Bundle args = new Bundle();
        args.putString(ARG_FEED_URL, url);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            //Appending the extension for mobile rendering.
            mUrl = getArguments().getString(ARG_FEED_URL) + Constants.FEED_ITEM_URL_EXT;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        LinearLayout ll = new LinearLayout(inflater.getContext());
        LinearLayout.LayoutParams params =
                new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT);
        ll.setGravity(Gravity.CENTER);
        ll.setLayoutParams(params);
        ll.setOrientation(LinearLayout.VERTICAL);

        final ProgressBar progressBar = new ProgressBar(inflater.getContext());
        ll.addView(progressBar,  new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT));

        mWebView = new WebView(inflater.getContext());
        mWebView.setVisibility(View.GONE);
        mWebView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                super.onProgressChanged(view, newProgress);
                if(newProgress == 100) {
                    progressBar.setVisibility(View.GONE);
                    mWebView.setVisibility(View.VISIBLE);
                }

            }
        });

        ll.addView(mWebView, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));


        return ll;
    }

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    public void onResume() {
        super.onResume();
        mWebView.getSettings().setJavaScriptEnabled(true); //in order to view youtube vids on page
        mWebView.loadUrl(mUrl);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }
}
