package com.luckyboy.componentarchitect;

import com.luckyboy.common.RouterManager;
import com.luckyboy.common.base.BaseApplication;
import com.luckyboy.module.personal.Personal_MainActivity;
import com.luckyboy.module.product.Product_MainActivity;

public class LKApplication extends BaseApplication {

    @Override
    public void onCreate() {
        super.onCreate();
        // 管理维护比较麻烦 如果有200 500多个需要管理的 是要每个都要手写加上？
        // 利用注解的方式 将这些信息自动的添加到RouterManager中
        RouterManager.getInstance().addPath("app", "com.luckyboy.componentarchitect.MainActivity", MainActivity.class);
        RouterManager.getInstance().addPath("product", "com.luckyboy.module.product.Product_MainActivity", Product_MainActivity.class);
        RouterManager.getInstance().addPath("personal", "com.luckyboy.module.personal.Personal_MainActivity", Personal_MainActivity.class);
    }



}
