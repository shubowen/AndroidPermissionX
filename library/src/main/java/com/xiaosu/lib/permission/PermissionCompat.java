package com.xiaosu.lib.permission;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.util.SparseArray;

import com.xiaosu.lib.permission.annotation.OnGrant;
import com.xiaosu.lib.permission.annotation.OnDeny;

import java.lang.reflect.Method;


public class PermissionCompat {

    private static OnRequestPermissionsCallBack sCallBack;
    private static Object sTarget;
    private static int sId;

    private Builder mBuilder;

    private PermissionCompat(Builder builder) {
        mBuilder = builder;
    }

    /**
     * 发起权限请求
     */
    public void request() {

        if (Build.VERSION.SDK_INT < 23) {
            // TODO: 2017/6/24 检查权限有没有在清单文件中配置

            for (String p : mBuilder.mPermissions) {
                PackageManager pm = mBuilder.mContext.getPackageManager();
                if (PackageManager.PERMISSION_DENIED ==
                        pm.checkPermission(p, mBuilder.mContext.getPackageName())) {
                    throw new RuntimeException("请在AndroidManifest.xml文件中配置 [ " + p + " ] 权限");
                }
            }

            notifyOnGrantCallback();

            return;
        }

        int length = mBuilder.mPermissions.length;
        String[] explains = new String[length];

        int size = mBuilder.sia.size();

        for (int i = 0; i < size; i++) {
            int index = mBuilder.sia.keyAt(i);

            if (index >= length) {
                throw new ArrayIndexOutOfBoundsException(index);
            }
            String explain = mBuilder.sia.valueAt(i);

            explains[index] = explain;
        }

        if (mBuilder.mContext instanceof Activity) {
            Activity fa = (Activity) mBuilder.mContext;
            FragmentManager manager = fa.getFragmentManager();
            Fragment fragment = manager.findFragmentByTag(Constants.TAG);
            if (null != fragment && fragment instanceof RequestFragment) {
                RequestFragment f = (RequestFragment) fragment;
                f.request(mBuilder.mPermissions, explains);
            } else {
                manager.beginTransaction()
                        .add(RequestFragment.newInstance(mBuilder.mPermissions, explains, false), Constants.TAG)
                        .commitAllowingStateLoss();
            }
            manager.executePendingTransactions();
        } else {
            RequestActivity.go(mBuilder.mContext, mBuilder.mPermissions, explains);
        }
    }

    static void notifyOnGrantCallback() {
        if (null != PermissionCompat.sCallBack) {
            PermissionCompat.sCallBack.onGrant();
            PermissionCompat.sCallBack = null;
        } else if (null != PermissionCompat.sTarget) {
            Method[] methods = PermissionCompat.sTarget.getClass().getDeclaredMethods();
            for (Method m : methods) {

                if (m.getModifiers() != Method.PUBLIC) continue;

                OnGrant onGrant = m.getAnnotation(OnGrant.class);
                // TODO: 2017/6/26 检验方法参数
                if (null != onGrant && onGrant.value() == PermissionCompat.sId) {
                    try {
                        m.invoke(PermissionCompat.sTarget);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
                }
            }
        }
        releaseReference();
    }

    static void notifyOnDenyCallback(String permission) {
        if (null != PermissionCompat.sCallBack) {
            PermissionCompat.sCallBack.onDenied(permission);
        } else if (null != PermissionCompat.sTarget) {
            Method[] methods = PermissionCompat.sTarget.getClass().getDeclaredMethods();
            for (Method m : methods) {
                if (m.getModifiers() != Method.PUBLIC) continue;

                OnDeny onDeny = m.getAnnotation(OnDeny.class);
                // TODO: 2017/6/26 检验方法参数
                if (null != onDeny && onDeny.value() == PermissionCompat.sId) {
                    try {
                        m.invoke(PermissionCompat.sTarget, permission);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
                }
            }
        }
        releaseReference();
    }

    private static void releaseReference() {
        PermissionCompat.sCallBack = null;
        PermissionCompat.sTarget = null;
        PermissionCompat.sId = 0;
    }

    public static Builder create(Context context) {
        return new Builder(context);
    }

    public static class Builder {

        private final Context mContext;

        private SparseArray<String> sia = new SparseArray<>();
        private String[] mPermissions;

        Builder(Context context) {
            this.mContext = context;
        }

        /**
         * @param index    权限角标
         * @param describe 权限描述
         * @return
         */
        public Builder explain(int index, String describe) {
            sia.put(index, describe);
            return this;
        }

        /**
         * @param explains 权限被拒绝一次之后，再次申请弹出的描述
         * @return
         */
        public Builder explain(String... explains) {
            int length = explains.length;
            for (int i = 0; i < length; i++) {
                sia.put(i, explains[i]);
            }
            return this;
        }

        /**
         * @param permissions 需要申请的权限
         * @return
         */
        public Builder permissions(String... permissions) {
            this.mPermissions = permissions;
            return this;
        }

        /**
         * @param callBack 申请回调
         * @return
         */
        public Builder callBack(OnRequestPermissionsCallBack callBack) {
            PermissionCompat.sCallBack = callBack;
            return this;
        }

        public Builder2 compactCallBack(Object cb, int id) {
            if (null != PermissionCompat.sCallBack)
                throw new RuntimeException("已经调用了callBack方法，不能再调用callBack2方法，只能同时存在一个回调");
            return new Builder2(this).callBack(cb, id);
        }

        public PermissionCompat build() {
            if (null == this.mPermissions)
                throw new RuntimeException("请添加申请权限");

            return new PermissionCompat(this);
        }
    }

    public static class Builder2 {

        Builder mBuilder;

        Builder2(Builder builder) {
            mBuilder = builder;
        }

        public Builder2 callBack(Object cb, int id) {
            PermissionCompat.sTarget = cb;
            PermissionCompat.sId = id;
            return this;
        }

        public PermissionCompat build() {
            return mBuilder.build();
        }

    }

}
