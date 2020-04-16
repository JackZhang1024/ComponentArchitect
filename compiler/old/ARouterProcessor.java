package com.luckyboy.compiler;

import com.google.auto.service.AutoService;
import com.luckyboy.annotation.ARouter;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;

import java.io.IOException;
import java.io.Writer;
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
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import javax.tools.Diagnostic;

@AutoService(Processor.class)
@SupportedAnnotationTypes({"com.luckyboy.annotation.ARouter"})
@SupportedSourceVersion(SourceVersion.RELEASE_7)
@SupportedOptions("content")
public class ARouterProcessor extends AbstractProcessor {

    // 操作Element 工具类
    private Elements elementUtils;

    // type(类信息) 工具类
    private Types typeUtils;

    // 用来输出警告，错误等信息
    private Messager messager;

    // 文件生成器
    private Filer filer;

    // 初始化工作 文件生辰器
    @Override
    public synchronized void init(ProcessingEnvironment processingEnvironment) {
        super.init(processingEnvironment);
        elementUtils = processingEnvironment.getElementUtils();
        typeUtils = processingEnvironment.getTypeUtils();
        messager = processingEnvironment.getMessager();
        filer = processingEnvironment.getFiler();

        String content = processingEnvironment.getOptions().get("content");
        messager.printMessage(Diagnostic.Kind.WARNING, content);
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
        messager.printMessage(Diagnostic.Kind.WARNING, "set.isEmpty "+set.isEmpty());
        // 没有任何节点使用注解
        if (set.isEmpty()) {
            return false;
        }
        // 获取项目中所有使用了ARouter注解的节点
        Set<? extends Element> elements = roundEnvironment.getElementsAnnotatedWith(ARouter.class);
        // 遍历素有的类节点
        for (Element element : elements) {
            // 类节点之上 就是包节点
            String packageName = elementUtils.getPackageOf(element).getQualifiedName().toString();
            // 获取简单类名
            String className = element.getSimpleName().toString();
            messager.printMessage(Diagnostic.Kind.WARNING, "被注解的类名 2" + className);
            // 最终生成我们想要生成的类文件 如： MainActivity$$ARouter
            // 可以先写一个模拟的类 然后根据模拟的类来生成文件
            String finalClassName = className + "$$ARouter";

            ARouter aRouter =  element.getAnnotation(ARouter.class);
            try {
                MethodSpec methodSpec = MethodSpec.methodBuilder("findTargetClass")
                        .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                        .returns(Class.class)
                        .addParameter(String.class, "path")
                        .addStatement("return path.equals($S) ? $T.class:null", aRouter.path(),
                                ClassName.get((TypeElement) element))
                        .build();

                TypeSpec typeSpec = TypeSpec.classBuilder(finalClassName)
                        .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
                        .addMethod(methodSpec)
                        .build();

                JavaFile javaFile = JavaFile.builder(packageName, typeSpec)
                        .build();

                javaFile.writeTo(filer);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return true;
    }


}
