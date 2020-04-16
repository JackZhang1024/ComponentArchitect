package com.luckyboy.arouter_api;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;
import android.util.LruCache;

import com.luckyboy.annotation.model.RouterBean;
import com.luckyboy.arouter_api.core.ARouterLoadGroup;
import com.luckyboy.arouter_api.core.ARouterLoadPath;

public class RouterManager {

    // 路由组名
    private String group;
    // 路由路径
    private String path;

    private static RouterManager instance;

    // LRU 缓存 key: 组名 value：路由组加载接口
    private LruCache<String, ARouterLoadGroup> groupLruCache;
    // LRU 缓存 key: 类名 value：路由路径加载接口
    private LruCache<String, ARouterLoadPath> pathLruCache;
    // APT 生成类文件后缀名（包名拼接）
    private static final String GROUP_FILE_PREFIX_NAME = ".ARouter$$Group$$";

    private RouterManager() {
        groupLruCache = new LruCache<>(200);
        pathLruCache = new LruCache<>(200);
    }


    public static RouterManager getInstance() {
        if (instance == null) {
            synchronized (RouterManager.class) {
                if (instance == null) {
                    instance = new RouterManager();
                }
            }
        }
        return instance;
    }

    public BundleManager build(String path) {
        if (TextUtils.isEmpty(path) || !path.startsWith("/")) {
            throw new IllegalArgumentException("未按照规范配置 /app/MainActivity");
        }
        group = subFromPath2Group(path);
        this.path = path;
        return new BundleManager();
    }

    private String subFromPath2Group(String path) {
        // /MainActivity 只有一个 "/"
        if (path.lastIndexOf("/") == 0) {
            throw new IllegalArgumentException("未按照规范配置 /app/MainActivity");
        }
        // 从第一个 "/" 到 第二个 "/" 之间
        String finalGroup = path.substring(1, path.indexOf("/", 1));
        if (TextUtils.isEmpty(finalGroup)) {
            throw new IllegalArgumentException("未按照规范配置 /app/MainActivity");
        }
        return finalGroup;
    }


    private static final String TAG = "RouterManager";

    // 开始跳转
    //
    public Object navigation(Context context, BundleManager bundleManager, int code) {
        // ARouter$$Group$$order
        String packageName = BuildConfig.packageNameForAPT;
        String groupClassName = packageName + GROUP_FILE_PREFIX_NAME + group;
        Log.e(TAG, "navigation: groupClassName " + groupClassName);
        //  读取路由组Group 类文件 （缓存 懒加载）
        ARouterLoadGroup aRouterLoadGroup = groupLruCache.get(group);
        try {
            if (aRouterLoadGroup == null) {
                // 加载APT路由组Group类文件 ARouter$$Group$$order
                Class<?> clazz = Class.forName(groupClassName);
                // 初始化类文件
                aRouterLoadGroup = (ARouterLoadGroup) clazz.newInstance();
                groupLruCache.put(group, aRouterLoadGroup);
            }
            if (aRouterLoadGroup.loadGroup().isEmpty()) {
                throw new RuntimeException("路由表Group加载失败");
            }
            // 读取路由path路径类文件
            ARouterLoadPath aRouterLoadPath = pathLruCache.get(path);
            if (aRouterLoadPath == null) {
                // 通过组Group 加载接口 获取Path加载接口
                Class<? extends ARouterLoadPath> clazz = aRouterLoadGroup.loadGroup().get(group);
                // 初始化类文件
                if (clazz != null) {
                    aRouterLoadPath = clazz.newInstance();
                }
                if (aRouterLoadPath != null) {
                    pathLruCache.put(path, aRouterLoadPath);
                }
            }
            if (aRouterLoadPath != null) {
                if (aRouterLoadPath.loadPath().isEmpty()) {
                    throw new RuntimeException("路由表Path加载失败");
                }
                RouterBean routerBean = aRouterLoadPath.loadPath().get(path);
                if (routerBean != null) {
                    // 类型的判断
                    switch (routerBean.getType()) {
                        case ACTIVITY:
                            Intent intent = new Intent(context, routerBean.getClazz());
                            intent.putExtras(bundleManager.getBundle());
                            // 如果是 startActivityForResult -->setResult
                            if (bundleManager.isResult()) {
                                ((Activity) context).setResult(code, intent);
                                ((Activity) context).finish();
                            } else {
                                // 普通跳转
                                if (code > 0) {
                                    ((Activity) context).startActivityForResult(intent, code, bundleManager.getBundle());
                                } else {
                                    ((Activity) context).startActivity(intent, bundleManager.getBundle());
                                }
                            }
                            break;
                        case CALL:
                            // 返回Call接口实现类
                            return routerBean.getClazz().newInstance();
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
