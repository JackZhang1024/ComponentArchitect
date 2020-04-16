package com.luckyboy.module.order;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.luckyboy.annotation.ARouter;
import com.luckyboy.annotation.Parameter;
import com.luckyboy.arouter_api.ParameterManager;
import com.luckyboy.arouter_api.RouterManager;
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
//        Order_MainActivity$$Parameter parameter = new Order_MainActivity$$Parameter();
//        parameter.loadParameter(this);

        ParameterManager.getInstance().loadParameter(this);
        Log.e(TAG, "onCreate: "+name);
    }


    public void goMain(View view){
        RouterManager.getInstance().build("/app/MainActivity")
                .withResultString("result", "I'am back From Order")
                .navigation(this);
    }

}
