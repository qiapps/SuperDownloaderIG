package com.qiapps.superdownloaderig.qiadsopenapp;

import static androidx.lifecycle.Lifecycle.Event.ON_START;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.OnLifecycleEvent;
import androidx.lifecycle.ProcessLifecycleOwner;

import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.appopen.AppOpenAd;

import java.util.Date;

public class QIAppOpenSplash implements LifecycleObserver, Application.ActivityLifecycleCallbacks {
    private static final String LOG_TAG = "AppOpen";
    private String AD_UNIT_ID;
    public static final String ON_LOAD = "CARREGOU";
    public static final String FAIL_OR_DISMISS = "FECHOU ANUNCIO";



    public static final String TEST_AD_UNIT = "ca-app-pub-3940256099942544/9257395921";
    private AppOpenAd appOpenAd = null;
    private Activity currentActivity;
    private AppOpenAd.AppOpenAdLoadCallback loadCallback;
    private final Application myApplication;
    private long loadTime = 0;
    public static int interval = 0;

    private QIAppOpenSplash.Utils utils;

    /** Constructor */
    public QIAppOpenSplash(Application myApplication, String adUnit, int interval) {
        this.myApplication = myApplication;
        this.AD_UNIT_ID = adUnit;
        this.interval = interval;
        this.myApplication.registerActivityLifecycleCallbacks(this);
        ProcessLifecycleOwner.get().getLifecycle().addObserver(this);

    }

    public static void addPositiveEvent(Context context){
        EventManager.addPositiveEvent(context,"ctd");
    }

    public void setEventUtils(QIAppOpenSplash.Utils utils){
        this.utils = utils;
    }

    /** LifecycleObserver methods */
    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    public void onStart() {
        fetchAd();
    }

    public void fetchAd() {
        // Have unused ad, no need to fetch another.
        if (isAdAvailable()) {
            return;
        }

        loadCallback = new AppOpenAd.AppOpenAdLoadCallback() {
            @Override
            public void onAdLoaded(@NonNull AppOpenAd appOpenAd) {
                QIAppOpenSplash.this.appOpenAd = appOpenAd;
                QIAppOpenSplash.this.loadTime = (new Date()).getTime();
                QIAppOpenSplash.this.utils.event(ON_LOAD);
            }

            @Override
            public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                super.onAdFailedToLoad(loadAdError);
                //QIAppOpenSplash.this.utils.event(FAIL_OR_DISMISS);
            }
        };
        AdRequest request = getAdRequest();
        AppOpenAd.load(
                myApplication, AD_UNIT_ID, request, loadCallback);

    }


    public void show(){
        if(isAdAvailable() && EventManager.isEventCountEnable(currentActivity,"ctd",interval)){
            FullScreenContentCallback fullScreenContentCallback =
                    new FullScreenContentCallback() {
                        @Override
                        public void onAdDismissedFullScreenContent() {
                            // Set the reference to null so isAdAvailable() returns false.
                            QIAppOpenSplash.this.appOpenAd = null;
                            QIAppOpenSplash.this.utils.event(FAIL_OR_DISMISS);

                        }

                        @Override
                        public void onAdFailedToShowFullScreenContent(AdError adError) {
                            QIAppOpenSplash.this.appOpenAd = null;
                            QIAppOpenSplash.this.utils.event(FAIL_OR_DISMISS);
                        }

                        @Override
                        public void onAdShowedFullScreenContent() {

                        }
                    };

            appOpenAd.setFullScreenContentCallback(fullScreenContentCallback);
            appOpenAd.show(currentActivity);
        }else{
            if(utils != null){
                QIAppOpenSplash.this.utils.event(FAIL_OR_DISMISS);
            }

        }

    }

    /** Creates and returns ad request. */
    private AdRequest getAdRequest() {
        return new AdRequest.Builder().build();
    }

    /** Utility method to check if ad was loaded more than n hours ago. */
    private boolean wasLoadTimeLessThanNHoursAgo(long numHours) {
        long dateDifference = (new Date()).getTime() - this.loadTime;
        long numMilliSecondsPerHour = 3600000;
        return (dateDifference < (numMilliSecondsPerHour * numHours));
    }

    /** Utility method that checks if ad exists and can be shown. */
    public boolean isAdAvailable() {
        return appOpenAd != null && wasLoadTimeLessThanNHoursAgo(4);
    }

    @Override
    public void onActivityCreated(@NonNull Activity activity, @Nullable Bundle savedInstanceState) {

    }

    @Override
    public void onActivityStarted(@NonNull Activity activity) {
        currentActivity = activity;
    }

    @Override
    public void onActivityResumed(@NonNull Activity activity) {
        currentActivity = activity;
    }

    @Override
    public void onActivityPaused(@NonNull Activity activity) {

    }

    @Override
    public void onActivityStopped(@NonNull Activity activity) {

    }

    @Override
    public void onActivitySaveInstanceState(@NonNull Activity activity, @NonNull Bundle outState) {

    }

    @Override
    public void onActivityDestroyed(@NonNull Activity activity) {
        currentActivity = null;
    }

    public interface Utils{
        abstract void event(String event);
    }

}
