package com.luckyboy.arouter_api.core;


import java.util.Map;

/**
 * 路由组Group对外提供加载数据接口
 * // 一对一 "app" "ARouter$$Group$app"
 * */
public interface ARouterLoadGroup {

    Map<String, Class<? extends ARouterLoadPath>> loadGroup();
}
