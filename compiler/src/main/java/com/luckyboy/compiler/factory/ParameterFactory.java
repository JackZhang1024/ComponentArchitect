package com.luckyboy.compiler.factory;

import com.luckyboy.annotation.Parameter;
import com.luckyboy.compiler.utils.Constants;
import com.luckyboy.compiler.utils.EmptyUtils;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import javax.annotation.processing.Messager;
import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import javax.tools.Diagnostic;

public class ParameterFactory {

    // MainActivity t = (MainActivity)target;
    private static final String CONTENT = "$T t = ($T)target";

    // 方法体构建
    private MethodSpec.Builder methodBuilder;

    // Messager 用来报告错误 警告和其他提示信息
    private Messager messager;

    // type(类信息)工具类
    private Types typeUtils;

    // 类名： 如： MainActivity
    private ClassName className;


    private ParameterFactory(Builder builder) {
        this.messager = builder.messager;
        this.typeUtils = builder.typeUtils;
        this.className = builder.className;

        // 通过方法参数体构建方法体 public void loadParameter(Object target){
        methodBuilder = MethodSpec.methodBuilder(Constants.PARAMETER_METHOD_NAME)
                .addAnnotation(Override.class)
                .addModifiers(Modifier.PUBLIC)
                .addParameter(builder.parameterSpec);
    }

    public void addFirstStatement() {
        methodBuilder.addStatement(CONTENT, className, className);
    }

    public MethodSpec build() {
        return methodBuilder.build();
    }

    /**
     * 构建方法内容
     * 如：t.s = getIntent.getStringExtra("s);
     *
     * @param element 被注解的属性元素
     */
    public void buildStatement(Element element) {
        // 遍历注解的属性节点 生成函数体
        TypeMirror typeMirror = element.asType();
        // 获取TypeKind的序列号
        int type = typeMirror.getKind().ordinal();
        // 获取属性名称
        String filedName = element.getSimpleName().toString();
        // 获取注解的value
        String annotationValue = element.getAnnotation(Parameter.class).name();
        // 如果的注解的value为空 则用属性名称
        // annotationValue最终就是Intent的中参数名
        annotationValue = EmptyUtils.isEmpty(annotationValue) ? filedName : annotationValue;
        // 最终拼接的前缀
        String finalValue = "t." + filedName;
        // t.s = t.getIntent().
        String methodContent = finalValue + " = t.getIntent().";

        // TypeKind 枚举类型不包括String
        if (type == TypeKind.INT.ordinal()) {
            // t.s = t.getIntent.getIntExtra("age", t.age);
            methodContent += "getIntExtra($S," + finalValue + ")";
        } else if (type == TypeKind.BOOLEAN.ordinal()) {
            // t.s = t.getIntent.getBooleanExtra("age", t.age);
            methodContent += "getBooleanExtra($S," + finalValue + ")";
        } else {
            // t.s = t.getStringExtra("S");
            if (typeMirror.toString().equalsIgnoreCase(Constants.STRING)) {
                methodContent += "getStringExtra($S)";
            }
        }
        if (methodContent.endsWith(")")) {
            // t.s = t.getIntent.getIntExtra($S, t.age)
            // annotationValue替换$S $S为Intent中的参数
            //messager.printMessage(Diagnostic.Kind.WARNING, "methodContent "+methodContent);
            methodBuilder.addStatement(methodContent, annotationValue);
        } else {
            messager.printMessage(Diagnostic.Kind.WARNING, "目前暂支持String, int, boolean 传参");
        }

    }


    public static class Builder {
        // Messager 用来报告错误 警告和其他提示信息
        private Messager messager;

        // type(类信息)工具类
        private Types typeUtils;

        // 类名： 如： MainActivity
        private ClassName className;

        private Elements elementUtils;

        // 方法参数体
        private ParameterSpec parameterSpec;

        public Builder(ParameterSpec parameterSpec) {
            this.parameterSpec = parameterSpec;
        }


        public Builder setMessager(Messager messager) {
            this.messager = messager;
            return this;
        }

        public Builder setTypeUtils(Types typeUtils) {
            this.typeUtils = typeUtils;
            return this;
        }

        public Builder setElementUtils(Elements elementUtils) {
            this.elementUtils = elementUtils;
            return this;
        }

        public Builder setClassName(ClassName className) {
            this.className = className;
            return this;
        }

        public ParameterFactory build() {
            if (parameterSpec == null) {
                throw new IllegalArgumentException("parameterSpec 方法参数不能为空");
            }
            if (className == null) {
                throw new IllegalArgumentException("方法内容中的className不能为空");
            }
            if (messager == null) {
                throw new IllegalArgumentException("Messager不能为空");
            }
            return new ParameterFactory(this);
        }


    }


}
