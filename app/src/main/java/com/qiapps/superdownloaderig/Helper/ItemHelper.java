package com.qiapps.superdownloaderig.Helper;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;

import java.io.File;

import com.qiapps.superdownloaderig.Model.InstagramResource;
import com.qiapps.superdownloaderig.R;

public class ItemHelper {


    public static void buildItemVideo(View itemVideo, InstagramResource instagramResource, Context context){

        SimpleDraweeView thumb = itemVideo.findViewById(R.id.img_thumb);
        SimpleDraweeView profile = itemVideo.findViewById(R.id.img_profile);
        TextView username = itemVideo.findViewById(R.id.txt_username);
        TextView title = itemVideo.findViewById(R.id.txt_title);
        TextView duration = itemVideo.findViewById(R.id.txt_duration);
        ImageView img_video = itemVideo.findViewById(R.id.img_video);

        thumb.setImageAlpha(1000);
        //Log.d("instaresource","profile: " + instagramResource.getImg_profile() + ";   url: " + instagramResource.getUrl());
        if(instagramResource.getImg_profile() != null) {
            profile.setImageURI(instagramResource.getImg_profile());
        }
        username.setText(instagramResource.getUsername());
        title.setText(instagramResource.getTitle());
        if(instagramResource.getUrl() != null) {
            if(FileManager.isSideCar(instagramResource)){

                String s = "";
//                if(!instagramResource.getVideo_url().equals("") && !FileManager.isUrlVideo(instagramResource.getVideo_url())){
//                    s = instagramResource.getVideo_url();
//                }else {
//                    String[] urls = instagramResource.getUrl().split(";");
//                    for (String u : urls) {
//                        if (s.equals("") && !FileManager.isUrlVideo(u)) {
//                            s = u;
//                        }
//                    }
//                }
                s = instagramResource.getUrl();
                thumb.setImageURI(s);
            }else{
                thumb.setImageURI(instagramResource.getUrl());
            }

        }

        if(instagramResource.isVideo() && !MyDownloadManager.isStories(instagramResource.getInstagramPath())){
            duration.setVisibility(View.VISIBLE);
            img_video.setVisibility(View.VISIBLE);

            duration.setText(configDuration(instagramResource.getDuration()));
        }else{
            duration.setVisibility(View.GONE);
            img_video.setVisibility(View.GONE);
        }

        if(FileManager.isSideCar(instagramResource)){
            if(!FileManager.isFileExists(context,instagramResource.getFilepath().split(";")[0])){
                img_video.setBackgroundResource(R.drawable.circle_black);
                img_video.setImageResource(R.drawable.ic_close_white);
                img_video.setVisibility(View.VISIBLE);
                thumb.setImageAlpha(50);
            }
        }else{
            if(!FileManager.isFileExists(context,instagramResource.getFilepath().split(";")[0])){
                img_video.setBackgroundResource(R.drawable.circle_black);
                img_video.setImageResource(R.drawable.ic_close_white);
                img_video.setVisibility(View.VISIBLE);
                thumb.setImageAlpha(50);
            }
        }


        //esconder duração video
        duration.setVisibility(View.GONE);
    }

    public static String configDuration(int duration){
        if(duration < 60){
            if(duration > 9){
                return "00:" + duration;
            }else{
                return "00:0" + duration;
            }
        }else{
            int it = duration/60;
            int dec = duration%60;
            String res = "";
            if(it > 9){
                res = "" + it + ":";
            }else{
                res = "0" + it + ":";
            }

            if(dec > 9){
                res += dec;
            }else{
                res += "0" + dec;
            }

            return res;
        }
    }
}
