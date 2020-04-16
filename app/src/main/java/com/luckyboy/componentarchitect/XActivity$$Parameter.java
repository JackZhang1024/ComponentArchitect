package com.luckyboy.componentarchitect;

import com.luckyboy.arouter_api.core.ParameterLoad;
import com.luckyboy.componentarchitect.MainActivity;

// APT 生成的代码必须和MainActivity在同一个包下
public class XActivity$$Parameter implements ParameterLoad {

    @Override
    public void loadParameter(Object target) {
        // 一次
        MainActivity t = (MainActivity) target;
        // 循环
        t.name = t.getIntent().getStringExtra("name");
        t.age = t.getIntent().getIntExtra("age", 1);
    }


}
