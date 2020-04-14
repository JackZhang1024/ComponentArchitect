package com.luckyboy.common;

// 类加载路径
public class PathBean {

    private String group;

    private String path;

    private Class clazz;

    public PathBean(String group, String path, Class clazz) {
        this.group = group;
        this.path = path;
        this.clazz = clazz;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public Class getClazz() {
        return clazz;
    }

    public void setClazz(Class clazz) {
        this.clazz = clazz;
    }
}
