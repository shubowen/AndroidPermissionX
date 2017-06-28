package com.xiaosu.lib.permission;

import android.annotation.TargetApi;
import android.app.Fragment;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.view.ContextThemeWrapper;
import android.text.TextUtils;
import android.util.SparseBooleanArray;


/**
 * 疏博文 新建于 2017/6/23.
 * 邮箱：shubw@icloud.com
 * 描述：请添加此文件的描述
 */


public class RequestFragment extends Fragment {

    private String[] mExplain;

    private String[] mPermissions;
    private boolean isNewActivity;
    private boolean mRetry;

    private SparseBooleanArray retryArr = new SparseBooleanArray();

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initData();
        checkPermission();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (checkPermission(mPermissions[requestCode])) {
            checkPermission();
        } else {
            onDeny(requestCode);
        }
    }

    private void initData() {
        Bundle extras = getArguments();
        mExplain = extras.getStringArray(Constants.EXPLAIN_KEY);
        mRetry = extras.getBoolean(Constants.RETRY_KEY);
        isNewActivity = extras.getBoolean(Constants.NEW_ACTIVITY, true);
        mPermissions = extras.getStringArray(Constants.PERMISSIONS_KEY);
    }

    private void checkPermission() {
        int index = checkPermissions(mPermissions);
        if (index != -1) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), mPermissions[index]) &&
                    null != mExplain && !TextUtils.isEmpty(mExplain[index])) {
                explain(mExplain[index], mPermissions[index], index);
            } else {
                requestPermission(mPermissions[index], index);
            }
        } else {
            onGrant();
        }
    }

    private void onDeny(int index) {
        boolean shouldExplain = ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), mPermissions[index]);

        if (shouldExplain) {
            if (mRetry &&
                    null != mExplain && !TextUtils.isEmpty(mExplain[index]) &&
                    retryArr.get(index, true)) {
                explain(mExplain[index], mPermissions[index], index);
            } else {
                onRealDeny(mPermissions[index], true);
            }
        } else {
            onRealDeny(mPermissions[index], false);
        }
    }

    /**
     * @param permission      拒绝的权限
     * @param canRequestAgain 是否能再次申请
     */
    private void onRealDeny(String permission, boolean canRequestAgain) {
        //用户点击不再询问，禁止权限
        PermissionCompat.notifyOnDenyCallback(permission, canRequestAgain);
        finish();
    }

    private void onGrant() {
        PermissionCompat.notifyOnGrantCallback();
        finish();
    }

    @TargetApi(Build.VERSION_CODES.M)
    private void requestPermission(String permission, int index) {
        requestPermissions(new String[]{permission}, index);
    }

    private int checkPermissions(String[] permissions) {
        for (int i = 0; i < permissions.length; i++) {
            if (!checkPermission(permissions[i])) {
                return i;
            }
        }
        return -1;
    }

    private boolean checkPermission(String permission) {
        return ContextCompat.checkSelfPermission(getActivity(), permission)
                == PackageManager.PERMISSION_GRANTED;
    }

    public static RequestFragment newInstance(String[] permissions,
                                              String[] explains,
                                              boolean retry,
                                              boolean newActivity) {

        Bundle args = new Bundle();
        args.putStringArray(Constants.PERMISSIONS_KEY, permissions);
        args.putStringArray(Constants.EXPLAIN_KEY, explains);
        args.putBoolean(Constants.RETRY_KEY, retry);
        args.putBoolean(Constants.NEW_ACTIVITY, newActivity);

        RequestFragment fragment = new RequestFragment();
        fragment.setArguments(args);
        return fragment;
    }

    public static RequestFragment newInstance(Bundle args) {
        RequestFragment fragment = new RequestFragment();
        fragment.setArguments(args);
        return fragment;
    }

    private void explain(String explain, final String permission, final int index) {
        retryArr.put(index, false);
        new AlertDialog.Builder(new ContextThemeWrapper(getActivity(), R.style.DialogStyle))
                .setMessage(explain)
                .setPositiveButton(R.string.sure, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        requestPermission(permission, index);
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        onRealDeny(permission, true);
                    }
                })
                .show()
                .setCancelable(false);
    }

    private void finish() {
        retryArr.clear();
        if (isNewActivity) getActivity().finish();
    }

    public void request(String[] permissions, String[] explains, boolean retry) {
        mPermissions = permissions;
        mExplain = explains;
        mRetry = retry;

        checkPermission();
    }
}
