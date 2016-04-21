package com.github.ws.cakeviewdemo;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.github.ws.cakeviewdemo.bean.BaseMessage;
import com.github.ws.cakeviewdemo.widget.CakeView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private CakeView cv;
    private List<BaseMessage> mList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        cv = (CakeView) findViewById(R.id.cv);

       // cv.setStartAngle(60);

        //cv.setSpacingLineColor(Color.parseColor("#000000"));

        cv.setTextColor(Color.parseColor("#000000"));

        mList = new ArrayList<>();

        BaseMessage mes = new BaseMessage();
        mes.percent = 50;
        mes.content = "身高";
        mes.color = Color.parseColor("#0ff000");
        mList.add(mes);

        BaseMessage message = new BaseMessage();
        message.percent = 20;
        message.content = "体重";
        message.color = Color.parseColor("#fff000");
        mList.add(message);

        BaseMessage mes0 = new BaseMessage();
        mes0.percent = 30;
        mes0.content = "言谈举止";
        mes0.color = Color.parseColor("#ff00ff");
        mList.add(mes0);

        BaseMessage mes1 = new BaseMessage();
        mes1.percent = 40;
        mes1.content = "打扮穿着";
        mes1.color = Color.parseColor("#00ffff");
        mList.add(mes1);

        BaseMessage mes2 = new BaseMessage();
        mes2.percent = 30;
        mes2.content = "家庭背景";
        mes2.color = Color.parseColor("#0099ff");
        mList.add(mes2);

        BaseMessage mes3 = new BaseMessage();
        mes3.percent = 40;
        mes3.content = "学历";
        mes3.color = Color.parseColor("#ff0000");
        mList.add(mes3);

        cv.setCakeData(mList);
    }
}
