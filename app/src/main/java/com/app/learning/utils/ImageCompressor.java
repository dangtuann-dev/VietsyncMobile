package com.app.learning.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.example.vietsyncmobile.R;

import java.io.ByteArrayOutputStream;

public class ImageCompressor {

    public static byte[] compressBitmap(Bitmap bitmap, int quality) {
        if (bitmap == null) return new byte[0];
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, quality, stream);
        return stream.toByteArray();
    }

    public static Bitmap decodeCompressedBitmap(byte[] compressedBytes) {
        if (compressedBytes == null || compressedBytes.length == 0) return null;
        return BitmapFactory.decodeByteArray(compressedBytes, 0, compressedBytes.length);
    }

    public static void loadImageOptimized(Context context, String imageUrl, ImageView imageView, int width, int height) {
        if (context == null || imageView == null) return;

        RequestOptions options = new RequestOptions()
                .override(width, height)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .placeholder(R.drawable.ic_logo_placeholder)
                .error(R.drawable.ic_logo_placeholder);

        Glide.with(context)
                .load(imageUrl)
                .apply(options)
                .thumbnail(0.1f) // Fast low-res thumbnail load
                .into(imageView);
    }
}
