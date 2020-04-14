package com.luckyboy.common;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public class RouterManager {

    private static RouterManager instance;
    private Map<String, List<PathBean>> routeMap = new HashMap<>();

    private RouterManager() {

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

    public void addPath(String group, String path, Class clazz) {
        List<PathBean> pathBeans = routeMap.get(group);
        if (pathBeans == null) {
            pathBeans = new ArrayList<>();
            PathBean pathBean = new PathBean(group, path, clazz);
            pathBeans.add(pathBean);
            routeMap.put(group, pathBeans);
        } else {
            for (PathBean bean : pathBeans) {
                if (!bean.getPath().equals(path)) {
                    PathBean pathBean = new PathBean(group, path, clazz);
                    pathBeans.add(pathBean);
                }
            }
        }
    }


    public Class loadClass(String group, String path) {
        List<PathBean> pathBeans = routeMap.get(group);
        if (pathBeans!=null){
            for (PathBean bean : pathBeans) {
                if (bean.getPath().equalsIgnoreCase(path)) {
                    return bean.getClazz();
                }
            }
        }
        return null;
    }


}
