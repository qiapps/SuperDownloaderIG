package com.qiapps.superdownloaderig.Activity;

import android.app.Activity;
import android.app.Application;
import android.util.Log;

import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.formats.UnifiedNativeAd;
import com.qiapps.qiads.QIAppOpenAds;
import com.qiapps.qiads.QILoaderNativeAds;
import com.qiapps.qiads.QINativeAds;
import com.qiapps.qiads.QInterstitial;

import com.qiapps.superdownloaderig.Helper.UserPreferences;
import com.qiapps.superdownloaderig.R;
import com.qiapps.superdownloaderig.qiadsopenapp.QIAppOpenSplash;

import io.github.inflationx.calligraphy3.CalligraphyConfig;
import io.github.inflationx.calligraphy3.CalligraphyInterceptor;
import io.github.inflationx.viewpump.ViewPump;

public class CustomApplication extends Application {

    private static final String FONT_NAME = "Montserrat-Regular.ttf";
    public QInterstitial qInterstitialVideoDownload;
    public QInterstitial qInterstitialnEntrada;
    public QIAppOpenSplash qiAppOpenSplash;


    @Override
    public void onCreate() {
        super.onCreate();
        ViewPump.init(ViewPump.builder()
                .addInterceptor(new CalligraphyInterceptor(
                        new CalligraphyConfig.Builder()
                                .setDefaultFontPath(FONT_NAME)
                                .setFontAttrId(io.github.inflationx.calligraphy3.R.attr.fontPath)
                                .build()))
                .build());


        MobileAds.initialize(this);
    }

    public void buildAppOpenAds(){
        if(qiAppOpenSplash == null && !UserPreferences.isUserPremium(this)){
            qiAppOpenSplash = new QIAppOpenSplash(this,getString(R.string.app_open_instadownloader),2);
        }
    }

    public void buildVideoDownloadInterstitial(Activity activity){
        if(!UserPreferences.isUserPremium(this)) {
            qInterstitialVideoDownload = new QInterstitial(activity, getString(R.string.interstitial_download_video));
            qInterstitialVideoDownload.build();
        }
    }

    public void buildEntradaInterstitial(Activity activity){
//        int count = UserPreferences.getContScreen(this);
//        int countEntrada = UserPreferences.getCountEntradaAds(this);
//        if(!UserPreferences.isUserPremium(this) && count%countEntrada == 0) {
//            qInterstitialnEntrada = new QInterstitial(activity, getString(R.string.interstitial_nEntrada));
//            qInterstitialnEntrada.build();
//        }
    }


    public boolean isLoadEntradaAds(){
        if(qInterstitialnEntrada == null) return false;
        return qInterstitialnEntrada.isLoaded();
    }

    public void showEntradaAds(){
        if(qInterstitialnEntrada != null){
            if(qInterstitialnEntrada.isLoaded()){
                qInterstitialnEntrada.show();
                qInterstitialnEntrada = null;
            }
        }
    }


    public boolean isLoadVideoDownloadAds(){
        if(qInterstitialVideoDownload != null){
            return qInterstitialVideoDownload.isLoaded() && UserPreferences.canShowVideoDownloadAds(this);
        }else return false;
    }

    public void showVideoDownloadAds(Activity activity){
        if(isLoadVideoDownloadAds()){
            qInterstitialVideoDownload.show();
            qInterstitialVideoDownload = null;
            UserPreferences.resetEventVideo(this);
            buildVideoDownloadInterstitial(activity);
        }
    }

}
