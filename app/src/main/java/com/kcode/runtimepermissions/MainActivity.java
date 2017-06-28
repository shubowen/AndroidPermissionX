package com.kcode.runtimepermissions;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import com.xiaosu.lib.permission.PermissionCompat;
import com.xiaosu.lib.permission.annotation.OnDeny;
import com.xiaosu.lib.permission.annotation.OnGrant;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void openCamera(View view) {

        PermissionCompat.create(this)
                .permissions(Manifest.permission.CAMERA,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.RECORD_AUDIO)
                .explain("相机描述", "存储描述")
                .retry(true)
                .compactCallBack(this, 100)
                .build()
                .request();
    }

    @OnGrant(100)
    public void startCamera() {
        Intent intent = new Intent(MediaStore.INTENT_ACTION_STILL_IMAGE_CAMERA);
        startActivity(intent);
    }

    @OnDeny(100)
    public void onDenied(String permission, boolean retry) {
        Toast.makeText(MainActivity.this, permission + (retry ? "被拒绝" : "被禁止授权"), Toast.LENGTH_SHORT).show();

        if (!retry) {
            new AlertDialog.Builder(this)
                    .setMessage(permission + "已被禁止，如需使用相关功能，请进入设置页打开" + permission + "权限")
                    .setNegativeButton("去打开", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent intent = new Intent();
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            intent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
                            intent.setData(Uri.fromParts("package", getPackageName(), null));
                            startActivity(intent);
                        }
                    }).show();
        }
    }

}
