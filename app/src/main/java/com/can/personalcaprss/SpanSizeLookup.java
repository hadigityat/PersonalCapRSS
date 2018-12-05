package com.can.personalcaprss;

import android.support.v7.widget.GridLayoutManager;

/**
 * Custom span size look up class to determine the span of the first article, for tablet/phone cases.
 */
public class SpanSizeLookup extends GridLayoutManager.SpanSizeLookup {

    boolean mIsTablet;

    public SpanSizeLookup(boolean isTablet)
    {
        mIsTablet = isTablet;
    }
    @Override
    public int getSpanSize(int position) {
        if(position == 0) {
            if(mIsTablet) return 3;
            return 2;
        }
        return 1;
    }
}
