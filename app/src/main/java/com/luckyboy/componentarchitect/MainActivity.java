package com.luckyboy.componentarchitect;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.luckyboy.annotation.ARouter;
import com.luckyboy.annotation.Parameter;
import com.luckyboy.annotation.model.RouterBean;
import com.luckyboy.arouter_api.core.ARouterLoadPath;
import com.luckyboy.componentarchitect.apt.ARouter$$Group$$order;
import java.util.Map;

@ARouter(group = "app", path = "/app/MainActivity")
public class MainActivity extends AppCompatActivity {

    @Parameter(name = "name")
    String name;
    @Parameter(name = "age")
    int age;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        name = getIntent().getStringExtra("name");
        age = getIntent().getIntExtra("age", 1);
    }

    public void goToProduct(View view) {
        try {
//            Class clazz = Class.forName("com.luckyboy.module.product.Product_MainActivity");
//            Intent intent = new Intent(this, clazz);
//            startActivity(intent);

//            Class clazz = RouterManager.getInstance().loadClass("product", "com.luckyboy.module.product.Product_MainActivity");
//            if (clazz == null) {
//                throw new RuntimeException("没有找到对应的类");
//            }
//            Intent intent = new Intent(this, clazz);
//
//            startActivity(intent);

            //MainActivity$$ARouter.findTargetClass()
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 思考？为什么一个模块需要一个组路由
    // 一个模块一个路由的好处就是不用一次性把所有的组路由都加载上来
    // 我需要什么就给我给什么就可以了 懒加载 提高性能
    public void startOrder(View view) {
        // 最终集成化模式 所有子模块APT自动生成的文件都会放在apk中
        ARouter$$Group$$order loadGroup = new ARouter$$Group$$order();
        Map<String, Class<? extends ARouterLoadPath>> groupMap = loadGroup.loadGroup();
        Class<? extends ARouterLoadPath> clazz = groupMap.get("order");
        try {
            ARouterLoadPath path = clazz.newInstance();
            Map<String, RouterBean> pathMap = path.loadPath();
            RouterBean routerBean = pathMap.get("/order/Order_MainActivity");
            if (routerBean != null) {
                Intent intent = new Intent(this, routerBean.getClazz());
                intent.putExtra("name", "我爱中国");
                startActivity(intent);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
