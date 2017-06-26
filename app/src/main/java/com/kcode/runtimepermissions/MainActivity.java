package com.kcode.runtimepermissions;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import com.xiaosu.lib.permission.OnRequestPermissionsCallBack;
import com.xiaosu.lib.permission.PermissionCompat;
import com.xiaosu.lib.permission.annotation.OnGrant;
import com.xiaosu.lib.permission.annotation.OnDeny;

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
                .callBack(new OnRequestPermissionsCallBack() {
                    @Override
                    public void onGrant() {
                        startCamera();
                    }

                    @Override
                    public void onDenied(String permission) {
                        MainActivity.this.onDenied(permission);
                    }
                })
//                .compactCallBack(this, 100)
                .build()
                .request();
    }

    @OnGrant(100)
    public void startCamera() {
        Intent intent = new Intent(MediaStore.INTENT_ACTION_STILL_IMAGE_CAMERA);
        startActivity(intent);
    }

    @OnDeny(100)
    public void onDenied(String permission) {
        Toast.makeText(MainActivity.this, permission + "被拒绝", Toast.LENGTH_SHORT).show();
    }

}
