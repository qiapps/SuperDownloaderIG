package com.qiapps.superdownloaderig.Helper;

import android.content.Context;
import android.media.MediaPlayer;

import com.qiapps.superdownloaderig.R;

public class SoundHelper {

    public static void check(Context context){
        MediaPlayer mediaPlayer = MediaPlayer.create(context, R.raw.sword);
        mediaPlayer.start();
    }
}
