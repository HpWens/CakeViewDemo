package com.github.ws.cakeviewdemo;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.SparseArray;

import com.github.ws.cakeviewdemo.bean.BaseMessage;
import com.github.ws.cakeviewdemo.widget.CakeView;

public class MainActivity extends AppCompatActivity {

    private CakeView cv;
    private SparseArray<BaseMessage> sparseArray;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        cv = (CakeView) findViewById(R.id.cv);

        sparseArray = new SparseArray<>();
        BaseMessage mes = new BaseMessage();
        mes.percent = 30;
        mes.content = "远程控制";
        mes.color = Color.parseColor("#0ff000");
        sparseArray.put(0, mes);
        mes = new BaseMessage();
        mes.percent = 50;
        mes.content = "流氓推送";
        mes.color = Color.parseColor("#ff00ff");
        sparseArray.put(1, mes);
        mes = new BaseMessage();
        mes.percent = 30;
        mes.content = "广告推送";
        mes.color = Color.parseColor("#fff000");
        sparseArray.put(2, mes);

        mes = new BaseMessage();
        mes.percent = 40;
        mes.content = "广告推送";
        mes.color = Color.parseColor("#ffff99");
        sparseArray.put(3, mes);

        mes = new BaseMessage();
        mes.percent = 20;
        mes.content = "广告推送";
        mes.color = Color.parseColor("#993399");
        sparseArray.put(4, mes);

        mes = new BaseMessage();
        mes.percent = 50;
        mes.content = "广告推送";
        mes.color = Color.parseColor("#ffff66");
        sparseArray.put(5, mes);

        mes.percent = 30;
        mes.content = "广告推送";
        mes.color = Color.parseColor("#0099ff");
        sparseArray.put(6, mes);



        cv.setCakeData(sparseArray);
    }
}
