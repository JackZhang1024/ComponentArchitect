package com.luckyboy.compiler.utils;

import java.util.Map;
import java.util.Set;

public class EmptyUtils {

    public static boolean isEmpty(CharSequence data){
        return data ==null || data.length()==0;
    }

    public static boolean isEmpty(Map data){
        return data ==null || data.isEmpty();
    }

    public static boolean isEmpty(Set data){
        return data ==null || data.isEmpty();
    }
}
