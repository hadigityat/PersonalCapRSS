package com.can.personalcaprss;


import java.io.Serializable;

/**
 * This model object represents a RSS feed item.
 * It needs to be declared serializable in order to save the list of items in a bundle.
 */
public class RSSFeedItem implements Serializable {

    public String title;
    public String description;
    public String imageURL;
    public String link;
    public String pubDate;

    public RSSFeedItem(String title, String description, String imageURL,
                       String link, String pubDate) {
        this.title = title;
        this.link = link;
        this.description = description;
        this.imageURL = imageURL;
        this.pubDate = pubDate;
    }
}
