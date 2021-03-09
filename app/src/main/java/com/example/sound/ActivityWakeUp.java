package com.example.sound;

import android.os.Bundle;

import com.baidu.aip.asrwakeup3.core.recog.IStatus;
import com.baidu.aip.asrwakeup3.core.wakeup.IWakeupListener;
import com.baidu.aip.asrwakeup3.core.wakeup.MyWakeup;
import com.baidu.aip.asrwakeup3.core.wakeup.RecogWakeupListener;
import com.baidu.speech.asr.SpeechConstant;

import java.util.HashMap;
import java.util.Map;

public class ActivityWakeUp extends ActivityCommon implements IStatus {

    protected MyWakeup myWakeup;
    private int status = STATUS_NONE;
    private static final String TAG = "ActivityWakeUp";

    public ActivityWakeUp() {
        super();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 改为 SimpleWakeupListener 后，不依赖handler，但将不会在UI界面上显示
        // 基于DEMO唤醒词集成第1.1, 1.2, 1.3步骤
        IWakeupListener listener = new RecogWakeupListener(handler);
        myWakeup = new MyWakeup(this, listener);
    }


    // 点击“开始识别”按钮
    // 基于DEMO唤醒词集成第2.1, 2.2 发送开始事件开始唤醒
    protected void start() {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put(SpeechConstant.WP_WORDS_FILE, "assets:///WakeUp.bin");
        // "assets:///WakeUp.bin" 表示WakeUp.bin文件定义在assets目录下

        // params.put(SpeechConstant.ACCEPT_AUDIO_DATA,true);
        // params.put(SpeechConstant.IN_FILE,"res:///com/baidu/android/voicedemo/wakeup.pcm");
        // params里 "assets:///WakeUp.bin" 表示WakeUp.bin文件定义在assets目录下
        myWakeup.start(params);
    }

    // 基于DEMO唤醒词集成第4.1 发送停止事件
    protected void stop() {
        myWakeup.stop();
    }

//    @Override
//    public void initView() {
//        super.initView();
//        btn.setOnClickListener(new View.OnClickListener() {
//
//            @Override
//            public void onClick(View v) {
//                switch (status) {
//                    case STATUS_NONE:
//                        start();
//                        status = STATUS_WAITING_READY;
//                        updateBtnTextByStatus();
////                        txtLog.setText("");//清空屏幕上的显示
//                        break;
//                    case STATUS_WAITING_READY:
//                        stop();
//                        status = STATUS_NONE;
//                        updateBtnTextByStatus();
////                        txtLog.setText("");//清空屏幕上的显示
//                        break;
//                    default:
//                        break;
//                }
//            }
//        });
//    }

//    private void updateBtnTextByStatus() {
//        switch (status) {
//            case STATUS_NONE:
//                btn.setText("启动唤醒");
//                break;
//            case STATUS_WAITING_READY:
//                btn.setText("停止唤醒");
//                break;
//            default:
//                break;
//        }
//    }

    @Override
    protected void onDestroy() {
        // 基于DEMO唤醒词集成第5 退出事件管理器
        myWakeup.release();
        super.onDestroy();
    }
}
