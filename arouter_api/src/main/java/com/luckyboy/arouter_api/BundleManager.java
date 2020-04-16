package com.luckyboy.arouter_api;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * 参数管理
 */
public class BundleManager {

    private Bundle bundle = new Bundle();

    private boolean isResult;


    public Bundle getBundle() {
        return bundle;
    }

    public boolean isResult() {
        return isResult;
    }

    // 对外提供传参方法
    public BundleManager withString(@NonNull String key, @Nullable String value) {
        bundle.putString(key, value);
        return this;
    }

    public BundleManager withResultString(@NonNull String key, @Nullable String value) {
        bundle.putString(key, value);
        isResult = true;
        return this;
    }

    public BundleManager withBoolean(@NonNull String key, boolean value) {
        bundle.putBoolean(key, value);
        return this;
    }

    public BundleManager withInt(@NonNull String key, int value) {
        bundle.putInt(key, value);
        return this;
    }

    public BundleManager withBundle(Bundle value) {
        this.bundle = value;
        return this;
    }

    // startActivity
    public Object navigation(Context context){
        return navigation(context, -1);
    }

    // forResult 这里的code 可能是requestCode 也可能是resultCode 取决于 isResult
    public Object navigation(Context context, int code){
        return RouterManager.getInstance().navigation(context, this, code);
    }






}
