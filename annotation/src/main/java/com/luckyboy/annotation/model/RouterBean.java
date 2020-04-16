package com.luckyboy.annotation.model;

import javax.lang.model.element.Element;

/**
 * PathBean的升级版
 **/
public class RouterBean {

    public enum Type {
        ACTIVITY,
        // 跨模块的业务接口
        CALL
    }

    // 枚举类型
    private Type type;
    // 类节点
    private Element element;
    // 被@ARouter注解的类对象
    private Class<?> clazz;
    // 路由的组名
    private String group;
    // 路由地址
    private String path;


    private RouterBean(Builder builder) {
        this.group = builder.group;
        this.path = builder.path;
        this.element = builder.element;
    }

    private RouterBean(Type type, Class<?> clazz, String path, String group) {
        this.type = type;
        this.clazz = clazz;
        this.path = path;
        this.group = group;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public String getGroup() {
        return group;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getPath() {
        return path;
    }

    public void setElement(Element element) {
        this.element = element;
    }

    public Element getElement() {
        return element;
    }

    public Type getType() {
        return type;
    }

    public void setClazz(Class<?> clazz) {
        this.clazz = clazz;
    }

    public Class<?> getClazz() {
        return clazz;
    }

    public static RouterBean create(Type type, Class<?> clazz, String path, String group) {
        return new RouterBean(type, clazz, path, group);
    }

    public final static class Builder {
        // 类节点
        private Element element;
        // 路由的组名
        private String group;
        // 路由地址
        private String path;


        public Builder setElement(Element element) {
            this.element = element;
            return this;
        }

        public Builder setGroup(String group) {
            this.group = group;
            return this;
        }

        public Builder setPath(String path) {
            this.path = path;
            return this;
        }

        public RouterBean build() {
            if (path == null || path.length() == 0) {
                throw new IllegalArgumentException("path必填项为空 如：/app/MainActivity");
            }
            return new RouterBean(this);
        }
    }

    @Override
    public String toString() {
        return "path " + path;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null) {
            return false;
        }
        if (!(o instanceof RouterBean)) {
            return false;
        }
        RouterBean routerBean = (RouterBean) o;
        return this.type == routerBean.type
                && this.path.equalsIgnoreCase(routerBean.path)
                && this.group.equalsIgnoreCase(routerBean.path);
    }


}

