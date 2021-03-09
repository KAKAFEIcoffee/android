package com.example.videoplayer;

import android.annotation.SuppressLint;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Display;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.movie2.R;
import com.example.sound.ActivityWakeUpRecog;

import java.io.IOException;

import tv.danmaku.ijk.media.player.IMediaPlayer;
import tv.danmaku.ijk.media.player.IjkMediaPlayer;

public class soundvideo extends ActivityWakeUpRecog implements View.OnClickListener {

        private SurfaceView surfaceView;
        private IjkMediaPlayer mPlayer;
        //用于倍数
        private android.widget.Spinner Spinner;
        //获取屏幕分辨率
        Display display;

        //其他控件
        private ImageView ivState;
        private ImageView ivBack;
        private ImageView ivDown;
        private ImageView ivShare;
        private ImageView ivPlay;
        private ImageView ivFull;
        private TextView tvPlayTime;
        private TextView tvTotalTime;
        private TextView tvtips;
        private SeekBar seekBar;
        private View view;
        private LinearLayout topLayout;
        private LinearLayout bottomLayout;
        private boolean isShowMenu;
        /**横竖屏标识*/
        private boolean screenDirection=true;
        /**视频的宽高*/
        private long videoHeight;
        private long videoWidth;
        /**系统屏幕的宽高*/
        private long systemWidth;
        private long systemHeight;
        /*系统横竖屏分辨率*/
        private long Width;
        private long Height;
        /*视频播放标志位*/
        private boolean isPlay;
        /*视频的时长*/
        private long totalTime=0;
        //判断左右上下滑动
        private int x,y,endx,endy,startx,starty;
        //控制音量
        private AudioMngHelper audio;
        /*视频路径*/
        public String path;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.soundvideo);     //???????????????????????????????????????????????/

            audio = new AudioMngHelper(soundvideo.this);//音量
            //xml里的SurfaceView的id就是surface_view
            surfaceView = findViewById(R.id.surface_view);
            surfaceView.getHolder().addCallback(callback);/**注册当surfaceView创建、改变和销毁时应该执行的方法*/
            initView(); //初始化控件，绑定xml
            //--------监听进度条更改事件，这个写在了最后onSeekBarChangeListener()------
            seekBar.setOnSeekBarChangeListener(change);
        }

        //---------surface必须的函数，里面createplayer函数绑定ijk播放-------
        private SurfaceHolder.Callback callback = new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                //上面的函数
                createPlayer();
                mPlayer.setDisplay(surfaceView.getHolder());
            }
            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

            }
            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
                if (surfaceView != null) {
                    surfaceView.getHolder().removeCallback(callback);
                    surfaceView = null;
                }
            }
        };

        //---------createplayer----------
        private void createPlayer() {
            if (mPlayer == null) {
                mPlayer = new IjkMediaPlayer();

                //使用spinner选择倍数
                spinnerSelectSpeed();
                //设置1即为为变速不变调，默认为0变速变调
                mPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "soundtouch", 1);
                // 设置调用prepareAsync不自动播放，即调用start才开始播放
                mPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "start-on-prepared", 0);
                mPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                mPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER,"reconnect",5);
                mPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER,"packet-buffering",1);

                try {
                    mPlayer.setDataSource(path);//"http://9890.vod.myqcloud.com/9890_4e292f9a3dd011e6b4078980237cc3d3.f30.mp4"
                } catch (IOException e) {
                    e.printStackTrace();
                }
                //异步准备
                mPlayer.prepareAsync();
                mPlayer.setOnPreparedListener(new IMediaPlayer.OnPreparedListener() {
                    @Override
                    public void onPrepared(final IMediaPlayer mediaPlayer) {


                        //--------------想进来直接播放就用下面的strat，相应的控件改为播放-------------//
                        //mediaPlayer.start();
                        /**开始播放时，控件都处于暂停*/
                        ivState.setSelected(false);
                        ivPlay.setSelected(false);

                        /**为进度条设置最大值*/
                        seekBar.setMax((int) mPlayer.getDuration());
                        /*设置总时长*/
                        totalTime = mPlayer.getDuration();
                        tvTotalTime.setText("" + CommTools_video.LongToHms(totalTime));
                        /*获取视频size*/
                        videoWidth=mPlayer.getVideoWidth();
                        videoHeight=mPlayer.getVideoHeight();

                        if (videoHeight<=videoWidth) {
                            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);//landscape
                        }//横屏函数会自动刷新
//                        Toast.makeText(getApplication(), String.valueOf(videoWidth)+'\n'+String.valueOf(videoHeight)+'\n'+ mPlayer.getCurrentPosition(), Toast.LENGTH_SHORT).show();


                        /**开始线程，更新进度条的刻度*/
                        new Thread() {
                            @Override
                            public void run() {
                                try {
                                    isPlay = true;
                                    while (isPlay) {
                                        int current = (int) mPlayer.getCurrentPosition();
                                        Message message=Message.obtain();
                                        message.what=1;
                                        message.obj=current;
                                        handler1.sendMessage(message);
                                        seekBar.setProgress(current);
                                        sleep(500);
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }.start();
                        handler1.removeCallbacks(runnable);
                        handler1.postDelayed(runnable, 3000);
                    }
                });
            }
        }

        //---------其他控件--------
        public void initView() {
            ivState = (ImageView) findViewById(R.id.surface_iv_state);
            ivState.setOnClickListener(this);
            ivBack = (ImageView) findViewById(R.id.surface_iv_back);
            ivBack.setOnClickListener(this);
            ivDown = (ImageView) findViewById(R.id.surface_iv_download);
            ivDown.setOnClickListener(this);
            ivShare = (ImageView) findViewById(R.id.surface_iv_share);
            ivShare.setOnClickListener(this);
            ivPlay = (ImageView) findViewById(R.id.surface_iv_play);
            ivPlay.setOnClickListener(this);
            ivFull = (ImageView) findViewById(R.id.surface_iv_full);
            ivFull.setOnClickListener(this);
            tvPlayTime = (TextView) findViewById(R.id.surface_tv_start_time);
            tvTotalTime = (TextView) findViewById(R.id.surface_tv_total_time);
            tvtips = (TextView) findViewById(R.id.soundvideo_textView_tips);
            seekBar = (SeekBar) findViewById(R.id.surface_seekbar);
            topLayout = (LinearLayout) findViewById(R.id.surface_top_ll);
            bottomLayout = (LinearLayout) findViewById(R.id.surface_bottom_ll);


            //获取总时长，然后计算
            display = getWindowManager().getDefaultDisplay();
            systemWidth = display.getWidth();//获取分辨率
            systemHeight = display.getHeight();

            Width=systemWidth;
            Height=systemHeight;
//            if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) { //横屏
//                getWindow().getDecorView().setSystemUiVisibility(View.INVISIBLE);
//            }
//            else if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) { //竖屏
//// this.requestWindowFeature(Window.f);// 去掉标题栏
//// this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
//// WindowManager.LayoutParams.FLAG_FULLSCREEN);// 去掉信息栏
//                getWindow().getDecorView().setSystemUiVisibility(View.VISIBLE);
//            }
            if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {       //播放视频去掉状态栏
                WindowManager.LayoutParams attrs = getWindow().getAttributes();
                attrs.flags |= WindowManager.LayoutParams.FLAG_FULLSCREEN;
                getWindow().setAttributes(attrs);
                getWindow().addFlags(
                        WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
            } else if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
                WindowManager.LayoutParams attrs = getWindow().getAttributes();
                attrs.flags &= (~WindowManager.LayoutParams.FLAG_FULLSCREEN);
                getWindow().setAttributes(attrs);
                getWindow().clearFlags(
                        WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
            }

        }

        @Override
        public void onClick(View view) {
            this.view = view;
            switch (view.getId()){
                case R.id.surface_iv_state:
                    pause();
                    break;
                case R.id.surface_iv_back:
                    finish();
                    break;
                case R.id.surface_iv_download:
                    Toast.makeText(this,"本地视频无需下载",Toast.LENGTH_SHORT).show();
                    break;
                case R.id.surface_iv_share:
                    Toast.makeText(this,"暂无分享功能",Toast.LENGTH_SHORT).show();
                    break;
                case R.id.surface_iv_play:
                    pause();
                    break;
                case R.id.surface_iv_full:
//                    screenCut();
                    break;
            }
        }

        /**----------------暂停和继续播放-----------------*/

        /**定时任务*/
        @SuppressLint("HandlerLeak")
        Handler handler1;
        {
            handler1 = new Handler() {
                @Override
                public void handleMessage(Message msg) {
                    super.handleMessage(msg);
                    if (msg.what == 1) {
                        int playTime = (int) msg.obj;
                        tvPlayTime.setText("" + CommTools_video.LongToHms(playTime));
                    }
                }
            };
        }

        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                isShowHideTitle(false);
                handler1.postDelayed(this, 3000);
            }
        };

        /**控制上下标题栏显示和隐藏*/
        private void isShowHideTitle(boolean is){

            if (is){
                topLayout.setVisibility(View.VISIBLE);
                bottomLayout.setVisibility(View.VISIBLE);
                ivState.setVisibility(View.VISIBLE);
                isShowMenu=true;
            }else {
                topLayout.setVisibility(View.GONE);
                bottomLayout.setVisibility(View.GONE);
                ivState.setVisibility(View.GONE);
                isShowMenu=false;
            }
        }

        //------------pause函数-------------
        private void pause(){
            //Log.i(TAG,"暂停和继续播放");
            if (mPlayer.isPlaying()){
                mPlayer.pause();
                handler1.removeCallbacks(runnable);
                ivState.setSelected(false);
                ivPlay.setSelected(false);
                /**暂停时，上下标题栏一直显示*/
                isShowHideTitle(true);

            }else {
                ivState.setSelected(true);
                ivPlay.setSelected(true);
                mPlayer.start();
                /**继续播放时，上下标题栏等待3s隐藏*/
                handler1.postDelayed(runnable,3000);
            }
        }

        //--------触摸监听，为了解决再次触摸时控件能显示出来-------
        @Override
        public boolean onTouchEvent(MotionEvent event) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    //按下
                    x = (int) event.getX();
                    y = (int) event.getY();
                    startx=x;
                    starty=y;
                    break;

                case MotionEvent.ACTION_MOVE:    //快进
                    endx = (int) event.getX();
                    endy = (int) event.getY();
                    long disX0 = endx - x;
                    long disY0 = endy - y;

                    if (Math.abs(disX0) >= Math.abs(disY0)) //快进后退
                    {
                        float unitLength;
                        if(totalTime>(10*Width)) {     //视频足够长才能快进
                            unitLength =totalTime/(Width*10);
                            float fastLength =unitLength*disX0/Width;
                            long currentLength0 = (long) (mPlayer.getCurrentPosition() + fastLength*10000);
                            if(currentLength0>totalTime)
                            {
                                currentLength0=totalTime;
                            }
                            else if(currentLength0<=0)
                            {
                                currentLength0=0;
                            }
                            String a0 = "" + CommTools_video.LongToHms(currentLength0);
                            tips(a0);//提示框显示
                        }
                    } else if (Math.abs(disX0) < Math.abs(disY0)) //增大减小音量
                    {
                        float fastsound = (float) (10000*(endy-starty)/Height);//太小会被忽略
//                        Toast.makeText(getApplication(), String.valueOf(fastsound), Toast.LENGTH_SHORT).show();
                        up_down(fastsound);
                    }
                    startx=endx;   //与画图板画直线类似，一点一点变化
                    starty=endy;

                    break;

                case MotionEvent.ACTION_UP:
                    //松开并且未滑动
                    endx=(int)event.getX();
                    endy=(int)event.getY();
                    long disX=endx-x;
                    long disY=endy-y;

                    if((Math.abs(disX)<=10)&&(Math.abs(disY)<=10)) {
                        if (isShowMenu) {
                            isShowHideTitle(false);
                        } else {
                            isShowHideTitle(true);
                            handler1.removeCallbacks(runnable);
                            handler1.postDelayed(runnable, 3000);
                        }
                    }
                    else if(Math.abs(disX)>=Math.abs(disY)) //快进后退
                    {
                        float unitLength;
                        if(totalTime>(10*Width)) {     //视频足够长才能快进
                            unitLength =totalTime/(Width*10);
                            float fastLength =unitLength*disX/Width;
                            long currentLength = (long) (mPlayer.getCurrentPosition() + fastLength*10000);
                            forward_back(currentLength);
                        }
                    }
                    else if(Math.abs(disX)<Math.abs(disY)) //增大减小音量
                    {
//                        int fastsound = (int) (50*disY/Height);
//                        int a=up_down(fastsound);
//                        tips("音量 "+String.valueOf(a)+"%");//提示框显示
                    }

                    tips_0();//设置提示框不可见
                    break;
            }

            return super.onTouchEvent(event);
        }



       //-----------横屏----------------//会重绘屏幕
        public void  screenCut(){
            if (screenDirection){
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);//landscape
                screenDirection=false;
            }else {
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);//portrait
                screenDirection=true;
            }
        }

        //-----------按键选择播放速度------
        private void spinnerSelectSpeed() {
            /*-----------------spinner设计颜色--------------*/
            Spinner = (Spinner) findViewById(R.id.main_spinner);
            ArrayAdapter<String> adapter;
            String[] dataList={"×0.50","×0.75","×0.85","×1.00","×1.25","×1.50","×2.00"};
            //为dataList赋值，将下面这些数据添加到数据源中
        /*为spinner定义适配器，也就是将数据源存入adapter，这里需要三个参数
        1. 第一个是Context（当前上下文），这里就是this
        2. 第二个是spinner的布局样式，这里用android系统提供的一个样式
        3. 第三个就是spinner的数据源，这里就是dataList*/
            adapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item,dataList);
            //为适配器设置下拉列表下拉时的菜单样式。
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            //为spinner绑定我们定义好的数据适配器
            Spinner.setAdapter(adapter);
            //为spinner绑定监听器，这里我们使用匿名内部类的方式实现监听器
            Spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int speed_idx, long id) {
                    // adapter.getItem(color)即为选中的颜色 或为datalist[color]
                    TextView tv = (TextView)view;
                    tv.setTextColor(getResources().getColor(R.color.spinner));    //设置颜色
                    tv.setTextSize(18.0f);    //设置大小
                    tv.setGravity(android.view.Gravity.CENTER_HORIZONTAL);   //设置居中

                    switch (speed_idx) {
                        case 0:
                            setspeed(0.5f,speed_idx);
                            break;
                        case 1:
                            setspeed(0.75f,speed_idx);
                            break;
                        case 2:
                            setspeed(0.85f,speed_idx);
                            break;
                        case 3:
                            setspeed(1.0f,speed_idx);
                            break;
                        case 4:
                            setspeed(1.25f,speed_idx);
                            break;
                        case 5:
                            setspeed(1.5f,speed_idx);
                            break;
                        case 6:
                            setspeed(2.0f,speed_idx);
                            break;

                        default:
                            break;
                    }
                }
                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                    setspeed(1.0f,3);
                }
            });
            Spinner.setSelection(3);//初始化
        }

        //---------滑动条---------
        private SeekBar.OnSeekBarChangeListener change = new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                /**当进度条停止修改的时候触发*/
                /**取得当前进度条的刻度*/
                int progress = seekBar.getProgress();
                if (mPlayer != null && mPlayer.isPlaying()) {
                    /**设置当前播放的位置*/
                    mPlayer.seekTo(progress);
                    tvPlayTime.setText(""+ CommTools_video.LongToHms(progress));
                }
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            }
        };


        //********封装的函数*********
        //-----------播放暂停----------------
        public void  on_off(int a){
            if((a==1)&&(!mPlayer.isPlaying()))   //播放
            {
                pause();
            }
            else if((a==0)&&(mPlayer.isPlaying()))     //暂停
            {
                pause();
            }
        }
        //-----------快进后退----------------
        public void  forward_back(long a){
            if ((a>0)&&(a<= totalTime)) {
                mPlayer.seekTo(a);
            }
            else if(a>totalTime)
            {
                mPlayer.seekTo(totalTime);
            }
            else if(a<=0)
            {
                mPlayer.seekTo(0);
            }
        }
       //-----------提示----------------
        public void  tips(String a){
          tvtips.setVisibility(View.VISIBLE);
          ivState.setVisibility(View.GONE);
          tvtips.setText(a);//设置进度提醒
          tvtips.setBackgroundResource(R.drawable.corner_view);//设置Textview格式
        }
        public void  tips_0(){
          tvtips.setText("");//设置快进提示不可见
          tvtips.setVisibility(View.GONE);
          if(isShowMenu) {
            ivState.setVisibility(View.VISIBLE);
          }
        }
       //-----------增大减小音量----------------
        public void   up_down(float a){
           int b =audio.setVoice100_s(a);
           tips("音量"+String.valueOf(b)+"%");//提示框显示

            Handler TimerHandler=new Handler();                   //创建一个Handler对象
            Runnable myTimerRun=new Runnable()                //创建一个runnable对象
            {
                @Override
                public void run()
                {
                  tips_0();
                }
            };
            TimerHandler.postDelayed(myTimerRun, 1500);        //使用postDelayed方法，两秒后再调用此myTimerRun对象
        }
        //------设置倍数---------
        public void setspeed(float s,int speed_idx)
        {
            mPlayer.setSpeed(s);
            Spinner.setSelection(speed_idx);//设置数值
        }
        public void setspeed_control(int speed_idx)
        {
            switch (speed_idx) {
                case 0:
                    setspeed(0.5f,speed_idx);
                    break;
                case 1:
                    setspeed(0.75f,speed_idx);
                    break;
                case 2:
                    setspeed(0.85f,speed_idx);
                    break;
                case 3:
                    setspeed(1.0f,speed_idx);
                    break;
                case 4:
                    setspeed(1.25f,speed_idx);
                    break;
                case 5:
                    setspeed(1.5f,speed_idx);
                    break;
                case 6:
                    setspeed(2.0f,speed_idx);
                    break;

                default:
                    break;
            }
        }
    public void back_control(int back)
    {
        long currentLength=mPlayer.getCurrentPosition();
        switch (back) {
            case 0:
                currentLength -=5000;
                break;
            case 1:
                currentLength -=10000;
                break;
            case 2:
                currentLength -=15000;
                break;
            case 3:
                currentLength -=20000;
                break;
            case 4:
                currentLength -=25000;
                break;
            case 5:
                currentLength -=30000;
                break;

            default:
                break;
        }
        forward_back(currentLength);
    }
    public void forward_control(int forward)
    {
        long currentLength=mPlayer.getCurrentPosition();
        switch (forward) {
            case 0:
                currentLength +=5000;
                break;
            case 1:
                currentLength +=10000;
                break;
            case 2:
                currentLength +=15000;
                break;
            case 3:
                currentLength +=20000;
                break;
            case 4:
                currentLength +=25000;
                break;
            case 5:
                currentLength +=30000;
                break;

            default:
                break;
        }
        forward_back(currentLength);
    }


        //---------结束释放-----------
        @Override
        public void finish() {
            release();
            handler1.removeCallbacks(runnable);
            super.finish();
        }
        //-----退出释放函数-------
        @Override
        protected void onDestroy() {
            super.onDestroy();
            release();
        }
        private void release() {
            if (mPlayer != null) {
                mPlayer.stop();
                mPlayer.release();
                mPlayer = null;
            }
            IjkMediaPlayer.native_profileEnd();
        }
    }
