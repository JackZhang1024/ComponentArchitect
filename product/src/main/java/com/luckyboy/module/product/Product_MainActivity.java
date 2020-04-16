package com.luckyboy.module.product;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.luckyboy.annotation.ARouter;
import com.luckyboy.common.RouterManager;
import com.luckyboy.common.base.BaseActivity;

@ARouter(group = "product", path = "/product/Product_MainActivity")
public class Product_MainActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.product_activity_main);
    }

    public void goToPersonal(View view) {
        try {
//            Class clazz = Class.forName("com.luckyboy.module.personal.Personal_MainActivity");
//            Intent intent = new Intent(this, clazz);
//            startActivity(intent);

//            Class clazz = RouterManager.getInstance().loadClass("personal", "com.luckyboy.module.personal.Personal_MainActivity");
//            Intent intent = new Intent(this, clazz);
//            startActivity(intent);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void goToHome(View view){
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
