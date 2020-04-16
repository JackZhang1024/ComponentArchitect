package com.luckyboy.compiler;

import com.google.auto.service.AutoService;
import com.luckyboy.annotation.ARouter;
import com.luckyboy.annotation.model.RouterBean;
import com.luckyboy.compiler.utils.Constants;
import com.luckyboy.compiler.utils.EmptyUtils;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;
import com.squareup.javapoet.WildcardTypeName;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedOptions;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import javax.tools.Diagnostic;

@AutoService(Processor.class)
@SupportedAnnotationTypes({Constants.AROUTER_ANNOTATION_TYPES})
@SupportedSourceVersion(SourceVersion.RELEASE_7)
@SupportedOptions({Constants.MODULE_NAME, Constants.APT_PACKAGE}) // 从gradle中传递过来的参数
public class ARouterProcessor extends AbstractProcessor {

    // 操作Element 工具类
    private Elements elementUtils;

    // type(类信息) 工具类
    private Types typeUtils;

    // 用来输出警告，错误等信息
    private Messager messager;

    // 文件生成器
    private Filer filer;

    // 模块名称
    private String moduleName;

    // 包名 用于存放APT生成的类文件
    private String packageNameForAPT;

    // 临时Map存储 用来存放路由组Group对应的详细Path类对象 生成路由路径类文件时遍历
    // key 组名 "app" value: "app组的路由路径 ARouter$$Path$$app.class"
    private Map<String, List<RouterBean>> tempPathMap = new HashMap<>();

    // 临时map存储 用来存储路由Group信息 生成路由组类文件时遍历
    // key: 组名 "app" value: "app"组的路由路径 "ARouter$$Path$$app.class"
    private Map<String, String> tempGroupMap = new HashMap<>();

    // 初始化工作 文件生辰器
    @Override
    public synchronized void init(ProcessingEnvironment processingEnvironment) {
        super.init(processingEnvironment);
        elementUtils = processingEnvironment.getElementUtils();
        typeUtils = processingEnvironment.getTypeUtils();
        messager = processingEnvironment.getMessager();
        filer = processingEnvironment.getFiler();

        // 通过processingEnvironment 去获取对应的参数
        Map<String, String> options = processingEnvironment.getOptions();
        if (!options.isEmpty()) {
            moduleName = options.get(Constants.MODULE_NAME);
            packageNameForAPT = options.get(Constants.APT_PACKAGE);
            messager.printMessage(Diagnostic.Kind.WARNING, "moduleName---> " + moduleName);
            messager.printMessage(Diagnostic.Kind.WARNING, "packageForAPT---> " + packageNameForAPT);
        }

        // 必传参数判空（乱码问题 添加JavaK控制台输出中文乱码）
        if (EmptyUtils.isEmpty(moduleName) || EmptyUtils.isEmpty(packageNameForAPT)) {
            throw new RuntimeException("注解处理器需要的moduleName或者packageName为空 请在对应的build.gradle配置参数");
        }
    }


