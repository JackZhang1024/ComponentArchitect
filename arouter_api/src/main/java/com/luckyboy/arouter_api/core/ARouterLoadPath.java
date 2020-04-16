package com.luckyboy.arouter_api.core;


import com.luckyboy.annotation.model.RouterBean;

import java.util.Map;

/**
 *
 * 路由组Group 对应的详细Path加载数据接口
 * 比如： "app"分组有这些信息
 *
 * key: "/app/MainActivity" , value MainActivity 信息封装到Router对象中
 *
 * */
public interface ARouterLoadPath {

    Map<String, RouterBean> loadPath();

}
