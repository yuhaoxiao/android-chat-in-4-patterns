package nju.androidchat.client.hw1;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import lombok.extern.java.Log;
import nju.androidchat.client.ClientMessage;
import nju.androidchat.client.R;
import nju.androidchat.client.Utils;
import nju.androidchat.client.component.ItemImageReceive;
import nju.androidchat.client.component.ItemImageSend;
import nju.androidchat.client.component.ItemTextReceive;
import nju.androidchat.client.component.ItemTextSend;
import nju.androidchat.client.component.OnRecallMessageRequested;

@Log
public class Hw1TalkActivity extends AppCompatActivity implements Mvp0Contract.View, TextView.OnEditorActionListener, OnRecallMessageRequested {
    private Mvp0Contract.Presenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Mvp0TalkModel mvp0TalkModel = new Mvp0TalkModel();

        // Create the presenter
        this.presenter = new Mvp0TalkPresenter(mvp0TalkModel, this, new ArrayList<>());
        mvp0TalkModel.setIMvp0TalkPresenter(this.presenter);
    }

    @Override
    public void onResume() {
        super.onResume();
        presenter.start();
    }

    @Override
    public void showMessageList(List<ClientMessage> messages) {
        runOnUiThread(() -> {
                    LinearLayout content = findViewById(R.id.chat_content);

                    // 删除所有已有的ItemText
                    content.removeAllViews();

                    // 增加ItemText
                    for (ClientMessage message : messages) {
                        String text = String.format("%s", message.getMessage());
                        String type= "((!\\[)(.*?)(\\][(])(.*?)([)]))";
                        Pattern p = Pattern.compile(type);
                        Matcher m = p.matcher(text);
                        if (m.find()) {
                            String url=m.group(5);
                            if (message.getSenderUsername().equals(this.presenter.getUsername())) {
                                try {
                                    content.addView(new ItemImageSend(this, url, message.getMessageId(), this));
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                            } else {
                                try {
                                    content.addView(new ItemImageReceive(this, url, message.getMessageId()));
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                            }
                        }

                        else{
                            if (message.getSenderUsername().equals(this.presenter.getUsername())) {
                                content.addView(new ItemTextSend(this, text, message.getMessageId(), this));
                            } else {
                                content.addView(new ItemTextReceive(this, text, message.getMessageId()));
                            }
                        }
                    }

                    Utils.scrollListToBottom(this);
                }
        );
    }

    @Override
    public void setPresenter(Mvp0Contract.Presenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (null != this.getCurrentFocus()) {
            return hideKeyboard();
        }
        return super.onTouchEvent(event);
    }

    private boolean hideKeyboard() {
        return Utils.hideKeyboard(this);
    }


    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        if (Utils.send(actionId, event)) {
            hideKeyboard();
            // 异步地让Controller处理事件
            sendText();
        }
        return false;
    }

    private void sendText() {
        EditText text = findViewById(R.id.et_content);
        AsyncTask.execute(() -> {
            this.presenter.sendMessage(text.getText().toString());
        });

    }

    public void onBtnSendClicked(View v) {
        hideKeyboard();
        sendText();
    }

    // 当用户长按消息，并选择撤回消息时做什么，MVP-0不实现
    @Override
    public void onRecallMessageRequested(UUID messageId) {

    }
}
