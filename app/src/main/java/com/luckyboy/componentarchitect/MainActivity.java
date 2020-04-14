package com.luckyboy.componentarchitect;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.luckyboy.annotation.ARouter;
import com.luckyboy.common.RouterManager;

@ARouter(group = "app", path = "/app/MainActivity")
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void goToProduct(View view) {
        try {
//            Class clazz = Class.forName("com.luckyboy.module.product.Product_MainActivity");
//            Intent intent = new Intent(this, clazz);
//            startActivity(intent);

            Class clazz = RouterManager.getInstance().loadClass("product", "com.luckyboy.module.product.Product_MainActivity");
            if (clazz == null) {
                throw new RuntimeException("没有找到对应的类");
            }
            Intent intent = new Intent(this, clazz);

            startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }



}
