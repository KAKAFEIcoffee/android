/************************************************/
/**********显示设置界面，保存用户设置信息**********/
/************************************************/
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
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;

import com.example.movie2.R;

import java.util.ArrayList;

public class setting extends AppCompatActivity {
    ImageView image;
    TextView txt;
    Switch sound;
    Switch sound_m;
    Spinner spinner_b;
    Spinner spinner_f;
    Spinner spinner_s;
    Spinner spinner_s_;

public static final String[] ARRAY_b_f = {           //快进，后退
        "5s", "10s", "15s","20s", "25s", "30s"};//0-5
public static final String[] ARRAY_s = {             //加速
        "1.25×", "1.5×","2.0×"};//0-2
public static final String[] ARRAY_s_ = {            //减速
        "0.5×", "0.75×", "0.85×"};//0-2

protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.setting);
    image = findViewById(R.id.imageView_s_picture);        //头像
    txt = findViewById(R.id.textView_s_name);           //姓名
    sound = findViewById(R.id.switch_sound);          //语音提示
    sound_m = findViewById(R.id.switch_sound_movie);  //播放语音提示
    spinner_b = findViewById(R.id.spinner_back);      //后退
    spinner_f = findViewById(R.id.spinner_forward);   //快进
    spinner_s = findViewById(R.id.spinner_speed);     //加速
    spinner_s_ = findViewById(R.id.spinner_speed_);   //减速

    initPermission();
    read_sq();//读取数据库
    user_picture();//用户头像更改
    user_name();//用户更改
  }
    @Override
    protected void onResume() {                //刷新用户数据数据
        super.onResume();
        read_sq();//读取数据库
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
    private void init(String name,int sound_and,int setting,String picture,String sound_wav ) {
        txt.setText(name);                                             //昵称
        //头像
        if(sound_and==11)                                              //语音
        { sound.setChecked(true); sound_m.setChecked(true);}
        else if(sound_and==10)
        { sound.setChecked(true); sound_m.setChecked(false);}
        else if(sound_and==01)
        { sound.setChecked(false); sound_m.setChecked(true);}
        else if(sound_and==00)
        { sound.setChecked(false); sound_m.setChecked(false);}

        int a[]={setting/1000,setting/100%10,setting/10%10,setting%10};  //设置

        ArrayAdapter<String> adapter_b_f = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, ARRAY_b_f);
        //设置样式
        adapter_b_f.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_b.setAdapter(adapter_b_f);
        spinner_b.setSelection(a[0]);
        spinner_f.setAdapter(adapter_b_f);
        spinner_f.setSelection(a[1]);

        ArrayAdapter<String> adapter_s = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, ARRAY_s);
        //设置样式
        adapter_s.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_s.setAdapter(adapter_s);
        spinner_s.setSelection(a[2]);

        ArrayAdapter<String> adapter_s_ = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, ARRAY_s_);
        //设置样式
        adapter_s_.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_s_.setAdapter(adapter_s_);
        spinner_s_.setSelection(a[3]);
    }

    /**************************************读取数据库*******************************************/
    private void read_sq() {

        int count = DBHelper.getInstance(setting.this).getLampCount();
        if (count!=0) {
            Userdata user =  DBHelper.getInstance(setting.this).getALamp_id("1");
            init(" 当前用户："+user.name,user.sound_and,user.setting,user.picture,user.sound_wav);
            sound.setClickable(true);        //设置为可选中
            sound_m.setClickable(true);
            spinner_b.setEnabled(true);
            spinner_f.setEnabled(true);
            spinner_s.setEnabled(true);
            spinner_s_.setEnabled(true);
            write_sq();
        }
        else {
             init(" 请添加用户",10,1101,null,null);
             sound.setClickable(false);        //设置为不可选中
             sound_m.setClickable(false);
             spinner_b.setEnabled(false);
             spinner_f.setEnabled(false);
             spinner_s.setEnabled(false);
             spinner_s_.setEnabled(false);
        }
    }
    /****************************************存入数据库*************************************/
    private void write_sq(){
        final Userdata user = DBHelper.getInstance(setting.this).getALamp_id("1");
        sound.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked)
                {
                    int sound_and = user.getsound_and()%10+10;
                    user.setsound_and(sound_and);
                    DBHelper.getInstance(setting.this).updateLamp_sound_and(sound_and,1);
                }else{
                    int sound_and =user.getsound_and()%10;
                    user.setsound_and(sound_and);
                    DBHelper.getInstance(setting.this).updateLamp_sound_and(sound_and,1);
                }
            }
        });
        sound_m.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked)
                {
                    int sound_and = user.getsound_and()/10*10+1;
                    user.setsound_and(sound_and);
                    DBHelper.getInstance(setting.this).updateLamp_sound_and(sound_and,1);
                }else{
                    int sound_and =user.getsound_and()/10*10;
                    user.setsound_and(sound_and);
                    DBHelper.getInstance(setting.this).updateLamp_sound_and(sound_and,1);
                }
            }
        });
        spinner_b.setOnItemSelectedListener(new Spinner.OnItemSelectedListener() {
            //选择item的选择点击监听事件
            public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                int setting = user.getsetting()%1000+1000*arg2;
                user.setsetting(setting);
                DBHelper.getInstance(setting.this).updateLamp_setting(setting,1);
            }
            public void onNothingSelected(AdapterView<?> arg0) { }
        });
        spinner_f.setOnItemSelectedListener(new Spinner.OnItemSelectedListener() {
            //选择item的选择点击监听事件
            public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                int setting = user.getsetting()%100+100*arg2+user.getsetting()/1000*1000;
                user.setsetting(setting);
                DBHelper.getInstance(setting.this).updateLamp_setting(setting,1);
            }
            public void onNothingSelected(AdapterView<?> arg0) { }
        });
        spinner_s.setOnItemSelectedListener(new Spinner.OnItemSelectedListener() {
            //选择item的选择点击监听事件
            public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                int setting = user.getsetting()%10+10*arg2+user.getsetting()/100*100;
                user.setsetting(setting);
                DBHelper.getInstance(setting.this).updateLamp_setting(setting,1);
            }
            public void onNothingSelected(AdapterView<?> arg0) {

            }
        });
        spinner_s_.setOnItemSelectedListener(new Spinner.OnItemSelectedListener() {
            //选择item的选择点击监听事件
            public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                int setting = user.getsetting()/10*10+arg2;
                user.setsetting(setting);
                DBHelper.getInstance(setting.this).updateLamp_setting(setting,1);
            }
            public void onNothingSelected(AdapterView<?> arg0) { }
        });
    }
    /****************************************头像更改*************************************/
    private void user_picture(){

    }
    /****************************************用户更改*************************************/
    private void user_name(){
        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(setting.this, user.class);
                startActivity(intent);
                //设置切换动画，从右边进入，左边退出
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });
    }
}
