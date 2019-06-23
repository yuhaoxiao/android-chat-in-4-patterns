package nju.androidchat.client.component;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.StyleableRes;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.UUID;

import lombok.Setter;
import nju.androidchat.client.R;

public class ItemImageSend extends LinearLayout implements View.OnLongClickListener {
    @StyleableRes
    int index0 = 0;

    private ImageView imageView;
    private Context context;
    private UUID messageId;
    @Setter private OnRecallMessageRequested onRecallMessageRequested;

    public ItemImageSend(Context context, String url, UUID messageId, OnRecallMessageRequested onRecallMessageRequested) throws InterruptedException {
        super(context);
        this.context = context;
        inflate(context, R.layout.item_image_send, this);
        this.imageView = findViewById(R.id.chat_item_content_image);
        this.messageId = messageId;
        this.onRecallMessageRequested = onRecallMessageRequested;

        this.setOnLongClickListener(this);
        setBitmap(url);
    }

    public Bitmap  getBitmap() {
        imageView.setDrawingCacheEnabled(true);
        Bitmap bitmap=imageView.getDrawingCache();
        imageView.setDrawingCacheEnabled(false);
        return bitmap;
    }

    public void setBitmap(String url) throws InterruptedException {
        Bitmap image=getBitmapFromURL(url);
        imageView.setImageBitmap(image);
    }


    public static Bitmap getBitmapFromURL(String src) throws InterruptedException {
        final HttpURLConnection[] connection = {null};
        final InputStream[] input = {null};
        Thread a=new Thread(){
            public void run(){
                try {
                    URL url = new URL(src);
                    connection[0] = (HttpURLConnection) url.openConnection();
                    connection[0].setDoInput(true);
                    connection[0].connect();
                    input[0] = connection[0].getInputStream();
                }catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        a.start();
        a.join();
        Bitmap myBitmap = BitmapFactory.decodeStream(input[0]);
        return myBitmap;
    }

    @Override
    public boolean onLongClick(View v) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage("确定要撤回这条消息吗？")
                .setPositiveButton("是", (dialog, which) -> {
                    if (onRecallMessageRequested != null) {
                        onRecallMessageRequested.onRecallMessageRequested(this.messageId);
                    }
                })
                .setNegativeButton("否", ((dialog, which) -> {
                }))
                .create()
                .show();

        return true;


    }

}
