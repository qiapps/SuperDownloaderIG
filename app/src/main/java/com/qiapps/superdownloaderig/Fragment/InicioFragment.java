package com.qiapps.superdownloaderig.Fragment;


import android.content.ClipboardManager;
import android.content.Context;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.qiapps.qiads.QINativeAds;
import com.qiapps.qiads.QIUtils;

import com.qiapps.superdownloaderig.Activity.CustomApplication;
import com.qiapps.superdownloaderig.Database.DatabaseManager;
import com.qiapps.superdownloaderig.Helper.CheckConnectivity;
import com.qiapps.superdownloaderig.Helper.FileManager;
import com.qiapps.superdownloaderig.Helper.ItemHelper;
import com.qiapps.superdownloaderig.Helper.MyDownloadManager;
import com.qiapps.superdownloaderig.Activity.MainActivity;
import com.qiapps.superdownloaderig.Helper.UserPreferences;
import com.qiapps.superdownloaderig.Model.InstagramResource;
import com.qiapps.superdownloaderig.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class InicioFragment extends Fragment {

    private View view,item_video;
    private EditText editText;
    private TextView colar,como_usar;
    private ImageView ir,clear;
    private MainActivity activity;
    private String receivingUrl = "";
    private Handler handler;
    private ViewGroup info1,info2;
    private boolean isShowInfo = false;
    private boolean hasInstagramResource = false;
    private QINativeAds qiNativeAds;
    private ViewGroup container_ads;
    private FirebaseAnalytics analytics;
    public InicioFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_inicio, container, false);

        editText = view.findViewById(R.id.edt_link);
        colar = view.findViewById(R.id.btn_colar);
        como_usar = view.findViewById(R.id.btn_como_usar);
        ir = view.findViewById(R.id.btn_ir);
        item_video = view.findViewById(R.id.item_video_inicio);
        clear = view.findViewById(R.id.btn_clear_url);
        info1 = view.findViewById(R.id.container_info);
        info2 = view.findViewById(R.id.container_info2);
        container_ads = view.findViewById(R.id.container_ads);

        activity = (MainActivity)getActivity();
        analytics = activity.getAnalytics();
        item_video.findViewById(R.id.btn_opcoes).setVisibility(View.GONE);

        clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editText.setText("");
            }
        });

        if(activity != null) {
            if (activity.getInstagramResources().size() > 0) {
                final InstagramResource irs = activity.getInstagramResources().get(0);
                updateLastResource(irs);
                item_video.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if(FileManager.isFileExists(activity,irs.getFilepath().split(";")[0])) {
                            activity.startPreviewActivity(irs.getFilepath());
                            CustomApplication customApplication = (CustomApplication)activity.getApplication();
                            customApplication.showEntradaAds();
                        }else{
                            Toast.makeText(activity, R.string.arquivo_removido_dispositivo, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }else{
                item_video.setVisibility(View.GONE);
            }
        }

        colar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ClipboardManager clipboardManager = (ClipboardManager) activity.getSystemService(Context.CLIPBOARD_SERVICE);
                try {
                    String tx = clipboardManager.getPrimaryClip().getItemAt(0).coerceToHtmlText(activity);
                    editText.setText(tx);
                    ir.performClick();
                }catch (NullPointerException n){

                }

            }
        });


        ir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String link = editText.getText().toString();
                if(link.equals("")){
                    ClipboardManager clipboardManager = (ClipboardManager) activity.getSystemService(Context.CLIPBOARD_SERVICE);
                    try {
                        link = clipboardManager.getPrimaryClip().getItemAt(0).coerceToHtmlText(activity);
                        editText.setText(link);
                    }catch (NullPointerException n){

                    }
                }
                if (!CheckConnectivity.hasConnected(activity)) {
                    Toast.makeText(activity, R.string.verificar_internet, Toast.LENGTH_SHORT).show();
                } else {
                    if (FileManager.isValidUrl(link)) {
                        if (DatabaseManager.isUrlExists(activity, link)) {
                            Toast.makeText(activity, R.string.item_ja_baixado, Toast.LENGTH_SHORT).show();
                        } else {

                            handler = new Handler();
                            new MyDownloadManager(link, new MyDownloadManager.DownloadUtils() {
                                @Override
                                public void onStartDownload() {
                                    Log.d("MyDownloadManager","onStartDownload");
                                    handler.post(new Runnable() {
                                        @Override
                                        public void run() {
                                            activity.showVideoLoading();
                                        }
                                    });

                                }

                                @Override
                                public void onInstagramResourceBuild(InstagramResource instagramResource) {
                                    handler.post(new Runnable() {
                                        @Override
                                        public void run() {
                                            Log.d("MyDownloadManager","InstagramResource: " + instagramResource.toString());
                                            activity.showDownloadContainer(instagramResource);
                                        }
                                    });

                                }

                                @Override
                                public void onError(boolean showMessage) {
                                    handler.post(new Runnable() {
                                        @Override
                                        public void run() {

                                            activity.hideDownloadContainer();
                                            activity.hideVideoContainer();
                                            Toast.makeText(activity, R.string.nao_foi_possivel_download, Toast.LENGTH_SHORT).show();
                                        }
                                    });

                                }

                                @Override
                                public void onFoundPrivateAccount() {

                                }

                                @Override
                                public void onFoundProblemLink() {
                                    handler.post(new Runnable() {
                                        @Override
                                        public void run() {
                                            activity.hideDownloadContainer();
                                            activity.hideVideoContainer();
                                            Toast.makeText(activity, R.string.nao_foi_possivel_download, Toast.LENGTH_SHORT).show();
                                        }
                                    });

                                }

                                @Override
                                public void onConnectAbort() {
                                    handler.post(new Runnable() {
                                        @Override
                                        public void run() {
                                            activity.hideDownloadContainer();
                                            activity.hideVideoContainer();
                                            Toast.makeText(activity, R.string.nao_foi_possivel_download, Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }
                            },activity).init();
//                            handler = new Handler();
//                            final String finalLink = link;
//                            if(MyDownloadManager.isStories(link)){
//                                InstagramResource ins = new InstagramResource();
//                                ins.setTitle("");
//                                ins.setInstagramPath(link);
//                                ins.setIsVideo(0);
//                                ins.setImg_profile("");
//                                ins.setUsername("Stories");
//                                activity.configWebViewByStories(finalLink,ins);
//                            }else{
//
//                                handler.post(new Runnable() {
//                                    @Override
//                                    public void run() {
//                                        activity.configWebViewV2(finalLink);
//                                    }
//                                });
//                            }

                        }
                    }
                    else {

                        Toast.makeText(activity, R.string.digite_url_valida, Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        como_usar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isShowInfo){
                    hideInfo();
                }else {
                    showInfo();
                }
            }
        });

        hideInfo();

        container_ads.setVisibility(View.GONE);
        if(!UserPreferences.isUserPremium(activity)) {
            qiNativeAds = new QINativeAds(activity, container_ads, activity.getString(R.string.nativo_inicio));
            Drawable d = ContextCompat.getDrawable(activity,R.drawable.borda_big_1);
            qiNativeAds.setButtonResource(d);
            Typeface tf = Typeface.createFromAsset(activity.getAssets(),"Montserrat-Bold.ttf");
            qiNativeAds.setTitleTypeFace(tf);
            qiNativeAds.setButtonTypeFace(tf);
            qiNativeAds.addBlockedQIAppsAds(QIUtils.INSTADOWNLOADER);
            qiNativeAds.build();
//            qiStartNaviteAd = new QIStartNaviteAd(activity,container_ads,true);
//            qiStartNaviteAd.setTag("nativo_inicio");
//            qiStartNaviteAd.load();
        }

        return view;

    }

    public void hideInfo(){
        isShowInfo = false;
        info1.setVisibility(View.GONE);
        info2.setVisibility(View.GONE);
        if(hasInstagramResource){
            item_video.setVisibility(View.VISIBLE);
        }
    }

    public void showInfo(){
        isShowInfo = true;
        info1.setVisibility(View.VISIBLE);
        info2.setVisibility(View.VISIBLE);
        item_video.setVisibility(View.GONE);
        if(analytics != null){
            analytics.logEvent("clicou_como_usar",null);
        }
    }

    public void onReceivingText(String url){
        if(url != null) {
            receivingUrl = url;
        }

    }

    public void forceDownload(){
        if(receivingUrl != null && editText != null && ir != null){
            editText.setText(receivingUrl);
            ir.performClick();
        }
    }

    public void updateLastResource(final InstagramResource instagramResource){
        try {
            if (instagramResource != null) {
                item_video.setVisibility(View.VISIBLE);
                ItemHelper.buildItemVideo(item_video, instagramResource,activity);
                item_video.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        activity.startPreviewActivity(instagramResource.getFilepath());
                    }
                });
                hideInfo();
                hasInstagramResource = true;
            } else {
                item_video.setVisibility(View.GONE);
                hasInstagramResource = false;
            }
        }catch (Exception e){

        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if(!receivingUrl.equals("")){
            editText.setText(receivingUrl);
            ir.performClick();
            receivingUrl = "";
        }else{
            activity.hideLoadingContainer();
        }
    }

    @Override
    public void onDestroy() {
//        if(qiNativeAds!=null) {
//            qiNativeAds.destroy();
//        }
        super.onDestroy();
    }
}
