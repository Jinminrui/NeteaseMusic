package com.jmr.nuist.neteasemusic.Utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.youth.banner.loader.ImageLoader;

import java.util.concurrent.ExecutionException;

public class ImageLoaderUtil extends ImageLoader {
    @Override
    public void displayImage(Context context, Object path, ImageView imageView) {
        RoundedCorners roundedCorners= new RoundedCorners(16);
        RequestOptions options=RequestOptions.bitmapTransform(roundedCorners).override(300, 300);
        Glide.with(context.getApplicationContext())
                .load(path)
                .apply(options)
                .into(imageView);
    }

    Bitmap bitmap;
    public  Bitmap urlToBitmap(Context context,String url) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    bitmap = Glide.with(context).asBitmap().load(url).submit().get();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
        return bitmap;
    }
}
