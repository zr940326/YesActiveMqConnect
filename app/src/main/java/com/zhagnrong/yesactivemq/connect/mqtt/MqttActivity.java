package com.zhagnrong.yesactivemq.connect.mqtt;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.lichfaker.log.Logger;
import com.zhagnrong.yesactivemq.connect.R;
import com.zhagnrong.yesactivemq.connect.ws_stomp.WsStompActivity;

import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

/**
 * Created by zhangrong on 2018/7/21.
 */

public class MqttActivity extends Activity {

    private EditText cheat; //聊天输入框
    private Button send;//发送按钮
    private Button stop;//停止按钮
    private LinearLayout message; //放收到的消息

    //mqtt
    public static final String URL = "tcp://192.168.3.137:1883";
    private String userName = "zhangsan";
    private String password = "123";
    private String clientId = "zhangsan";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_mqtt_layout);
        initViews();
    }

    private void initViews() {

        cheat = (EditText) findViewById(R.id.cheat);//聊天输入框
        send = (Button) findViewById(R.id.send);//发送按钮
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        MqttManager.getInstance().publish("USERS.test", 2, cheat.getText().toString().getBytes());
                    }
                }).start();
            }
        });

        stop = findViewById(R.id.stop);
        stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            MqttManager.getInstance().disConnect();
                        } catch (MqttException e) {

                        }
                    }
                }).start();
            }
        });

        message = (LinearLayout) findViewById(R.id.message);//放收到的消息

        //另开一个线程 ，进行mqtt 初始化和订阅操作
        new Thread(new Runnable() {
            @Override
            public void run() {
                initMqtt();
            }
        }).start();
    }

    private void initMqtt() {

        //mqtt 进行连接
        boolean isConnect = MqttManager.getInstance().creatConnect(URL, userName, password, clientId);
        Logger.d("isConnected: " + isConnect);
        if (isConnect) {
            toast("连接已开启");
            // 订阅 接收USERS.test 路径发布的消息
            MqttManager.getInstance().subscribe("USERS.test", 2);

        } else {//提示 连接失败的时候，检查一下网络权限 给了没有

            toast("连接出错");
        }


    }

    private void toast(final String message) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(MqttActivity.this, message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        //注册
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //取消注册
        EventBus.getDefault().isRegistered(this);
    }

    /**
     * 订阅接收到的消息
     * 这里的Event类型可以根据需要自定义, 这里只做基础的演示
     *
     * @param messageMqtt
     */
    @Subscribe
    public void onEvent(final MqttMessage messageMqtt) {
        Logger.d(message.toString());
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                TextView text = new TextView(MqttActivity.this);
                text.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                text.setText(System.currentTimeMillis() + " body is --->" + messageMqtt.toString());
                message.addView(text);
            }
        });

    }


}
