package com.qiapps.superdownloaderig.ViewPager;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.VideoView;

import androidx.viewpager.widget.PagerAdapter;

import java.io.File;
import java.util.ArrayList;

import com.facebook.drawee.view.SimpleDraweeView;
import com.qiapps.superdownloaderig.Helper.FileManager;
import com.qiapps.superdownloaderig.R;

/**
 * Created by mikeafc on 15/11/26.
 */
public class UltraPagerAdapter extends PagerAdapter {

    private ArrayList<String> filePaths;
    private Context context;
    private MediaController mediaController;

    public UltraPagerAdapter(ArrayList<String> filePaths,Context context) {
        this.filePaths = filePaths;
        this.context = context;
    }

    @Override
    public int getCount() {
        return filePaths.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        FrameLayout frameLayout = (FrameLayout) LayoutInflater.from(container.getContext()).inflate(R.layout.custom_sidecar, null);

        SimpleDraweeView img = frameLayout.findViewById(R.id.img_sidecar);
        VideoView video = frameLayout.findViewById(R.id.video_sidecar);

        String filePath = filePaths.get(position);
        if(FileManager.isUrlVideo(filePath)){
            img.setVisibility(View.GONE);
            video.setVisibility(View.VISIBLE);
            //Uri uri = FileManager.getUriByVideoPath(context,filePath);
            Uri uri = Uri.parse(filePath);
            if(uri != null){
                video.setVideoURI(uri);
                mediaController = new MediaController(context);
                video.setMediaController(mediaController);
                video.seekTo(1);
                video.start();
            }

        }else{
            img.setVisibility(View.VISIBLE);
            video.setVisibility(View.GONE);
            img.setImageURI(filePath);

//            Bitmap bt = FileManager.getBitmapFromFilePath(context,filePath);
//            if (bt != null) {
//                img.setImageBitmap(bt);
//            }
        }

        container.addView(frameLayout);

        return frameLayout;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        FrameLayout view = (FrameLayout) object;
        container.removeView(view);
    }
}
