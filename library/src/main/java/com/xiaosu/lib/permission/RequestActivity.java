package com.xiaosu.lib.permission;

import android.app.Activity;
import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;


public class RequestActivity extends Activity {

    public static void go(Context context, String[] permissions, boolean retry,
                          String[] explains) {
        Intent intent = new Intent(context, RequestActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        Bundle args = new Bundle();
        args.putStringArray(Constants.PERMISSIONS_KEY, permissions);
        args.putBoolean(Constants.RETRY_KEY, retry);
        args.putStringArray(Constants.EXPLAIN_KEY, explains);
        args.putBoolean(Constants.NEW_ACTIVITY, true);
        intent.putExtras(args);

        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FragmentManager manager = getFragmentManager();
        manager.beginTransaction()
                .add(RequestFragment.newInstance(getIntent().getExtras()), Constants.TAG)
                .commitAllowingStateLoss();
        manager.executePendingTransactions();
    }
}
