package com.luckyboy.compiler.utils;

public class Constants {

    // 路由注解处理器支持的注解类型
    public static final String AROUTER_ANNOTATION_TYPES = "com.luckyboy.annotation.ARouter";

    // 参数注解处理器支持的注解类型
    public static final String PARAMETER_ANNOTATION_TYPES = "com.luckyboy.annotation.Parameter";

    // 每个子模块的模块名
    public static final String MODULE_NAME = "moduleName";
    // 用户存放APT生成的文件
    public static final String APT_PACKAGE = "packageNameForAPT";


    // String 类全名
    public static final String STRING = "java.lang.String";

    // Activity 类全名
    public static final String ACTIVITY = "android.app.Activity";


    // 包名前缀封装
    public static final String PACKAGE_PREFIX = "com.luckyboy.arouter_api";

    public static final String ROUTER_MANAGER = "RouterManager";

    // 跨模块业务 回调接口
    public static final String CALL = PACKAGE_PREFIX+ ".core.Call";

    // 路由组Group 加载接口
    public static final String AROUTE_GROUP = PACKAGE_PREFIX + ".core.ARouterLoadGroup";
    // 路由组Group 对应的详细Path 加载接口
    public static final String AROUTE_PATH = PACKAGE_PREFIX + ".core.ARouterLoadPath";
    // 获取参数 加载接口
    public static final String PARAMETER_LOAD = PACKAGE_PREFIX + ".core.ParameterLoad";

    // 路由组Group 对应的详细path 方法名
    public static final String PATH_METHOD_NAME = "loadPath";

    // 路由组Group 对应的详细group 方法名
    public static final String GROUP_METHOD_NAME = "loadGroup";

    public static final String PARAMETER_NAME = "target";

    // 路由组Group 对应的详细group 方法名
    public static final String PARAMETER_METHOD_NAME = "loadParameter";

    // 路由组Group 对应的详细path 参数名
    public static final String PATH_PARAMETER_NAME = "pathMap";

    // 路由组Group 对应的详细group 参数名
    public static final String GROUP_PARAMETER_NAME = "groupMap";

    // APT 生成的路由组Group 对应的详细path 类文件名
    public static final String PATH_FILE_NAME = "ARouter$$Path$$";
    // APT 生成的路由组Group 对应的详细group 类文件名
    public static final String GROUP_FILE_NAME = "ARouter$$Group$$";
    // APT 生成的获取参数的类文件名
    public static final String PARAMETER_FILE_NAME = "$$Parameter";


}
