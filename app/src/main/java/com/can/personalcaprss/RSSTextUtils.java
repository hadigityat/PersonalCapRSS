package com.can.personalcaprss;

import android.os.Build;
import android.text.Html;

/**
 * Utility class to manipulate strings.
 */
public class RSSTextUtils {

    public static String getFormattedDate(String date) {
        return date.substring(0,16);
    }

    public static String getHTMLFormattedText(String text) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            return Html.fromHtml(text, Html.FROM_HTML_MODE_COMPACT).toString();
        } else {
            return Html.fromHtml(text).toString();
        }
    }
}
