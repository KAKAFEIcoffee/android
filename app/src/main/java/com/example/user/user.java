/********************************************/
/**********显示用户list,更改当前用户**********/
/********************************************/
package com.example.user;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.movie2.R;

import java.util.ArrayList;
import java.util.List;

public class user extends AppCompatActivity{

    ListView listview;
    List<Userdata> userList = new ArrayList<Userdata>();

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user);

        initPermission();
        inituser(); // 显示用户数据
        User_item adapter = new User_item(user.this, R.layout.user_item, userList);
        listview =findViewById(R.id.ListView_user);
        listview.setAdapter(adapter);

        inituser_data();//添加监视器，用于确认或修改用户信息
    }
    @Override
    protected void onResume() {                //刷新用户数据数据
        super.onResume();
        inituser(); // 显示用户数据
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

    /*************************************显示用户数据***************************************/
    private void inituser() {
        userList=DBHelper.getInstance(user.this).getLampList();//获取所有用户数据
        Userdata user = new Userdata();
        user.set_id(-100);           //虚假id
        user.setname("添加用户");
        user.setsound_and(10);
        user.setsetting(1101);
        user.setpicture("");
        user.setsound_wav("");
        userList.add(user);
    }

    /*************************************确认或修改用户信息***************************************/
    private void inituser_data() {

        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //position 点击的Item位置，从0开始算
                Intent intent=new Intent();
                intent.setClass(user.this, user_data.class);
                intent.putExtra("position",position);//传递给下一个Activity的值
                startActivity(intent);
                //设置切换动画，从右边进入，左边退出
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });
    }
}
