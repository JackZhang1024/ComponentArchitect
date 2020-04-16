package com.luckyboy.arouter_api;

import android.app.Activity;
import android.util.LruCache;

import androidx.annotation.NonNull;

import com.luckyboy.arouter_api.core.ParameterLoad;

/**
 * 参数Parameter 加载管理器
 */
public class ParameterManager {

    private static ParameterManager instance;

    // LRU 缓存 key:类名 value：参数Parameter加载接口
    private LruCache<String, ParameterLoad> cache;

    // APT 生成的获取参数类文件 后缀名
    private static final String FILE_SUFFIX_NAME = "$$Parameter";

    public static ParameterManager getInstance() {
        if (instance == null) {
            synchronized (ParameterManager.class) {
                if (instance == null) {
                    instance = new ParameterManager();
                }
            }
        }
        return instance;
    }

    private ParameterManager() {
        // 初始化 cache 并赋条目最大值
        cache = new LruCache<>(200);
    }

    public void loadParameter(@NonNull Activity activity) {
        String className = activity.getClass().getName();
        ParameterLoad iParameter = cache.get(className);
        try {
            if (iParameter == null) {
                Class<?> clazz = Class.forName(className + FILE_SUFFIX_NAME);
                iParameter = (ParameterLoad) clazz.newInstance();
                cache.put(className, iParameter);
            }
            iParameter.loadParameter(activity);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
