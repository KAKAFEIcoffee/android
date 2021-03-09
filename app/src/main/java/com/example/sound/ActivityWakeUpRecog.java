package com.example.sound;

import android.os.Bundle;
import android.os.Message;
import android.widget.Toast;

import com.baidu.aip.asrwakeup3.core.recog.IStatus;
import com.baidu.aip.asrwakeup3.core.recog.MyRecognizer;
import com.baidu.aip.asrwakeup3.core.recog.listener.IRecogListener;
import com.baidu.aip.asrwakeup3.core.recog.listener.MessageStatusRecogListener;
import com.baidu.aip.asrwakeup3.core.wakeup.IWakeupListener;
import com.baidu.aip.asrwakeup3.core.wakeup.RecogWakeupListener;
import com.baidu.speech.asr.SpeechConstant;
import com.example.user.DBHelper;
import com.example.user.Userdata;

import java.util.LinkedHashMap;
import java.util.Map;

public class ActivityWakeUpRecog extends ActivityWakeUp implements IStatus {


    private static final String TAG = "ActivityWakeUpRecog";

    /**
     * 识别控制器，使用MyRecognizer控制识别的流程
     */
    protected MyRecognizer myRecognizer;

    /**
     * 0: 方案1， backTrackInMs > 0,唤醒词说完后，直接接句子，中间没有停顿。
     *              开启回溯，连同唤醒词一起整句识别。推荐4个字 1500ms
     *          backTrackInMs 最大 15000，即15s
     *
     * >0 : 方案2：backTrackInMs = 0，唤醒词说完后，中间有停顿。
     *       不开启回溯。唤醒词识别回调后，正常开启识别。
     * <p>
     *
     */
    private int backTrackInMs = 1500;

    public ActivityWakeUpRecog() {
        super();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();//初始化函数

        IRecogListener recogListener = new MessageStatusRecogListener(handler);
        // 改为 SimpleWakeupListener 后，不依赖handler，但将不会在UI界面上显示
        myRecognizer = new MyRecognizer(this, recogListener);

        IWakeupListener listener = new RecogWakeupListener(handler);
        myWakeup.setEventListener(listener); // 替换原来的 listener

    }
    protected void init() {
        Userdata user = DBHelper.getInstance(ActivityWakeUpRecog.this).getALamp_id(String.valueOf(1));//获取当前用户数据
        int a=user.getsound_and();
        if(a%10==1)//获取视频播放语音控制开关状态
        {
            start();
            Toast.makeText(getApplication(), "已打开语音助手", Toast.LENGTH_SHORT).show();
        }
        else
        {
            Toast.makeText(getApplication(), "未打开语音助手", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void handleMsg(Message msg) {
        super.handleMsg(msg);
        if (msg.what == STATUS_WAKEUP_SUCCESS) { // 唤醒词识别成功的回调，见RecogWakeupListener
            // 此处 开始正常识别流程
            Map<String, Object> params = new LinkedHashMap<String, Object>();
            params.put(SpeechConstant.ACCEPT_AUDIO_VOLUME, false);
            params.put(SpeechConstant.VAD, SpeechConstant.VAD_DNN);
            // 如识别短句，不需要需要逗号，使用1536搜索模型。其它PID参数请看文档
            params.put(SpeechConstant.PID, 1536);
            if (backTrackInMs > 0) {
                // 方案1  唤醒词说完后，直接接句子，中间没有停顿。开启回溯，连同唤醒词一起整句识别。
                // System.currentTimeMillis() - backTrackInMs ,  表示识别从backTrackInMs毫秒前开始
                params.put(SpeechConstant.AUDIO_MILLS, System.currentTimeMillis() - backTrackInMs);
            }
            myRecognizer.cancel();
            myRecognizer.start(params);
        }
    }

    @Override
    protected void stop() {
        super.stop();
        myRecognizer.stop();
    }

    @Override
    protected void onDestroy() {
        myRecognizer.release();
        super.onDestroy();
    }

//    public void start_sound()
//    {
//        start();//打开语音识别
//    }
//    public void stop_sound()
//    {
//        stop();//关闭语音识别
//    }
//    private int get_type()
//    {
//        return sound_type;//返回识别得到的唤醒词
//    }
}

