package com.can.personalcaprss;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.content.res.AppCompatResources;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;


public class FeedListAdapter extends RecyclerView.Adapter implements View.OnClickListener{

    public static final String TAG_IMAGE_VIEW = "image";
    public static final String TAG_TITLE = "title";
    public static final String TAG_DESCRIPTION = "description";

    private int FEED_ITEM_TEXT_MARGIN = 10;
    private int TITLE_TEXT_SIZE = 15;
    private int DESC_TEXT_SIZE = 12;
    private int PADDING_10 = 10;
    private int NEWEST_ARTICLE_POS = 0;
    private int DOWNLOAD_IMAGE_WIDTH = 500;
    private int DOWNLOAD_IMAGE_HEIGHT = 400;
    private int DOWNLOAD_IMAGE_WIDTH_TOP_ARTICLE = 800;
    private int DOWNLOAD_IMAGE_HEIGHT_TOP_ARTICLE = 500;
    private int LAYOUT_HEIGHT_TOP_ARTICLE = 700;
    private int LAYOUT_HEIGHT_TABLET = 400;
    private int IMAGE_HEIGHT_TOP_ARTICLE_TABLET = 600;
    private int IMAGE_HEIGHT_TOP_ARTICLE = 500;
    private int IMAGE_HEIGHT_TABLET = 300;
    private int IMAGE_HEIGHT = 400;
    private int LAYOUT_HEIGHT = 550;

    private boolean mIsTablet;


    List<RSSFeedItem> mItems;
    Context mContext;
    IOnFragmentInteractionListener mListener;

    public FeedListAdapter(List<RSSFeedItem> itemList, Context c, boolean isTablet) {
        mItems = itemList;
        mContext = c;
        mIsTablet = isTablet;
        if (mContext instanceof IOnFragmentInteractionListener) {
            mListener = (IOnFragmentInteractionListener) mContext;
        } else {
            throw new RuntimeException(mContext.toString()
                    + " must implement OnFragmentInteractionListener");
        }    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        LinearLayout ll = new LinearLayout(mContext);
        ll.setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams layoutParams =
                new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT);
        ll.setLayoutParams(layoutParams);

        setRectBackground(ll);
        ll.setGravity(Gravity.CENTER_HORIZONTAL);

