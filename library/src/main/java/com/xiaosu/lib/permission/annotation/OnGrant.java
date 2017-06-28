package com.xiaosu.lib.permission.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * 疏博文 新建于 2017/6/26.
 * 邮箱：shubw@icloud.com
 * 描述：请添加此文件的描述
 */
@Retention(RUNTIME)
@Target(ElementType.METHOD)
public @interface OnGrant {
    int[] value();
}