    /**
     * 相当于main函数 开始处理注解
     * 注解处理器的核心方法 处理具体的注解 生成Java文件
     *
     * @param set              使用了支持处理注解的节点集合（类 上面写了注解）
     * @param roundEnvironment 当前或者是之前的运行环境 可以通过该对象查找到的注解
     * @return true 表示后续处理器不会再处理（已经处理完成）
     */
    @Override
    public boolean process(Set<? extends TypeElement> set, RoundEnvironment roundEnvironment) {
        if (!set.isEmpty()) {
            // 获取所有的被@ARouter 注解的元素集合
            Set<? extends Element> elements = roundEnvironment.getElementsAnnotatedWith(ARouter.class);
            if (!elements.isEmpty()) {
                try {
                    // 解析元素
                    parseElements(elements);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            return true;
        }
        return true;
    }

    // 解析所有被@ARouter注解的元素集合
    private void parseElements(Set<? extends Element> elements) throws Exception {
        // 通过Element 工具类 获取Activity 类型
        TypeElement activityType = elementUtils.getTypeElement(Constants.ACTIVITY);
        TypeElement callType = elementUtils.getTypeElement(Constants.CALL);
        // 获取Mirror 显示类的信息
        TypeMirror activityMirror = activityType.asType();
        TypeMirror callMirror = callType.asType();

        // 获取每个元素的类信息
        for (Element element : elements) {
            // 获取每个元素的类信息
            TypeMirror elementMirror = element.asType();
            messager.printMessage(Diagnostic.Kind.WARNING, "遍历的元素信息为： " + elementMirror.toString());

            // 获取每个类上的@ARouter的注解 对应的path值
            ARouter aRouter = element.getAnnotation(ARouter.class);
            String path = aRouter.path();
            // 路由详细信息 封装到实体类
            RouterBean bean = new RouterBean.Builder()
                    .setGroup(aRouter.group())
                    .setPath(aRouter.path())
                    .setElement(element)
                    .build();
            // 高级判断 说明ARouter注解 只能被用到类上，并且是规定的Activity
            if (typeUtils.isSubtype(elementMirror, activityMirror)) {
                bean.setType(RouterBean.Type.ACTIVITY);
            } else if (typeUtils.isSubtype(elementMirror, callMirror)) {
                bean.setType(RouterBean.Type.CALL);
            } else {
                throw new RuntimeException("@ARouter注解目前仅限用于Activity之上");
            }
            // 赋值临时的map存储以上信息 用来遍历时生成代码
            valueOfMap(bean);
        }


        // ARouterLoadPath ARouterLoadGroup 用来生成类文件时实现接口
        TypeElement groupLoadType = elementUtils.getTypeElement(Constants.AROUTE_GROUP);
        TypeElement pathLoadType = elementUtils.getTypeElement(Constants.AROUTE_PATH);

        // 1. 生成路由详细的Path类文件 如 ARouter$$Path$$Order
        createPathFile(pathLoadType);
        // 2. 生成路由组Group类文件(没有path文件 取不到) 如 ARouter$$Group$$Order
        createGroupFile(groupLoadType, pathLoadType);
    }

    private void createPathFile(TypeElement pathLoadType) throws IOException {
        if (EmptyUtils.isEmpty(tempPathMap)) {
            return;
        }

        // 方法返回的是Map<String, RouterBean>
        TypeName methodReturns = ParameterizedTypeName.get(ClassName.get(Map.class), ClassName.get(String.class), ClassName.get(RouterBean.class));

        // 遍历分组 每一个分组创一个路径类文件
        for (Map.Entry<String, List<RouterBean>> entry : tempPathMap.entrySet()) {
            // 方法体构造 public Map<String, RouterBean> loadPath(){
            MethodSpec.Builder methodBuilder = MethodSpec.methodBuilder(Constants.PATH_METHOD_NAME)
                    .addAnnotation(Override.class) // 重写注解
                    .addModifiers(Modifier.PUBLIC)
                    .returns(methodReturns);
            // 不循环部分  Map<String, RouterBean> pathMap = new HashMap<>();
            methodBuilder.addStatement("$T<$T, $T> $N = new $T<>()",
                    ClassName.get(Map.class),
                    ClassName.get(String.class),
                    ClassName.get(RouterBean.class),
                    Constants.PATH_PARAMETER_NAME,
                    HashMap.class
            );
            // 循环部分
            //pathMap.put("/order/Order_MainActivity", RouterBean.create(RouterBean.Type.ACTIVITY,
            //        Order_MainActivity.class, "/order/Order_MainActivity", "order"));
            //pathMap.put("/order/Order_ListActivity", RouterBean.create(RouterBean.Type.ACTIVITY,
            //        Order_MainActivity.class, "/order/Order_ListActivity", "order"));

            List<RouterBean> pathList = entry.getValue();
            for (RouterBean bean : pathList) {
                // $L 就是字面量
                methodBuilder.addStatement(
                        "$N.put($S, $T.create($T.$L,$T.class, $S, $S))",
                        Constants.PATH_PARAMETER_NAME, //pathMap
                        bean.getPath(), // "/app/MainActivity"
                        ClassName.get(RouterBean.class), // ""
                        ClassName.get(RouterBean.Type.class),
                        bean.getType(), // 枚举ACTIVITY
                        ClassName.get((TypeElement) bean.getElement()), // MainActivity.class
                        bean.getPath(),  // "/app/MainActivity"
                        bean.getGroup() // "app"
                );
            }
            // 遍历结束之后 最后返回 return pathMap;
            methodBuilder.addStatement("return $N", Constants.PATH_PARAMETER_NAME);

            // 生成文件 如 ARouter$$Path$$app
            String finalClassName = Constants.PATH_FILE_NAME + entry.getKey();
            messager.printMessage(Diagnostic.Kind.WARNING, "APT生成路由Path类文件为： " + packageNameForAPT + "." + finalClassName);

            JavaFile.builder(packageNameForAPT, // 包路径
                    TypeSpec.classBuilder(finalClassName) // 类名
                            .addSuperinterface(ClassName.get(pathLoadType)) // 实现接口
                            .addModifiers(Modifier.PUBLIC) // 类修饰符
                            .addMethod(methodBuilder.build()) // 方法的构建
                            .build())
                    .build()
                    .writeTo(filer); // 类构建完成

            tempGroupMap.put(entry.getKey(), finalClassName);
        }
    }


    /**
     * // 模拟ARouter路由器的组文件
     * public class ARouter$$Group$$order implements ARouterLoadGroup {
     *
     * @Override public Map<String, Class<? extends ARouterLoadPath>> loadGroup() {
     * Map<String, Class<? extends ARouterLoadPath>> groupMap = new HashMap<>();
     * groupMap.put("order", ARouter$$Path$$order.class);
     * return groupMap;
     * }
     * }
     */
    // 生成Group组对应的文件
    private void createGroupFile(TypeElement groupLoadType, TypeElement pathLoadType) throws Exception {
        if (EmptyUtils.isEmpty(tempPathMap) || EmptyUtils.isEmpty(tempGroupMap)) {
            return;
        }

        TypeName methodReturns = ParameterizedTypeName.get(
                ClassName.get(Map.class), // Map
                ClassName.get(String.class), // Map<String,
                // 第二个参数： Class<? extends ARouterLoadPath>
                // 某某Class 是否属于 ARouterLoadPath接口的实现类
                ParameterizedTypeName.get(
                        ClassName.get(Class.class),
                        WildcardTypeName.subtypeOf(ClassName.get(pathLoadType))
                )
        );

        // @Override public Map<String, Class<? extends ARouterLoadPath>> loadGroup() {
        MethodSpec.Builder methodBuilder = MethodSpec.methodBuilder(Constants.GROUP_METHOD_NAME)
                .addAnnotation(Override.class) // 重写注解
                .addModifiers(Modifier.PUBLIC)
                .returns(methodReturns);

        //Map<String, Class<? extends ARouterLoadPath>> groupMap = new HashMap<>();
        methodBuilder.addStatement("$T<$T, $T> $N = new $T<>()",
                ClassName.get(Map.class),
                ClassName.get(String.class),
                ParameterizedTypeName.get(
                        ClassName.get(Class.class),
                        WildcardTypeName.subtypeOf(ClassName.get(pathLoadType))),
                Constants.GROUP_PARAMETER_NAME,
                HashMap.class
        );
        messager.printMessage(Diagnostic.Kind.WARNING, " map.size() " + tempGroupMap.size());
        // 方法内容配置
        for (Map.Entry<String, String> entry : tempGroupMap.entrySet()) {
            //groupMap.put("order", ARouter$$Path$$order.class);
            methodBuilder.addStatement(
                    "$N.put($S, $T.class)",
                    Constants.GROUP_PARAMETER_NAME, //pathMap
                    entry.getKey(), // "app"
                    // 类文件在指定报名下
                    // 这块用的是具体的类
                    ClassName.get(packageNameForAPT, entry.getValue())
            );
        }
        // 遍历之后： return groupMap
        methodBuilder.addStatement("return $N", Constants.GROUP_PARAMETER_NAME);
        // 生成文件 如 ARouter$$Group$$app 一个模块下只能有一个
        String finalClassName = Constants.GROUP_FILE_NAME + moduleName;
        messager.printMessage(Diagnostic.Kind.WARNING, "APT生成路由Group类文件为： " + packageNameForAPT + "." + finalClassName);
        JavaFile.builder(packageNameForAPT, // 包路径
                TypeSpec.classBuilder(finalClassName) // 类名
                        .addSuperinterface(ClassName.get(groupLoadType)) // 实现接口
                        .addModifiers(Modifier.PUBLIC) // 类修饰符
                        .addMethod(methodBuilder.build()) // 方法的构建
                        .build())
                .build()
                .writeTo(filer); // 类构建完成
    }


    private void valueOfMap(RouterBean bean) {
        messager.printMessage(Diagnostic.Kind.WARNING, "RouterBen ----> " + bean.toString());
        if (checkRouterPath(bean)) {
            // 开始赋值
            List<RouterBean> routerBeans = tempPathMap.get(bean.getGroup());
            if (routerBeans == null || routerBeans.isEmpty()) {
                routerBeans = new ArrayList<>();
                routerBeans.add(bean);
                tempPathMap.put(bean.getGroup(), routerBeans);
            } else {
                // 如果存在这个组 就判断是否已经存放过对应的bean路径
                if (!routerBeans.contains(bean)) {
                    routerBeans.add(bean);
                }
            }
        } else {
            messager.printMessage(Diagnostic.Kind.WARNING, "@ARouter注解未按规范，如：/app/MainActivity");
        }
    }


    /**
     * 校验@ARouter 注解的值 如果group未填写就从必填项path中截取数据
     *
     * @param bean 路由详细信息 最终实体类封装
     */
    private boolean checkRouterPath(RouterBean bean) {
        String group = bean.getGroup();
        String path = bean.getPath();
        // @ARouter 注解的path值，必须要以 / 开头
        if (EmptyUtils.isEmpty(path) || !path.startsWith("/")) {
            messager.printMessage(Diagnostic.Kind.WARNING, "@ARouter注解未按规范，如：/app/MainActivity");
            return false;
        }
        // 比如开发者代码 path = "/MainActivity"
        if (path.lastIndexOf("/") == 0) {
            messager.printMessage(Diagnostic.Kind.WARNING, "@ARouter注解未按规范，如：/app/MainActivity");
            return false;
        }
        String finalGroup = path.substring(1, path.indexOf("/", 1));
        // /app/MainActivity/MainActivity
        if (finalGroup.contains("/")) {
            messager.printMessage(Diagnostic.Kind.WARNING, "@ARouter注解未按规范，如：/app/MainActivity");
            return false;
        }
        // @ARouter注解中group赋值有问题
        if (EmptyUtils.isEmpty(group) || !group.equals(moduleName)) {
            messager.printMessage(Diagnostic.Kind.WARNING, "@ARouter注解中的group值必须和当前子模块名相同");
            return false;
        } else {
            bean.setGroup(finalGroup);
        }
        return true;
    }


}
