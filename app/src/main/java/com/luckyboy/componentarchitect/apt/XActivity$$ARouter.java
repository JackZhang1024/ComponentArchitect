package com.luckyboy.componentarchitect.apt;

import com.luckyboy.componentarchitect.MainActivity;

public class XActivity$$ARouter {

    public static Class<?> findTargetClass(String path) {
        if (path.equalsIgnoreCase("/app/MainActivity")) {
            return MainActivity.class;
        }
        return null;
    }

}

