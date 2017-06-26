package com.kcode.runtimepermissions;

import android.app.Application;

import com.squareup.leakcanary.LeakCanary;

/**
 * 疏博文 新建于 2017/6/26.
 * 邮箱：shubw@icloud.com
 * 描述：请添加此文件的描述
 */


public class CustomApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        if (!LeakCanary.isInAnalyzerProcess(this)) {
            LeakCanary.install(this);
        }
    }
}
