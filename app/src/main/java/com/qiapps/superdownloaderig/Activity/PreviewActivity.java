package com.qiapps.superdownloaderig.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.Toast;
import android.widget.VideoView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.qiapps.qiads.QINativeAds;
import com.qiapps.qiads.QIUtils;
import com.qiapps.superdownloaderig.R;
import com.tmall.ultraviewpager.UltraViewPager;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

import com.qiapps.superdownloaderig.Components.MenuOpcoes;
import com.qiapps.superdownloaderig.Database.DatabaseManager;
import com.qiapps.superdownloaderig.Helper.FileManager;
import com.qiapps.superdownloaderig.Model.InstagramResource;
import com.qiapps.superdownloaderig.ViewPager.UltraPagerAdapter;
import io.github.inflationx.viewpump.ViewPumpContextWrapper;

public class PreviewActivity extends AppCompatActivity {

    private ImageView back;
    private SimpleDraweeView img;
    private VideoView videoView;
    private UltraViewPager ultraViewPager;
    private InstagramResource instagramResource;
    private ImageView opcoes;
    private MenuOpcoes menuOpcoes;
    private ViewGroup container;
    private boolean menuOpen = false;
    //private ViewGroup container_ads;
    //private NativeAdsUtils.CustomNativeAds customNativeAds;
    //private QINativeAds qiNativeAds;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preview);


        img = findViewById(R.id.img_preview);
        back = findViewById(R.id.back);
        videoView = findViewById(R.id.video_preview);
        opcoes = findViewById(R.id.opcoes);
        container = findViewById(R.id.container_preview);
        //container_ads = findViewById(R.id.container_ads);

        ultraViewPager = findViewById(R.id.ultra_viewpager);

        Bundle b = getIntent().getExtras();

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        if(b!= null){
            String path = b.getString("path");
            instagramResource = DatabaseManager.getInstagramResourceByFilePath(this,path);
        }

        if(instagramResource != null){
            if(instagramResource.isVideo()){//é video unico
                Log.d("PreviewActivity","É Video");
                videoView.setVisibility(View.VISIBLE);
                ultraViewPager.setVisibility(View.GONE);
                img.setVisibility(View.GONE);
                //Log.d("PreviewActivity","getFilepath: " + instagramResource.getFilepath());
                //Uri uri = FileManager.getUriByVideoPath(this,instagramResource.getFilepath());

                //Log.d("PreviewActivity","Uri: " + uri);
                Uri videoUri = Uri.parse(instagramResource.getVideo_url());
                if(videoUri!= null){
//                    videoView.setVideoURI(uri);
//                    //videoView.setVideoPath(instagramResource.getFilepath());
//                    videoView.setMediaController(new MediaController(this));
//                    videoView.seekTo(1);

                    videoView.setVideoURI(videoUri);
                    MediaController mediaController = new MediaController(this);
                    mediaController.setAnchorView(videoView);
                    videoView.setMediaController(mediaController);
                    videoView.requestFocus();
                    videoView.start();
                }

            }else{
                if(FileManager.isSideCar(instagramResource)){//é carrossel/stories
                    Log.d("PreviewActivity","É SideCar");
                    videoView.setVisibility(View.GONE);
                    img.setVisibility(View.GONE);
                    ultraViewPager.setVisibility(View.VISIBLE);
                    String[] filesPaths = instagramResource.getVideo_url().split(";");
                    ArrayList<String> fps = new ArrayList<>();
                    fps.addAll(Arrays.asList(filesPaths));

                    ultraViewPager.setScrollMode(UltraViewPager.ScrollMode.HORIZONTAL);
                    ultraViewPager.setAdapter(new UltraPagerAdapter(fps,this));

                    ultraViewPager.initIndicator();
                    ultraViewPager.getIndicator()
                            .setOrientation(UltraViewPager.Orientation.HORIZONTAL)
                            .setFocusColor(Color.MAGENTA)
                            .setNormalColor(Color.WHITE)
                            .setRadius((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 5, getResources().getDisplayMetrics()));
                    ultraViewPager.getIndicator().setGravity(Gravity.CENTER_HORIZONTAL | Gravity.BOTTOM);
                    ultraViewPager.getIndicator().build();

                }else {//é imagem unica
                    Log.d("PreviewActivity","É Imagem Unica");
                    videoView.setVisibility(View.GONE);
                    ultraViewPager.setVisibility(View.GONE);
                    img.setVisibility(View.VISIBLE);

                    //Uri imgUri = Uri.parse();
                    img.setImageURI(instagramResource.getVideo_url());

//                    Bitmap bt = FileManager.getBitmapFromFilePath(this,instagramResource.getFilepath().split(";")[0]);
//                    if(bt != null){
//                        img.setImageBitmap(bt);
//                    }
                }
            }

            opcoes.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    menuOpcoes = new MenuOpcoes(PreviewActivity.this, instagramResource, new MenuOpcoes.MenuUtils() {
                        @Override
                        public void onItemDelete() {
                            Toast.makeText(PreviewActivity.this, R.string.excluido_com_sucesso, Toast.LENGTH_SHORT).show();
                            finish();
                        }

                        @Override
                        public void onMenuHide() {
                            menuOpen = false;
                        }

                        @Override
                        public void onShowMenu() {
                            menuOpen = true;
                        }

                        @Override
                        public void onClickDownload(String url) {

                        }
                    });

                    container.addView(menuOpcoes.create());
                    menuOpcoes.hideExcluir();
                    menuOpcoes.configDarkMode();
                }
            });
        }
        //container_ads.setVisibility(View.GONE);
//        if(!UserPreferences.isUserPremium(this)) {
//            qiNativeAds = new QINativeAds(this, container_ads, getString(R.string.nativo_inicio));
//            Drawable d = ContextCompat.getDrawable(this,R.drawable.borda_big_1);
//            qiNativeAds.setButtonResource(d);
//            qiNativeAds.addBlockedQIAppsAds(QIUtils.INSTADOWNLOADER);
//            qiNativeAds.setSmallType();
//            int color = Color.WHITE;
//            qiNativeAds.setTitleColor(color);
//            qiNativeAds.setDescriptionColor(color);
//            Typeface tf = Typeface.createFromAsset(getAssets(),"Montserrat-Bold.ttf");
//            qiNativeAds.setTitleTypeFace(tf);
//            qiNativeAds.setButtonTypeFace(tf);
//            qiNativeAds.build();
//        }
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase));
    }

    @Override
    public void onBackPressed() {
        if(menuOpen){
            menuOpcoes.closeMenu();
        }else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
