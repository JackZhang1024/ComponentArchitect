package com.luckyboy.arouter_api.core;


/**
 *
 * 参数Parameter 加载接口
 *
 * 目标对象.属性名 = getIntent().属性类型("注解值或属性名"); 完成赋值
 * */
public interface ParameterLoad {

    void loadParameter(Object target);

}
