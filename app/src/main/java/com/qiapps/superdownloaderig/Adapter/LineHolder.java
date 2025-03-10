package com.qiapps.superdownloaderig.Adapter;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.facebook.drawee.view.SimpleDraweeView;

import com.qiapps.superdownloaderig.R;


public class LineHolder extends RecyclerView.ViewHolder{

    public SimpleDraweeView thumb,profile;
    public TextView username,title,duration;
    public ImageView img_video;


    public LineHolder(View itemView) {
        super(itemView);
        thumb = itemView.findViewById(R.id.img_thumb);
        profile = itemView.findViewById(R.id.img_profile);
        username = itemView.findViewById(R.id.txt_username);
        title = itemView.findViewById(R.id.txt_title);
        duration = itemView.findViewById(R.id.txt_duration);
        img_video = itemView.findViewById(R.id.img_video);

    }
}