package com.example.videoplayer;

import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.widget.Toast;

import com.example.user.DBHelper;
import com.example.user.Userdata;

public class SoundVideo_control extends soundvideo{/*子类重写，会导致两个hander同时工作，一个进程不能出现两个hander，应该另开一个线程 可以用这个接口*/
    protected int sound_type;
    protected Userdata user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        /*利用Intent传递数据*/
        Intent intent = getIntent();
        path = intent.getStringExtra("FilePath");

        user = DBHelper.getInstance(SoundVideo_control.this).getALamp_id("1");
    }

    @Override
    protected void handleMsg(Message msg) {
        super.handleMsg(msg);                         //重写父类的函数，为确保父类函数的运行，应加super
        if (msg.obj != null) {                        //显示识别结果
            sound_action(msg);//执行相应的操作
        }
    }

    private void sound_action(Message msg)
    {
        if(msg.obj.toString().equals("播放tips")) {   //string不能直接用==
            sound_type=0;
        }
        else if (msg.obj.toString().equals("暂停tips"))
        {
            sound_type=1;
        }
        else if (msg.obj.toString().equals("停止tips"))
        {
            sound_type=2;
        }
        else if (msg.obj.toString().equals("增大音量tips"))
        {
            sound_type=3;
        }
        else if (msg.obj.toString().equals("减小音量tips"))
        {
            sound_type=4;
        }else if (msg.obj.toString().equals("增大亮度tips"))
        {
            sound_type=5;
        }
        else if (msg.obj.toString().equals("减小亮度tips"))
        {
            sound_type=6;
        }
        else                        //完整的句子
        {
            if(sound_type==0)
            {
                Toast.makeText(getApplication(), msg.obj.toString(), Toast.LENGTH_SHORT).show();
                if(msg.obj.toString().equals("播放"))
                {
                    on_off(1);
                }
                else if(msg.obj.toString().equals("你播放的太快了"))
                {
                    int b = user.getsetting()/1000;
                    back_control(b);
                    int a = user.getsetting()%10;
                    setspeed_control(a);
                }
                else if(msg.obj.toString().equals("你播放的太慢了"))
                {
                    int a = (user.getsetting()/10)%10;
                    setspeed_control(a+4);
                }
                else if(msg.obj.toString().equals("快进播放"))
                {
                    int b = (user.getsetting()/100)%10;
                    forward_control(b);
                }
            }
            else if(sound_type==1)
            {
                Toast.makeText(getApplication(), msg.obj.toString(), Toast.LENGTH_SHORT).show();
                if(msg.obj.toString().equals("暂停"))
                {
                    on_off(0);
                }
            }
            else if(sound_type==2)
            {
                Toast.makeText(getApplication(), msg.obj.toString(), Toast.LENGTH_SHORT).show();
                if(msg.obj.toString().equals("暂停"))
                {
                    on_off(0);
                }
            }
            else if(sound_type==3)
            {
                Toast.makeText(getApplication(), msg.obj.toString(), Toast.LENGTH_SHORT).show();
                if(msg.obj.toString().equals("增大音量"))
                {
                    up_down(-500);
                }
            }
            else if(sound_type==4)
            {
                Toast.makeText(getApplication(), msg.obj.toString(), Toast.LENGTH_SHORT).show();
                if(msg.obj.toString().equals("减小音量"))
                {
                    up_down(500);
                }
            }
            else if(sound_type==5)
            {
                Toast.makeText(getApplication(), msg.obj.toString(), Toast.LENGTH_SHORT).show();
            }
            else if(sound_type==6)
            {
                Toast.makeText(getApplication(), msg.obj.toString(), Toast.LENGTH_SHORT).show();
            }
        }
        //播放0 暂停1 停止2 增大音量3 减小音量4 增加亮度5 减小亮度6
//            txtLog.setText(msg.obj.toString() + "\n"+ String.valueOf(sound_type));

        //通过分析返回结果控制
    }
}
