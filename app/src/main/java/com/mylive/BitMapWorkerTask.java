package com.mylive;

import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.widget.ImageView;

/**
 * Created by Sunny on 3/31/2017.
 */
class BitmapWorkerTask extends AsyncTask<Integer, Void, Bitmap> {

    // Decode image in background.
    @Override
    protected Bitmap doInBackground(Integer... params) {
  //      final Bitmap bitmap = decodeSampledBitmapFromResource(getResources(), params[0], 100, 100));
    //    addBitmapToMemoryCache(String.valueOf(params[0]), bitmap);
        return null;
    }

/*
    public void addBitmapToMemoryCache(String key, Bitmap bitmap) {
        if (getBitmapFromMemCache(key) == null) {
            mMemoryCache.put(key, bitmap);
        }
    }

    public Bitmap getBitmapFromMemCache(String key) {
        return mMemoryCache.get(key);
    }

    public void loadBitmap(int resId, ImageView imageView) {
        final String imageKey = String.valueOf(resId);

        final Bitmap bitmap = getBitmapFromMemCache(imageKey);
        if (bitmap != null) {
            eventImage.setImageBitmap(bitmap);
        } else {
            eventImage.setImageResource(R.drawable.image_placeholder);
            BitmapWorkerTask task = new BitmapWorkerTask(eventImage);
            task.execute(resId);
        }
    }
*/
}