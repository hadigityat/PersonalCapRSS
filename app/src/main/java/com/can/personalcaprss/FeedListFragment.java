package com.can.personalcaprss;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FeedListFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FeedListFragment extends Fragment implements IFeedTaskActionListener{
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_IS_TABLET = "isTablet";
    private static final String ARG_IS_PORT = "isPortrait";
    private String TAG = "FeedListFragment";


    // TODO: Rename and change types of parameters
    private boolean mIsTablet;
    private ArrayList<RSSFeedItem> mFeedItems;

    RecyclerView mFeedListView;
    ProgressBar mProgressBar;
    FeedListAdapter mAdapter;

    public FeedListFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param isTablet device type.
     * @return A new instance of fragment FeedListFragment.
     */
    public static FeedListFragment newInstance(boolean isTablet) {
        FeedListFragment fragment = new FeedListFragment();
        Bundle args = new Bundle();
        args.putBoolean(ARG_IS_TABLET, isTablet);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mIsTablet = getArguments().getBoolean(ARG_IS_TABLET);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        LinearLayout ll = new LinearLayout(inflater.getContext());
        LinearLayout.LayoutParams params =
                new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT);
        ll.setLayoutParams(params);
        ll.setOrientation(LinearLayout.VERTICAL);


        mProgressBar = new ProgressBar(inflater.getContext());

        mFeedListView = new RecyclerView(inflater.getContext());

        int gridItemCount = mIsTablet ? 3 : 2;

        RecyclerView.LayoutManager layoutManager =
                new GridLayoutManager(inflater.getContext(), gridItemCount);

        ((GridLayoutManager) layoutManager).setSpanSizeLookup(new SpanSizeLookup(mIsTablet));

        mFeedListView.setLayoutManager(layoutManager);
        mFeedListView.setHasFixedSize(true);
        mFeedListView.setItemViewCacheSize(25);
        mFeedListView.setDrawingCacheEnabled(true);
        mFeedListView.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);

        ll.addView(mProgressBar,  new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT));

        ll.setGravity(Gravity.CENTER);

        ll.addView(mFeedListView, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));
        mFeedListView.setVisibility(View.GONE);

        if(mFeedItems != null) setAdapter(mFeedItems);

        return ll;
    }

    @Override
    public void onRefresh() {
        mProgressBar.setVisibility(View.VISIBLE);
        mFeedListView.setVisibility(View.GONE);
        mAdapter = null;
    }

    @Override
    public void onFeedListError() {
        mProgressBar.setVisibility(View.GONE);
        Toast.makeText(getActivity(), R.string.feed_error_toast_message, Toast.LENGTH_LONG).show();
        Log.e(TAG, "Error fetching list");
    }

    @Override
    public void onListReady(ArrayList<RSSFeedItem> items) {
        setAdapter(items);
    }

    private void setAdapter(ArrayList<RSSFeedItem> items)
    {
        mProgressBar.setVisibility(View.GONE);
        mFeedListView.setVisibility(View.VISIBLE);
        mAdapter = new FeedListAdapter(items, getActivity(), mIsTablet);
        mFeedListView.setAdapter(mAdapter);
        mFeedItems = items;
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
