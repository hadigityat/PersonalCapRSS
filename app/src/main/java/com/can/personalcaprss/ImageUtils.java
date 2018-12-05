package com.can.personalcaprss;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.IOException;
import java.net.URL;

/**
 * Utility class to reduce the size of loaded bitmaps in order to save memory.
 */
public class ImageUtils {

    public static Bitmap getScaledBitmapFromUrl(String imageUrl, int requiredWidth, int requiredHeight) throws IOException {
        URL url = new URL(imageUrl);
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeStream(url.openConnection().getInputStream(), null, options);
        options.inSampleSize = calculateInSampleSize(options, requiredWidth, requiredHeight);
        options.inJustDecodeBounds = false;

        Bitmap bm = BitmapFactory.decodeStream(url.openConnection().getInputStream(), null, options);
        return bm;
    }

    public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {
            if (width > height) {
                inSampleSize = Math.round((float) height / (float) reqHeight);
            } else {
                inSampleSize = Math.round((float) width / (float) reqWidth);
            }
        }
        return inSampleSize;
    }
}
