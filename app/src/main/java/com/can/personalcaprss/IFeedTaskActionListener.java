package com.can.personalcaprss;

import java.util.ArrayList;

/**
 * This interface is used to pass information about the feed task actions, such as starting the task,
 * and the resulting success or error.
 */
public interface IFeedTaskActionListener {

    void onRefresh();

    void onFeedListError();

    void onListReady(ArrayList<RSSFeedItem> items);
}
