package com.luckyboy.module.order;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;

import com.luckyboy.annotation.ARouter;
import com.luckyboy.annotation.Parameter;
import com.luckyboy.common.base.BaseActivity;

@ARouter(group = "order", path = "/order/Order_MainActivity")
public class Order_MainActivity extends BaseActivity {

    private static final String TAG = "Order_MainActivity";

    @Parameter
    String name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.order_activity_main);
        Log.e(TAG, "onCreate: " + name);
        Order_MainActivity$$Parameter parameter = new Order_MainActivity$$Parameter();
        parameter.loadParameter(this);
        Log.e(TAG, "onCreate: "+name);
    }



}
