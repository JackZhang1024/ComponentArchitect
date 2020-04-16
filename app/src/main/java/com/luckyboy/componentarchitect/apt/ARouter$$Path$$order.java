package com.luckyboy.componentarchitect.apt;

import com.luckyboy.annotation.model.RouterBean;
import com.luckyboy.arouter_api.core.ARouterLoadPath;
import com.luckyboy.module.order.Order_MainActivity;

import java.util.HashMap;
import java.util.Map;


public class ARouter$$Path$$order implements ARouterLoadPath {

    @Override
    public Map<String, RouterBean> loadPath() {
        Map<String, RouterBean> pathMap = new HashMap<>();
        pathMap.put("/order/Order_MainActivity", RouterBean.create(RouterBean.Type.ACTIVITY,
                Order_MainActivity.class, "/order/Order_MainActivity", "order"));
        pathMap.put("/order/Order_ListActivity", RouterBean.create(RouterBean.Type.ACTIVITY,
                Order_MainActivity.class, "/order/Order_ListActivity", "order"));
        return pathMap;
    }


}
