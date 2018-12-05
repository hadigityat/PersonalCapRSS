package com.can.personalcaprss;

import android.os.Bundle;

import java.util.ArrayList;

/**
 * This interface must be implemented by activities allow an interaction in any
 * fragment to be communicated to the activity and potentially other fragments contained in that
 * activity.
 */
public interface IOnFragmentInteractionListener {

    void onFragmentInteraction(String action, Bundle data);

    void onFeedTaskComplete(boolean isSuccess, ArrayList<RSSFeedItem> items);
}
