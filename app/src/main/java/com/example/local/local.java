/***************************************************/
/**********显示本地视频List,实现视频点击播放**********/
/***************************************************/
package com.example.local;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.provider.BaseColumns;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.movie2.R;
import com.example.tools.CommTools;
import com.example.video.VideoInfo;
import com.example.videoplayer.SoundVideo_control;

import java.util.ArrayList;
import java.util.List;

public class local extends AppCompatActivity {
    ListView listview;
    List<VideoInfo> mData;
    private static final int MY_PERMISSIONS_REQUEST_CALL_PHONE = 6;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.local);

        initPermission();
        initData();//获取数据

        Local_item adapter = new Local_item(local.this, R.layout.local_item, mData);
        listview =findViewById(R.id.local_ListView);
        listview.setAdapter(adapter);

        initvideo();//设置点击监听
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


    private void initData() {
        mData=new ArrayList<>();
        String[] attr=new String[]{
                MediaStore.MediaColumns.DATA,
                BaseColumns._ID,
                MediaStore.MediaColumns.TITLE,
                MediaStore.MediaColumns.MIME_TYPE,
                MediaStore.Video.VideoColumns.DURATION,
                MediaStore.MediaColumns.SIZE
        };
            Cursor cursor = getContentResolver().query(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, attr, null, null, null);
            if (cursor != null) {
                while (cursor.moveToNext()) {
                    VideoInfo info = new VideoInfo();
                    info.setFilePath(cursor.getString(cursor
                            .getColumnIndexOrThrow(MediaStore.MediaColumns.DATA)));
                    info.setMimeType(cursor.getString(cursor
                            .getColumnIndexOrThrow(MediaStore.MediaColumns.MIME_TYPE)));
                    info.setTitle(cursor.getString(cursor
                            .getColumnIndexOrThrow(MediaStore.MediaColumns.TITLE)));
                    info.setTime(CommTools.LongToHms(cursor.getInt(cursor
                            .getColumnIndexOrThrow(MediaStore.Video.VideoColumns.DURATION))));
                    info.setSize(CommTools.LongToPoint(cursor
                            .getLong(cursor
                                    .getColumnIndexOrThrow(MediaStore.MediaColumns.SIZE))));
                    int id = cursor.getInt(cursor
                            .getColumnIndexOrThrow(BaseColumns._ID));
                    BitmapFactory.Options options = new BitmapFactory.Options();
                    options.inDither = false;
                    options.inPreferredConfig = Bitmap.Config.ARGB_8888;
                    info.setB(MediaStore.Video.Thumbnails.getThumbnail(getContentResolver(), id,
                            MediaStore.Images.Thumbnails.MICRO_KIND, options));
                    mData.add(info);
                }
            }
            cursor.close();
    }

    /*************************************点击视频***************************************/
    private void initvideo() {
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //position 点击的Item位置，从0开始算
                Intent intent=new Intent();
                intent.setClass(local.this, SoundVideo_control.class);
                intent.putExtra("FilePath",mData.get(position).getFilePath());//传递给下一个Activity的值
                startActivity(intent);
                //设置切换动画，从右边进入，左边退出
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });
    }
}
