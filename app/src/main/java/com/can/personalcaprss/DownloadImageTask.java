package com.can.personalcaprss;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageView;

import java.io.InputStream;

/**
 * This task is responsible for downloading the feed item image.
 */
public class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
    ImageView image;
    int width;
    int height;
    public DownloadImageTask(ImageView imageView, int w, int h) {
        width = w;
        height = h;
        image = imageView;
    }

    protected Bitmap doInBackground(String... urls) {
        String url = urls[0];
        Bitmap bmp = null;
        try {
            bmp = ImageUtils.getScaledBitmapFromUrl(url, width, height);
        } catch (Exception e) {
            Log.e("Error", e.getMessage());
            e.printStackTrace();
        }
        return bmp;
    }
    protected void onPostExecute(Bitmap result) {
        if(image != null) {
            image.setImageBitmap(result);
        }
    }
}
