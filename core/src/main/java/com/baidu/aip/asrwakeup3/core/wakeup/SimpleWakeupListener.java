package com.baidu.aip.asrwakeup3.core.wakeup;


import com.baidu.aip.asrwakeup3.core.util.MyLogger;

/**
 * Created by fujiayi on 2017/6/21.
 */

public class SimpleWakeupListener implements IWakeupListener {

    private static final String TAG = "SimpleWakeupListener";

    @Override
    public void onSuccess(String word, WakeUpResult result) {
//        MyLogger.info(TAG, "唤醒成功，唤醒词：" + word);
        MyLogger.info(TAG,word+"tips");//用于区分唤醒词跟句子
        //MyLogger.info("控制信息：" + word);
    }

    @Override
    public void onStop() {
//        MyLogger.info(TAG, "停止唤醒功能");
    }//停止唤醒词识别的回调

    @Override
    public void onError(int errorCode, String errorMessge, WakeUpResult result) {
        //MyLogger.info(TAG, "唤醒错误：" + errorCode + ";错误消息：" + errorMessge + "; 原始返回" + result.getOrigalJson());
    }

    @Override
    public void onASrAudio(byte[] data, int offset, int length) {
        //MyLogger.error(TAG, "audio data： " + data.length);
    }

}
