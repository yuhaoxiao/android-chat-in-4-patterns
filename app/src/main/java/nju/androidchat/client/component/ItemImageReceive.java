package nju.androidchat.client.component;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.StyleableRes;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.UUID;

import nju.androidchat.client.R;

public class ItemImageReceive extends LinearLayout {


    @StyleableRes
    int index0 = 0;

    private ImageView imageView;
    private Context context;
    private UUID messageId;
    private OnRecallMessageRequested onRecallMessageRequested;


    public ItemImageReceive(Context context, String url, UUID messageId) throws InterruptedException {
        super(context);
        this.context = context;
        inflate(context, R.layout.item_image_receive, this);
        this.imageView = findViewById(R.id.chat_item_content_image);
        this.messageId = messageId;
        setBitmap(url);

    }

    public void init(Context context) {

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

}