        ImageView imageView = new ImageView(mContext);
        imageView.setTag(TAG_IMAGE_VIEW);
        imageView.setImageResource(R.drawable.placeholder_image_logo);
        imageView.setPadding(PADDING_10, PADDING_10, PADDING_10, PADDING_10);
        LinearLayout.LayoutParams imageParams =
                new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT);

        ll.addView(imageView, imageParams);

        TextView title = new TextView(mContext);
        title.setTag(TAG_TITLE);
        title.setMaxLines(1);
        title.setEllipsize(TextUtils.TruncateAt.END);
        title.setPadding(0, 0, PADDING_10, 0);
        title.setTextColor(ResourcesCompat.getColor(mContext.getResources(),
                R.color.colorPrimary, mContext.getTheme()));
        title.setTypeface(title.getTypeface(), Typeface.BOLD);
        title.setTextSize(TypedValue.COMPLEX_UNIT_SP, TITLE_TEXT_SIZE);
        LinearLayout.LayoutParams titleParams =
                new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        titleParams.setMargins(FEED_ITEM_TEXT_MARGIN, 0, 0, FEED_ITEM_TEXT_MARGIN);
        ll.addView(title, titleParams);


        TextView desc = new TextView(mContext);
        desc.setTag(TAG_DESCRIPTION);
        desc.setMaxLines(2);
        desc.setPadding(0, 0, PADDING_10, 0);
        desc.setEllipsize(TextUtils.TruncateAt.END);
        LinearLayout.LayoutParams descParams =
                new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT);

        descParams.setMargins(FEED_ITEM_TEXT_MARGIN, 0, 0, 0);
        desc.setTextSize(TypedValue.COMPLEX_UNIT_SP, DESC_TEXT_SIZE);
        ll.addView(desc, descParams);

        return new FeedListViewHolder(ll);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
        FeedListViewHolder holder = (FeedListViewHolder) viewHolder;
        int layoutHeight;
        int imageHeight;
        if(i == NEWEST_ARTICLE_POS) {
            layoutHeight = LAYOUT_HEIGHT_TOP_ARTICLE;
            imageHeight = mIsTablet ? IMAGE_HEIGHT_TOP_ARTICLE_TABLET : IMAGE_HEIGHT_TOP_ARTICLE;
        }
        else {
            layoutHeight = mIsTablet ? LAYOUT_HEIGHT_TABLET : LAYOUT_HEIGHT;
            imageHeight = mIsTablet ? IMAGE_HEIGHT_TABLET : IMAGE_HEIGHT;
        }
        LinearLayout.LayoutParams layoutParams =
                new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, layoutHeight);
        holder.itemView.setLayoutParams(layoutParams);

        LinearLayout.LayoutParams imageParams =
                new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, imageHeight);
        holder.image.setLayoutParams(imageParams);

        RSSFeedItem item = mItems.get(i);

        int width = DOWNLOAD_IMAGE_WIDTH;
        int height = DOWNLOAD_IMAGE_HEIGHT;
        if(i == 0) {
            width = DOWNLOAD_IMAGE_WIDTH_TOP_ARTICLE;
            height = DOWNLOAD_IMAGE_HEIGHT_TOP_ARTICLE;
        }

        new DownloadImageTask(holder.image, width, height).execute(item.imageURL);

        if(i != NEWEST_ARTICLE_POS) holder.title.setMaxLines(2);
        holder.title.setText(item.title);
        holder.link = item.link;
        String desc;
        if(item.description.isEmpty()) {
            desc = RSSTextUtils.getFormattedDate(item.pubDate);
        }
        else{
            desc = RSSTextUtils.getFormattedDate(item.pubDate) + " – " +
                    RSSTextUtils.getHTMLFormattedText(item.description);
        }

        if(i == NEWEST_ARTICLE_POS) {
            holder.description.setVisibility(View.VISIBLE);
            holder.description.setText(desc);
        }
        else holder.description.setVisibility(View.GONE);

        holder.itemView.setTag(item.link);
        holder.itemView.setOnClickListener(this);
    }

    @Override
    public int getItemCount() {
        if(mItems != null) return mItems.size();
        return 0;
    }

    @Override
    public void onClick(View v) {
        //On feed item click, pass the link as a bundle to the listener.
        Bundle b = new Bundle();
        b.putString(Constants.KEY_FEED_LINK, v.getTag().toString());
        mListener.onFragmentInteraction(Constants.ACTION_FEED_ITEM_CLICK, b);
    }

    private void setRectBackground(View v) {
        GradientDrawable shape = new GradientDrawable();
        shape.setShape(GradientDrawable.RECTANGLE);
        shape.setCornerRadii(new float[] { 10, 10, 10, 10, 0, 0, 0, 0 });
        shape.setColor(ResourcesCompat.getColor(mContext.getResources(),
                R.color.colorBackground, mContext.getTheme()));
        shape.setStroke(1,
                ResourcesCompat.getColor(mContext.getResources(),
                        R.color.colorPrimaryDark, mContext.getTheme()));
        v.setBackground(shape);
    }
}

class FeedListViewHolder extends RecyclerView.ViewHolder
{
    public TextView title;
    public TextView description;
    public ImageView image;
    public String link;

    public FeedListViewHolder(@NonNull View itemView) {
        super(itemView);

        title = itemView.findViewWithTag(FeedListAdapter.TAG_TITLE);
        description = itemView.findViewWithTag(FeedListAdapter.TAG_DESCRIPTION);
        image = itemView.findViewWithTag(FeedListAdapter.TAG_IMAGE_VIEW);

    }
}
