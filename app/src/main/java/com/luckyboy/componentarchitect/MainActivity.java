package com.luckyboy.componentarchitect;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

    }

    public void goToProduct(View view) {
        try {
            Class clazz = Class.forName("com.luckyboy.module.product.Product_MainActivity");
            Intent intent = new Intent(this, clazz);
            startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
