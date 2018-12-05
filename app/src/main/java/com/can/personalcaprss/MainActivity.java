package com.can.personalcaprss;

import android.content.Context;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.content.res.AppCompatResources;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements IOnFragmentInteractionListener{

    WorkerFragment mWorkerFragment;
    FeedListFragment mFeedListFragment;
    FeedListItemFragment mFeedListItemFragment;
    IFeedTaskActionListener mFeedActionListener;
    private int FEED_LIST_FRAGMENT = 0;
    private int FEED_LIST_ITEM_FRAGMENT = 1;
    private int mFragment;
    private String mFeedItemLink;
    private ArrayList<RSSFeedItem> mFeedItems;

    private static final String TAG_WORKER_FRAGMENT = "worker_fragment";
    private static final String TAG_LIST_FRAGMENT = "list_fragment";
    private static final String TAG_LIST_ITEM_FRAGMENT = "list_item_fragment";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LinearLayout ll = new LinearLayout(this);
        ll.setOrientation(LinearLayout.VERTICAL);

        LinearLayout.LayoutParams params =
                new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT);

        boolean isTablet = getResources().getBoolean(R.bool.isTablet);

        mWorkerFragment = (WorkerFragment) getSupportFragmentManager().
                findFragmentByTag(TAG_WORKER_FRAGMENT);

        if(mWorkerFragment == null && isConnected()) {
            mWorkerFragment = WorkerFragment.newInstance(Constants.FEED_URL);
            getSupportFragmentManager().beginTransaction().
                    add(mWorkerFragment, TAG_WORKER_FRAGMENT).commit();
        }

        mFeedListFragment = FeedListFragment.newInstance(isTablet);

        mFeedActionListener = mFeedListFragment;

        FrameLayout fl = new FrameLayout(this);
        fl.setId(R.id.frameLayout);
        LinearLayout.LayoutParams frameParams =
                new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);
        ll.addView(fl, frameParams);

        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();

        fragmentTransaction.replace(R.id.frameLayout, mFeedListFragment, TAG_LIST_FRAGMENT).commit();

        setContentView(ll, params);
    }

    @SuppressWarnings("unchecked")
    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if(savedInstanceState != null) {
            //retain the feed list saved before the config change
            mFeedItems =
                    (ArrayList<RSSFeedItem>) savedInstanceState.
                            getSerializable(Constants.KEY_FEED_LIST);

            if(mFeedItems != null && mFeedActionListener != null) {
                mFeedActionListener.onListReady(mFeedItems);
            }

            mFragment = savedInstanceState.getInt(Constants.KEY_FEED_FRAGMENT_TYPE);

            //Show the list item if last fragment was item fragment.
            if(mFragment == FEED_LIST_ITEM_FRAGMENT) {
                mFeedItemLink = savedInstanceState.getString(Constants.KEY_FEED_LINK);
                showFeedItemFragment(mFeedItemLink);
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(!isConnected()) {
            onNetworkLoss();
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        Fragment f = getSupportFragmentManager().findFragmentById(R.id.frameLayout);
        if(f instanceof FeedListFragment) {
            MenuItem refresh = menu.add(0, R.id.menu_item, 1,
                    R.string.refresh_action_title);
            refresh.setIcon(AppCompatResources.getDrawable(this, R.drawable.icon_refresh));
            refresh.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection - refresh
        if(mFeedActionListener != null) mFeedActionListener.onRefresh();
        if(mWorkerFragment != null) mWorkerFragment.onRefresh();
        return true;
    }

    @Override
    public void onFragmentInteraction(String action, Bundle b) {
        switch (action)
        {
            case Constants.ACTION_FEED_ITEM_CLICK:
                if(!isConnected()) onNetworkLoss();
                if(b != null) {
                    mFeedItemLink = b.getString(Constants.KEY_FEED_LINK);
                    showFeedItemFragment(mFeedItemLink);
                }
        }
    }

    private void showFeedItemFragment(String link)
    {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        mFeedListItemFragment = FeedListItemFragment.newInstance(link);
        ft.replace(R.id.frameLayout, mFeedListItemFragment, TAG_LIST_ITEM_FRAGMENT).
                addToBackStack(null);
        ft.commit();
    }

    @Override
    public void onFeedTaskComplete(boolean isSuccess, ArrayList<RSSFeedItem> items) {
        if(mFeedActionListener != null) {
            if(isSuccess) {
                mFeedActionListener.onListReady(items);
                mFeedItems = items;
            }
            else mFeedActionListener.onFeedListError();
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mFeedActionListener = null;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putSerializable(Constants.KEY_FEED_LIST, mFeedItems);

        Fragment f = getSupportFragmentManager().findFragmentById(R.id.frameLayout);
        if(f instanceof FeedListFragment) {
            outState.putInt(Constants.KEY_FEED_FRAGMENT_TYPE, FEED_LIST_FRAGMENT);
        } else if(f instanceof FeedListItemFragment) {
            outState.putInt(Constants.KEY_FEED_FRAGMENT_TYPE, FEED_LIST_ITEM_FRAGMENT);
            outState.putString(Constants.KEY_FEED_LINK, mFeedItemLink);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        invalidateOptionsMenu();
    }

    @Override
    public void onAttachFragment(Fragment fragment) {
        super.onAttachFragment(fragment);
        invalidateOptionsMenu(); // remove the refresh action item when switched to the item fragment.
    }

    //Check network connectivity
    private boolean isConnected()
    {
        ConnectivityManager cm =
                (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        if(cm != null) {
            NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
            return activeNetwork != null &&
                    activeNetwork.isConnectedOrConnecting();
        }
        return false;
    }

    //Display a toast message on network loss, and quit the app.
    private void onNetworkLoss() {
        Toast.makeText(this,
                R.string.connectivity_error_toast_message, Toast.LENGTH_LONG).show();

        Handler handler = new Handler();
        Runnable r = new Runnable() {
            public void run() {
                finish();
            }
        };
        handler.postDelayed(r, 2000);
    }
}
