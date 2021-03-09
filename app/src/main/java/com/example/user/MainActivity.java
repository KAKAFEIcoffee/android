/********************************************/
/**********显示初始界面，用于用户确认**********/
/********************************************/
package com.example.user;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.TextView;

import com.example.local.local;
import com.example.movie2.R;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    Button btn_setting,btn_local,btn_net;
    TextView user_name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        user_name = (TextView) findViewById(R.id.textView_m_name);
        btn_setting = findViewById(R.id.button_setting);
        btn_local = findViewById(R.id.button_local);
        btn_net = findViewById(R.id.button_net);

        initPermission();
        init();
        set_btn();
    }
    @Override
    protected void onResume() {                //刷新用户数据数据
        super.onResume();
        init();//读取数据库
    }
    /**
     * android 6.0 以上需要动态申请权限
     */
    private void initPermission() {
        String[] permissions = {
                Manifest.permission.ACCESS_NETWORK_STATE,
                Manifest.permission.INTERNET,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.MOUNT_UNMOUNT_FILESYSTEMS,
                Manifest.permission.READ_EXTERNAL_STORAGE
        };

        ArrayList<String> toApplyList = new ArrayList<String>();

        for (String perm : permissions) {
            if (PackageManager.PERMISSION_GRANTED != ContextCompat.checkSelfPermission(this, perm)) {
                toApplyList.add(perm);
                // 进入到这里代表没有权限.

            }
        }
        String[] tmpList = new String[toApplyList.size()];
        if (!toApplyList.isEmpty()) {
            ActivityCompat.requestPermissions(this, toApplyList.toArray(tmpList), 123);
        }

    }

    /**************************************显示********************************************/
    private void init() {
        //更新数据库
        int count = DBHelper.getInstance(MainActivity.this).getLampCount();
        if (count == 0) {
            user_name.setText("无可用用户");
        } else {
            Userdata user = DBHelper.getInstance(MainActivity.this).getALamp_id("1");
            user_name.setText("用户："+user.getname());
        }
    }

    /**************************************设置按钮的监听器********************************************/
    private void  set_btn() {
        btn_setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(MainActivity.this, setting.class);
                startActivity(intent);
                //设置切换动画，从右边进入，左边退出
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });

        btn_local.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(MainActivity.this, local.class);
                startActivity(intent);
                //设置切换动画，从右边进入，左边退出
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });

        btn_net.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent intent = new Intent();
//                intent.setClass(MainActivity.this, SoundVideo_control.class);
//                startActivity(intent);
//                //设置切换动画，从右边进入，左边退出
//                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });
    }
}
