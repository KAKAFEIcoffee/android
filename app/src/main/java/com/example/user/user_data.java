/************************************************/
/**********显示用户个人信息，修改用户信息**********/
/************************************************/
package com.example.user;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.InputFilter;
import android.text.Spanned;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.movie2.R;

import java.util.ArrayList;

public class user_data extends AppCompatActivity {
    EditText user_name;
    ImageView user_picture;
    TextView user_ok,user_back;
    Button btn_sound,btn_transcribe;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_data);

        user_name = (EditText)findViewById(R.id.editText_user_d_name);
        user_picture = (ImageView)findViewById(R.id.imageView_user_d_picture);
        btn_sound = (Button)findViewById(R.id.button_user_d_sound);
        btn_transcribe = (Button)findViewById(R.id.button_user_d_transcribe);
        user_ok = (TextView) findViewById(R.id.textView_user_d_ok);
        user_back = (TextView) findViewById(R.id.textView_user_d_back);

        initPermission();
        //昵称禁止输入空格，回车，最多7个字
        InputFilter filter=new InputFilter() {
            @Override
            public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
                if (source.equals(" ") || source.toString().contentEquals("\n")) {
                    return "";
                } else {
                    return null;
                }
            }
        };
        user_name.setFilters(new InputFilter[]{filter});
        user_name.setFilters(new InputFilter[]{new InputFilter.LengthFilter(7)});

        show_data();//显示用户信息
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
    private void show_data(){
        /*利用Intent传递数据*/
        Intent intent = getIntent();
        int count = DBHelper.getInstance(user_data.this).getLampCount();
        int position = intent.getIntExtra("position",count);

        if((position == count)&&(position<=6)) //添加用户,最多6位用户
        {
//            user_picture
            user_name.setText("请输入昵称");
            btn_sound.setEnabled(false);
//            btn_transcribe
            user_ok.setText("创建用户");
            user_back.setText("取消创建");

            if(position==6){
                user_create(count);//创建用户,删除第7位用户数据
            }
            else if(position<=6){
                user_create(count+1);//创建用户
            }
            user_back();//不保存返回
        }
        else if(position<=5)
        {
            Userdata user = DBHelper.getInstance(user_data.this).getALamp_id(String.valueOf ( position+1 ));
//            user_picture
            user_name.setText(user.getname());
            btn_sound.setEnabled(true);
//            btn_transcribe
            user_ok.setText("确定");
            user_back.setText("退出");

            user_change(position+1);//修改用户
            user_back();//不保存返回
        }
    }
    /*************************************新建用户，该用户称为当前用户***************************************/
    public void user_create(final int id){
        user_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Userdata user = DBHelper.getInstance(user_data.this).getALamp_name(user_name.getText().toString());//数据库中搜索该昵称

                if(user_name.getText().toString().trim().isEmpty()){     //昵称为空
                    user_name.setText("请先填写昵称");
                    user_name.setTextColor(Color.RED);
                }
                else if(null != user.getname()){                          //昵称重复
                    Toast.makeText(getApplication(), "该昵称已存在", Toast.LENGTH_SHORT).show();
                }
//                else if(){
//                    Toast.makeText(getApplication(), "请先完成录音", Toast.LENGTH_SHORT).show();
//                }
                else {
                    if(id<7){            //最多6个用户
                        user.set_id(id); //新建用户,id也为当前所有用户个数
                        DBHelper.getInstance(user_data.this).saveLamp(user);   //新建id

                        for(int i=id;i>1;i--){
                            Userdata user_data = DBHelper.getInstance(user_data.this).getALamp_id(String.valueOf(i-1));//获得i-1用户的数据
                            user_data.set_id(i);//设置该用户id为i
                            DBHelper.getInstance(user_data.this).updateLamp(user_data,i);
                        }
                    }
                    if(id==7){            //最多6个用户
                        for(int i=id-1;i>1;i--){
                            Userdata user_data = DBHelper.getInstance(user_data.this).getALamp_id(String.valueOf(i-1));//获得i-1用户的数据
                            user_data.set_id(i);//设置该用户id为i
                            DBHelper.getInstance(user_data.this).updateLamp(user_data,i);  //删除第5位用户数据
                        }
                    }
                    user.set_id(1);
                    user.setpicture("");
                    user.setname(user_name.getText().toString());
                    user.setsound_wav("");
                    user.setsound_and(11);//设为默认值
                    user.setsetting(1101);
                    DBHelper.getInstance(user_data.this).updateLamp(user,1);//设置新建用户id=1(当前用户)

                    Toast.makeText(getApplication(), "已完成", Toast.LENGTH_SHORT).show();
                    user_data.this.onDestroy();
                    System.exit(0);
                }
            }
        });
    }
    /*************************************修改用户的信息，该用户成为当前用户***************************************/
    public void user_change(final int id){
        user_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Userdata user = DBHelper.getInstance(user_data.this).getALamp_name(user_name.getText().toString());//数据库中搜索该昵称

                if(user_name.getText().toString().trim().isEmpty()){     //昵称为空
//                    user_name.setText("请先填写昵称");
//                    user_name.setTextColor(Color.RED);
                    Toast.makeText(getApplication(), "请先填写昵称", Toast.LENGTH_SHORT).show();
                }
                else if((null != user.getname())&&(id!=user.get_id())){                          //昵称重复
                    Toast.makeText(getApplication(), "该昵称已存在", Toast.LENGTH_SHORT).show();
                }
//                else if(){
//                    Toast.makeText(getApplication(), "请先完成录音", Toast.LENGTH_SHORT).show();
//                }
                else {
                    user = DBHelper.getInstance(user_data.this).getALamp_id(String.valueOf(id));  //取出该用户数据
                        for(int i=id;i>1;i--){
                            Userdata user_data = DBHelper.getInstance(user_data.this).getALamp_id(String.valueOf(i-1));//获得i-1用户的数据
                            user_data.set_id(i);//设置该用户id为i
                            DBHelper.getInstance(user_data.this).updateLamp(user_data,i);
                        }
                    user.set_id(1);
                    user.setpicture("");
                    user.setname(user_name.getText().toString());
                    user.setsound_wav("");
                    DBHelper.getInstance(user_data.this).updateLamp(user,1);//设置新建用户id=1(当前用户)

                    Toast.makeText(getApplication(), "已完成", Toast.LENGTH_SHORT).show();
                    user_data.this.onDestroy();
                    System.exit(0);
                }
            }
        });

    }
    /*************************************不改变任何数据，直接返回***************************************/
    public void user_back(){
        user_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                user_data.this.onDestroy();
                System.exit(0);
            }
        });
    }
}