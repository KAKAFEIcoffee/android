/**************************************************************************/
/**********继承ArrayAdapter，生成Local_item类，将Local_item转换成view**********/
/**************************************************************************/
package com.example.local;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.movie2.R;
import com.example.video.VideoInfo;

import java.util.ArrayList;
import java.util.List;

public class Local_item extends ArrayAdapter {
    private List<VideoInfo> mData = new ArrayList<VideoInfo>();
    final int resourceId;

    public Local_item(Context context, int textViewResourceId, List<VideoInfo> m_Data) {
        super(context, textViewResourceId, m_Data);
        mData=m_Data;
        resourceId = textViewResourceId;
    }
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        VideoInfo video = (VideoInfo) getItem(position);                                            // 获取当前项的Userdata实例
        View view = LayoutInflater.from(getContext()).inflate(resourceId, null);              //实例化一个对象

        ImageView ivImg;
        TextView tvName;
        TextView tvTime;
        TextView tvWeight;

        ivImg=view.findViewById(R.id.local_item_video);
        tvName=view.findViewById(R.id.local_item_name);
        tvTime=view.findViewById(R.id.local_item_time);
        tvWeight=view.findViewById(R.id.local_item_size);

        tvName.setText(mData.get(position).getTitle());
        tvTime.setText("时长"+mData.get(position).getTime());
        tvWeight.setText("文件大小："+mData.get(position).getSize());
        ivImg.setImageBitmap(mData.get(position).getB());
        return view;
    }
}
