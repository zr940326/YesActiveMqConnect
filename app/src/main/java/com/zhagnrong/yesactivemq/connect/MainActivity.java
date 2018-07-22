package com.zhagnrong.yesactivemq.connect;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;

import com.zhagnrong.yesactivemq.connect.mqtt.MqttActivity;
import com.zhagnrong.yesactivemq.connect.ws_stomp.WsStompActivity;

/**
 * Created by zhangrong on 2018/7/21.
 */

public class MainActivity extends Activity {
    private Button b_wsStomp; //ws +stomp 通信按钮

    private Button b_Mqtt; //mqtt 通信按钮

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_main_layout);
        initViews();
    }

    private void initViews() {
        b_wsStomp = findViewById(R.id.b_wsStomp);
        b_wsStomp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //跳转到 ws+stomp通信页面
                startActivity(new Intent(MainActivity.this, WsStompActivity.class));
            }
        });

        b_Mqtt=findViewById(R.id.b_Mqtt);
        b_Mqtt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //跳转到 mqtt通信页面
                startActivity(new Intent(MainActivity.this, MqttActivity.class));
            }
        });


    }
}
