package com.luckyboy.compiler;

import com.google.auto.service.AutoService;
import com.luckyboy.annotation.Parameter;
import com.luckyboy.compiler.factory.ParameterFactory;
import com.luckyboy.compiler.utils.Constants;
import com.luckyboy.compiler.utils.EmptyUtils;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;
import com.squareup.javapoet.WildcardTypeName;

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
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import javax.tools.Diagnostic;

@AutoService(Processor.class)
@SupportedAnnotationTypes({Constants.PARAMETER_ANNOTATION_TYPES})
@SupportedSourceVersion(SourceVersion.RELEASE_7)
public class ParameterProcessor extends AbstractProcessor {

    // 操作Element 工具类
    private Elements elementUtils;

    // type(类信息) 工具类
    private Types typeUtils;

    // 用来输出警告，错误等信息
    private Messager messager;

    // 文件生成器
    private Filer filer;

    // 临时Map存储 用来存储被@Parameter注解的属性集合 生成类文件时遍历
    // Key: 节点类 value：被@Parameter注解的所有属性集合
    // TypeElement Activity
    // Element @Parameter name
    private Map<TypeElement, List<Element>> tempParameterMap = new HashMap<>();

    @Override
    public synchronized void init(ProcessingEnvironment processingEnvironment) {
        super.init(processingEnvironment);
        elementUtils = processingEnvironment.getElementUtils();
        typeUtils = processingEnvironment.getTypeUtils();
        messager = processingEnvironment.getMessager();
        filer = processingEnvironment.getFiler();

    }

    @Override
    public boolean process(Set<? extends TypeElement> set, RoundEnvironment roundEnvironment) {
        if (!EmptyUtils.isEmpty(set)) {
            // 获取所有被@Parameter注解的 元素集合
            Set<? extends Element> elements = roundEnvironment.getElementsAnnotatedWith(Parameter.class);
            if (!EmptyUtils.isEmpty(elements)) {
                // 用临时的map存储 用来遍历生成代码
                valueOfParameterMap(elements);
                try {
                    createParameterFile();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return true;
            }
        }
        return false;
    }

    /**
     * public class XActivity$$Parameter implements ParameterLoad {
     *
     * @Override public void loadParameter(Object target) {
     * // 一次
     * MainActivity t = (MainActivity) target;
     * // 循环
     * t.name = t.getIntent().getStringExtra("name");
     * t.age = t.getIntent().getIntExtra("age", 1);
     * }
     * <p>
     * }
     */
    private void createParameterFile() throws Exception {
        if (tempParameterMap.isEmpty()) {
            return;
        }
        // ParameterLoad接口
        TypeElement parameterType = elementUtils.getTypeElement(Constants.PARAMETER_LOAD);
        // 参数具体配置（Object target）
        ParameterSpec parameterSpec = ParameterSpec.builder(TypeName.OBJECT, Constants.PARAMETER_NAME).build();

        for (Map.Entry<TypeElement, List<Element>> entry : tempParameterMap.entrySet()) {
            // Map集合中的Key是类名 如： MainActivity
            TypeElement typeElement = entry.getKey();
            // 获取类名
            ClassName className = ClassName.get(typeElement);
            // 方法内容构建
            ParameterFactory factory = new ParameterFactory.Builder(parameterSpec)
                    .setMessager(messager)
                    .setElementUtils(elementUtils)
                    .setTypeUtils(typeUtils)
                    .setClassName(className)
                    .build();
            // 添加方法体内容第一行： MainActivity t = (MainActivity) target;
            factory.addFirstStatement();
            // 遍历类里面所有属性 t.name = t.getIntent.getStringExtra("name");
            for (Element fieldElement : entry.getValue()) {
                factory.buildStatement(fieldElement);
            }
            // 最终生成的类文件名（MainActivity$$Parameter）
            String finalClassName = typeElement.getSimpleName() + Constants.PARAMETER_FILE_NAME;
            messager.printMessage(Diagnostic.Kind.WARNING, "APT生成的获取参数的类文件为： " + className.packageName() + "." + finalClassName);
            JavaFile.builder(className.packageName(), // 包路径
                    TypeSpec.classBuilder(finalClassName) // 类名
                            .addSuperinterface(ClassName.get(parameterType)) // 实现接口
                            .addModifiers(Modifier.PUBLIC) // 类修饰符
                            .addMethod(factory.build()) // 方法的构建
                            .build())
                    .build()
                    .writeTo(filer); // 类构建完成
        }

    }

    private void valueOfParameterMap(Set<? extends Element> elements) {
        for (Element element : elements) {
            // 注解的属性 父节点是类节点
            TypeElement typeElement = (TypeElement) element.getEnclosingElement();
            // 如果map集合中有这个节点
            if (tempParameterMap.containsKey(typeElement)) {
                tempParameterMap.get(typeElement).add(element);
            } else {
                List<Element> fields = new ArrayList<>();
                fields.add(element);
                tempParameterMap.put(typeElement, fields);
            }
        }
    }


}
