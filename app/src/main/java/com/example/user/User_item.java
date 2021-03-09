/**************************************************************************/
/**********继承ArrayAdapter，生成user_item类，将user_item转换成view**********/
/**************************************************************************/
package com.example.user;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.movie2.R;

import java.util.List;

public class User_item extends ArrayAdapter {
    final int resourceId;

    public User_item(Context context, int textViewResourceId, List<Userdata> objects) {
        super(context, textViewResourceId, objects);
        resourceId = textViewResourceId;
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Userdata user = (Userdata)getItem(position);                                                // 获取当前项的Userdata实例
        View view = LayoutInflater.from(getContext()).inflate(resourceId, null);              //实例化一个对象
        TextView user_name = (TextView) view.findViewById(R.id.user_item_name);                     //获取该布局内的文本视图
        ImageView image = (ImageView) view.findViewById(R.id.user_item_picture);

        if(user.get_id()==(-100)) {                                 //添加用户框，对应虚假id
            user_name.setText("添加用户");
            image.setImageResource(R.mipmap.add);
        }
        else {
            user_name.setText(user.getname());
//            user_name.setCompoundDrawables();
        }
        return view;
    }
}
//                Log.d(TAG, "项目图片测试_source:" + source);       //Run中输出
//            android:drawableRight="@drawable/ic_launcher_background"