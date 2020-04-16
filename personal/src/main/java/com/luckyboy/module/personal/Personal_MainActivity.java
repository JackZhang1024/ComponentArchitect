package com.luckyboy.module.personal;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.luckyboy.annotation.ARouter;
import com.luckyboy.common.RouterManager;
import com.luckyboy.common.base.BaseActivity;

@ARouter(group = "personal", path = "/personal/Personal_MainActivity")
public class Personal_MainActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.personal_activity_main);
    }

    public void goToProduct(View view) {
        try {
//            Class clazz = Class.forName("com.luckyboy.module.product.Product_MainActivity");
//            Intent intent = new Intent(this, clazz);
//            startActivity(intent);

//            Class clazz = RouterManager.getInstance().loadClass("product", "com.luckyboy.module.product.Product_MainActivity");
//            Intent intent = new Intent(this, clazz);
//            startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void goToHome(View view) {
        try {
//            Class clazz = Class.forName("com.luckyboy.componentarchitect.MainActivity");
//            Intent intent = new Intent(this, clazz);
//            startActivity(intent);

//            Class clazz = RouterManager.getInstance().loadClass("app", "com.luckyboy.componentarchitect.MainActivity");
//            Intent intent = new Intent(this, clazz);
//            startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
