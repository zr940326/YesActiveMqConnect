package com.zhagnrong.yesactivemq.connect.ws_stomp;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.zhagnrong.yesactivemq.connect.R;

import org.java_websocket.WebSocket;

import java.util.ArrayList;
import java.util.List;

import rx.Subscriber;
import rx.functions.Action1;
import ua.naiksoftware.stomp.LifecycleEvent;
import ua.naiksoftware.stomp.Stomp;
import ua.naiksoftware.stomp.StompHeader;
import ua.naiksoftware.stomp.client.StompClient;
import ua.naiksoftware.stomp.client.StompMessage;

/**
 * Created by zhangrong on 2018/7/20.
 */

public class WsStompActivity extends Activity {

    private EditText cheat; //聊天输入框
    private Button send;//发送按钮
    private Button stop;//停止按钮
    private LinearLayout message; //放收到的消息

    private StompClient mStompClient;
    private String login = "zhangsan";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_wsstomp_layout);
        initViews();
    }

    private void initViews() {

        cheat = (EditText) findViewById(R.id.cheat);//聊天输入框
        send = (Button) findViewById(R.id.send);//发送按钮
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 向USERS.test  发送Json数据
                mStompClient.send("USERS.test", "{\"userId\":\"" + login + "\",\"message\":\"" + cheat.getText() + "\"}")
                        .subscribe(new Subscriber<Void>() {
                            @Override
                            public void onCompleted() {
                                toast("发送成功");
                            }

                            @Override
                            public void onError(Throwable e) {
                                e.printStackTrace();
                                toast("发送错误");
                            }

                            @Override
                            public void onNext(Void aVoid) {

                            }
                        });
            }
        });

        stop = findViewById(R.id.stop);
        stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mStompClient != null) {
                    mStompClient.disconnect();
                }
            }
        });

        message = (LinearLayout) findViewById(R.id.message);//放收到的消息

        createStompClient();    //创建stomp 客户端
        registerStompTopic();  // 接收USERS.test 路径发布的消息

    }

    //创建stomp 客户端
    private void createStompClient() {
        mStompClient = Stomp.over(WebSocket.class, "ws://192.168.3.137:61614");

        //设置登录 需要的身份验证
        List<StompHeader> headerList = new ArrayList<>();
        StompHeader loginHeader = new StompHeader("login", login);
        StompHeader passHeader = new StompHeader("passcode", "123456");
        headerList.add(loginHeader);
        headerList.add(passHeader);

        mStompClient.connect(headerList);
        Toast.makeText(WsStompActivity.this, "开始连接 192.168.1.121:61614", Toast.LENGTH_SHORT).show();
        mStompClient.lifecycle().subscribe(new Action1<LifecycleEvent>() {
            @Override
            public void call(LifecycleEvent lifecycleEvent) {
                switch (lifecycleEvent.getType()) {
                    case OPENED:
                        Log.i("xiaozhang", "Stomp connection opened");
                        toast("连接已开启");
                        break;

                    case ERROR:
                        Log.i("xiaozhang", "Stomp Error", lifecycleEvent.getException());
                        toast("连接出错");
                        break;
                    case CLOSED:
                        Log.i("xiaozhang", "Stomp connection closed");
                        toast("连接关闭");
                        break;
                }
            }
        });
    }

    // 接收USERS.test 路径发布的消息
    private void registerStompTopic() {
        mStompClient.topic("/queue/USERS.test").subscribe(new Action1<StompMessage>() {
            @Override
            public void call(StompMessage stompMessage) {
                Log.i("xiaozhang", "接收到的消息: " + stompMessage.getPayload());
                showMessage(stompMessage);
            }
        });
    }

    private void showMessage(final StompMessage stompMessage) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                TextView text = new TextView(WsStompActivity.this);
                text.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                text.setText(System.currentTimeMillis() + " body is --->" + stompMessage.getPayload());
                message.addView(text);
            }
        });
    }


    private void toast(final String message) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(WsStompActivity.this, message, Toast.LENGTH_SHORT).show();
            }
        });
    }
}

