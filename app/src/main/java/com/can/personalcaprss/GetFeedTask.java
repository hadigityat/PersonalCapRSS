package com.can.personalcaprss;

import android.os.AsyncTask;
import android.text.TextUtils;
import android.util.Log;
import android.util.Xml;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;

/**
 * This task fetches the RSS feed, parses the fields needed to display the items by constructing a
 * RSSFeedItem object for each feed item, and adding it to a list.
 */
class GetFeedTask extends AsyncTask<String, Void, Boolean> {

    private String TAG = "GetFeedTask";
    private ArrayList<RSSFeedItem> mFeedList;
    private IOnFragmentInteractionListener mListener;

    GetFeedTask(IOnFragmentInteractionListener listener) {
        mListener = listener;
    }

    @Override
    protected Boolean doInBackground(String... feedUrl) {
        if (TextUtils.isEmpty(feedUrl[0]))
            return false;

        try {

            URL url = new URL(feedUrl[0]);
            InputStream inputStream = url.openConnection().getInputStream();
            mFeedList = parseFeed(inputStream);
            return true;
        } catch (IOException e) {
            Log.e(TAG, "Error", e);
            mListener = null;
        } catch (XmlPullParserException e) {
            Log.e(TAG, "Error", e);
            mListener = null;
        }
        return false;
    }

    @Override
    protected void onPostExecute(Boolean success) {
        if(mListener != null) {
            mListener.onFeedTaskComplete(success, mFeedList);
        }
        mListener = null;
    }

    //Helper function to parse the RSS feed, construct RSSFeedItem objects, and make a list of them.
    private ArrayList<RSSFeedItem> parseFeed(InputStream inputStream) throws XmlPullParserException,
            IOException {
        String title = null;
        String link = null;
        String description = null;
        String imageURL = null;
        String pubDate = null;
        boolean isItem = false;
        ArrayList<RSSFeedItem> items = new ArrayList<>();

        try {
            XmlPullParser xmlPullParser = Xml.newPullParser();
            xmlPullParser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
            xmlPullParser.setInput(inputStream, null);

            xmlPullParser.nextTag();
            while (xmlPullParser.next() != XmlPullParser.END_DOCUMENT) {
                int eventType = xmlPullParser.getEventType();

                String name = xmlPullParser.getName();
                if(name == null)
                    continue;

                if(eventType == XmlPullParser.END_TAG) {
                    if(name.equalsIgnoreCase("item")) {
                        isItem = false;
                    }
                    continue;
                }

                if (eventType == XmlPullParser.START_TAG) {
                    if(name.equalsIgnoreCase("item")) {
                        isItem = true;
                        continue;
                    }
                }

                Log.d(TAG, "Parsing name: " + name);
                String result = "";
                if (xmlPullParser.next() == XmlPullParser.TEXT) {
                    result = xmlPullParser.getText();
                    xmlPullParser.nextTag();
                }

                if (name.equalsIgnoreCase("title")) {
                    title = result;
                } else if (name.equalsIgnoreCase("link")) {
                    link = result;
                } else if (name.equalsIgnoreCase("media:content")) {
                    imageURL = xmlPullParser.getAttributeValue(null, "url");
                } else if (name.equalsIgnoreCase("description")) {
                    description = result;
                } else if (name.equalsIgnoreCase("pubDate")) {
                    pubDate = result;
                }

                if (title != null && link != null && description != null && pubDate != null && imageURL != null) {
                    if (isItem) {
                        RSSFeedItem item = new RSSFeedItem(title, description, imageURL, link, pubDate);
                        items.add(item);
                    }

                    title = null;
                    link = null;
                    description = null;
                    imageURL = null;
                    pubDate = null;
                    isItem = false;
                }
            }

            return items;
        } finally {
            inputStream.close();
        }
    }
}