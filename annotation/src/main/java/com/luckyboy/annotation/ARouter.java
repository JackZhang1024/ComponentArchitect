package com.luckyboy.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.CLASS)
public @interface ARouter {

    // 详细路由路径 如 "/app/MainActivity"
    String path();

    // 可以不写 可以从path中截取出来 规范开发者开发
    String group() default "";

}
