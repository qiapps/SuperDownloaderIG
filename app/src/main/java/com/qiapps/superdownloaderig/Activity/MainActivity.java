package com.qiapps.superdownloaderig.Activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;


import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.view.SimpleDraweeView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;
import com.qiapps.qiads.QIBottomDrawerAds;
import com.qiapps.qiads.QINativeAds;
import com.qiapps.qiads.QIUtils;


import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.viewpager2.widget.ViewPager2;

import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.JavascriptInterface;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import com.qiapps.qirating.QIRating;
import com.qiapps.superdownloaderig.Components.MenuOpcoes;
import com.qiapps.superdownloaderig.Database.DatabaseManager;
import com.qiapps.superdownloaderig.Fragment.HistoricoFragment;
import com.qiapps.superdownloaderig.Fragment.InicioFragment;
import com.qiapps.superdownloaderig.Helper.CheckConnectivity;
import com.qiapps.superdownloaderig.Helper.DownloadFileFromInstagramResource;
import com.qiapps.superdownloaderig.Helper.FileManager;
import com.qiapps.superdownloaderig.Helper.ItemHelper;
import com.qiapps.superdownloaderig.Helper.MyDownloadManager;
import com.qiapps.superdownloaderig.Helper.SoundHelper;
import com.qiapps.superdownloaderig.Helper.UserPreferences;
import com.qiapps.superdownloaderig.Model.InstagramResource;
import com.qiapps.superdownloaderig.R;
import com.qiapps.superdownloaderig.ViewPager.MyPagerAdapter;

import org.w3c.dom.Text;

import io.github.inflationx.viewpump.ViewPumpContextWrapper;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_CODE = 134;
    private ViewPager2 pager;
    private TabLayout tabLayout;
    private ViewGroup container_download,container_video,container_loading,vg_progress;
    private View itemVideo;
    private Button btn_download;
    private ArrayList<InstagramResource> instagramResources;
    private HistoricoFragment historicoFragment;
    private InicioFragment inicioFragment;
    private MenuOpcoes menu_opcoes;
    private TextView txt_progress_download;
    private ProgressBar progress_download;
    private ImageView btn_instagram;
    private boolean menuOpcaoOpen = false;
    private boolean videoContainerOpen = false;
    private boolean jaMostrouAvaliacao = false;
    private WebView webView;
    private boolean webUrlload = false;
    private boolean webviewVisible = false;
    private boolean preloadWebview = false;
    private boolean initLogin = false;
    private boolean loginInstagramWebview = false;
    private boolean isShowModalLogin = false;
    private ViewGroup container_ads_download;
    private ViewGroup container;
    private QIBottomDrawerAds qiBottomDrawerAds;
    private QINativeAds qiNativeAds;
    //private QIStartNaviteAd qiStartNaviteAd;

    private Handler handler;
    private String sku = "premium";
    //BillingClient billingClient;
    //SkuDetails skuDetails;
    private static final int REQUEST_CODE_BILLING = 1100;
    private FirebaseAnalytics analytics;
    private String price = "";
    private FirebaseRemoteConfig mFirebaseRemoteConfig;
    private long timeMillis = 1500;
    private long timeMillisStories = 1500;

    public CustomApplication customApplication;
    private ViewGroup vg_loading_ads;
    private int countLoadingAds = 3;
    private Timer timer;
    private boolean enableGetResource = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fresco.initialize(this);
        setContentView(R.layout.activity_main);

        pager = findViewById(R.id.pager);
        container_video = findViewById(R.id.container_video);
        container_loading = findViewById(R.id.container_loading);
        container_download = findViewById(R.id.container_download);
        itemVideo = findViewById(R.id.item_video_download);
        btn_download = findViewById(R.id.btn_download);
        btn_instagram = findViewById(R.id.btn_instagram);
        vg_progress = findViewById(R.id.vg_progress);
        txt_progress_download = findViewById(R.id.txt_progress_download);
        progress_download = findViewById(R.id.progress_download);
        container_ads_download = findViewById(R.id.container_ads_download);
        container = findViewById(R.id.container);
        vg_loading_ads = findViewById(R.id.vg_loading_ads);

        analytics = FirebaseAnalytics.getInstance(this);

        customApplication = (CustomApplication) getApplication();
        customApplication.buildVideoDownloadInterstitial(this);

        MobileAds.initialize(this);

        instagramResources = DatabaseManager.getAllInstagramResources(this);

        historicoFragment = new HistoricoFragment();
        inicioFragment = new InicioFragment();

        pager.setAdapter(new MyPagerAdapter(this,historicoFragment,inicioFragment));

        tabLayout = findViewById(R.id.tabLayout);
        new TabLayoutMediator(tabLayout, pager, new TabLayoutMediator.TabConfigurationStrategy() {
            @Override
            public void onConfigureTab(@NonNull TabLayout.Tab tab, int position) {
                switch (position){
                    case 0:
                        tab.setText(R.string.instagram);
                        break;
                    case 1:
                        tab.setText(R.string.historico);
                        break;
                    case 2:
                        tab.setText(R.string.opcoes);
                        break;
                }

            }
        }).attach();

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if(customApplication.isLoadEntradaAds()){
                    showLoadingEntradaAds();
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        container_video.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(videoContainerOpen) {
                    hideVideoContainer();
                }
            }
        });


        itemVideo.findViewById(R.id.btn_opcoes).setVisibility(View.GONE);

        btn_instagram.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FileManager.openInstagram(MainActivity.this);
            }
        });

        hideVideoContainer();

        Intent intent = getIntent();
        String action = intent.getAction();
        String type = intent.getType();

        if (Intent.ACTION_SEND.equals(action) && type != null) {
            if(type.equals("text/plain")){
                String sharedText = intent.getStringExtra(Intent.EXTRA_TEXT);
                showVideoLoading();
                inicioFragment.onReceivingText(sharedText);
            }
        }else{
            ClipboardManager clipboardManager = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
            try {
                String link = clipboardManager.getPrimaryClip().getItemAt(0).coerceToHtmlText(this);
                if(FileManager.isValidUrl(link) && !DatabaseManager.isUrlExists(this,link) && CheckConnectivity.hasConnected(this)) {
                    showVideoLoading();
                    inicioFragment.onReceivingText(link);
                }else{
                    //customApplication.showAppOpenAds();
                }
            }catch (NullPointerException n){
                //customApplication.showAppOpenAds();
            }
        }

        QIRating.addPositiveEvent(this);

        //ads
        buildDownloadAds();



        if(!UserPreferences.isUserPremium(this)) {
            qiBottomDrawerAds = new QIBottomDrawerAds(this, getString(R.string.nativo_saida),container, new QIBottomDrawerAds.BottomDrawerUtils() {
                @Override
                public void onShowDrawer() {

                }

                @Override
                public void onHideDrawer() {

                }

                @Override
                public void onClosePress() {
                    finish();
                }

                @Override
                public void onFailedToShow() {
                    finish();
                }
            });
            qiBottomDrawerAds.build();
            QINativeAds qin = qiBottomDrawerAds.getAds();
            Drawable d = ContextCompat.getDrawable(MainActivity.this,R.drawable.borda_big_1);
            qin.setButtonResource(d);
            Typeface tf = Typeface.createFromAsset(getAssets(),"Montserrat-Bold.ttf");
            qin.setTitleTypeFace(tf);
            qin.setButtonTypeFace(tf);
            qin.addBlockedQIAppsAds(QIUtils.INSTADOWNLOADER);

            customApplication.buildEntradaInterstitial(MainActivity.this);
        }

        //premium
        //configPremium();
        findViewById(R.id.btn_premium_toolbar).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //purchaseItem();
            }
        });


        //configuração remota
//        mFirebaseRemoteConfig = FirebaseRemoteConfig.getInstance();
//        FirebaseRemoteConfigSettings configSettings = new FirebaseRemoteConfigSettings.Builder()
//                //.setMinimumFetchIntervalInSeconds(0)
//                .build();
//        mFirebaseRemoteConfig.setDefaultsAsync(R.xml.remote_config);
//        mFirebaseRemoteConfig.setConfigSettingsAsync(configSettings);
//
//        mFirebaseRemoteConfig.fetchAndActivate()
//                .addOnCompleteListener(this, new OnCompleteListener<Boolean>() {
//                    @Override
//                    public void onComplete(@NonNull Task<Boolean> task) {
//                        if (task.isSuccessful()) {
//                            int countVideoDownload = Integer.parseInt(mFirebaseRemoteConfig.getString("count_video_download_ads"));
//                            int countEntradaAds = Integer.parseInt(mFirebaseRemoteConfig.getString("count_entrada_ads"));
//
//                            UserPreferences.changeCountEntradaAds(MainActivity.this,countEntradaAds);
//                            UserPreferences.changeCountVideoDownloadAds(MainActivity.this,countVideoDownload);
//                        } else {
//                        }
//                    }
//                });

    }

    public FirebaseAnalytics getAnalytics(){
        return analytics;
    }

    public void showLoadingEntradaAds(){
        vg_loading_ads.setVisibility(View.VISIBLE);
        final TextView txt = findViewById(R.id.txt_loading_ads);
        String t = getString(R.string.anuncio_sera_exibido)+" "+countLoadingAds;
        txt.setText(t);

        final Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                countLoadingAds--;
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(countLoadingAds == 0){
                            customApplication.showEntradaAds();
                            vg_loading_ads.setVisibility(View.GONE);
                            countLoadingAds = 3;
                            timer.cancel();
                        }else{
                            String t = getString(R.string.anuncio_sera_exibido)+" "+countLoadingAds;
                            txt.setText(t);
                        }

                    }
                });
            }
        },1000,1000);
    }

    public void showModalLogin(){
        if(!isShowModalLogin) {
            isShowModalLogin = true;
            AlertDialog.Builder dialog = new AlertDialog.Builder(this);
            View cont = getLayoutInflater().inflate(R.layout.modal_login_instagram, null);
            dialog.setView(cont);
            final AlertDialog mDialog = dialog.create();

            cont.findViewById(R.id.btn_entendi).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mDialog.dismiss();
                    mDialog.cancel();
                    isShowModalLogin = false;
                }
            });

            mDialog.show();
        }

    }

    public void openMenu(final InstagramResource instagramResource){
        menu_opcoes = new MenuOpcoes(this, instagramResource, new MenuOpcoes.MenuUtils() {
            @Override
            public void onItemDelete() {
                historicoFragment.onItemDeleted(instagramResource);
                instagramResources = DatabaseManager.getAllInstagramResources(MainActivity.this);
                if(instagramResources.size() > 0){
                    inicioFragment.updateLastResource(instagramResources.get(0));
                }else{
                    inicioFragment.updateLastResource(null);
                }

            }

            @Override
            public void onMenuHide() {
                menuOpcaoOpen = false;
            }

            @Override
            public void onShowMenu() {
                menuOpcaoOpen = true;
            }

            @Override
            public void onClickDownload(String url) {
                if(url != null) {
                    inicioFragment.onReceivingText(url);
                    inicioFragment.forceDownload();
                }

            }
        });
        ViewGroup vg = findViewById(R.id.container);
        vg.addView(menu_opcoes.create());

    }

    public ArrayList<InstagramResource> getInstagramResources() {
        return instagramResources;
    }

    public void showVideoLoading(){
        container_video.setVisibility(View.VISIBLE);
        showLoadingContainer();
        hideDownloadContainer();
    }

    public void buildDownloadAds(){
        container_ads_download.setVisibility(View.GONE);
        if(!UserPreferences.isUserPremium(this)) {

            qiNativeAds = new QINativeAds(MainActivity.this,container_ads_download,getString(R.string.nativo_download));
            qiNativeAds.setShowWhenFinishLoad(false);
            qiNativeAds.setType(QIUtils.TYPE_SMALL2);
            Drawable d = ContextCompat.getDrawable(MainActivity.this,R.drawable.borda_redonda_pink_slim);
            qiNativeAds.setButtonResource(d);
            int color = ContextCompat.getColor(this,R.color.pink);
            Typeface tf = Typeface.createFromAsset(getAssets(),"Montserrat-Bold.ttf");
            qiNativeAds.setTitleTypeFace(tf);
            qiNativeAds.setCallToActionColor(color);
            qiNativeAds.setButtonTypeFace(tf);
            qiNativeAds.addBlockedQIAppsAds(QIUtils.INSTADOWNLOADER);
            qiNativeAds.build();
//            qiStartNaviteAd = new QIStartNaviteAd(MainActivity.this,container_ads_download,false);
//            qiStartNaviteAd.setTypeAd(QIStartNaviteAd.TYPE_SMALL);
//            qiStartNaviteAd.setTag("nativo_video_download");
//            qiStartNaviteAd.load();
        }
    }

    public void hideVideoContainer(){
        container_video.setVisibility(View.GONE);
        showLoadingContainer();
        videoContainerOpen = false;
        if(qiNativeAds != null) {
            buildDownloadAds();
        }
    }

    public void showDownloadContainer(final InstagramResource instagramResource){
        hideLoadingContainer();
        if(instagramResource != null) {

            if(qiNativeAds!= null) {
                qiNativeAds.show();
            }
            handler = new Handler();
            videoContainerOpen = true;
            container_download.setVisibility(View.VISIBLE);
            ItemHelper.buildItemVideo(itemVideo, instagramResource,this);
            final ImageView img_video = itemVideo.findViewById(R.id.img_video);
            img_video.setVisibility(View.GONE);
            vg_progress.setVisibility(View.GONE);
            btn_download.setAlpha(1f);
            final SimpleDraweeView s = itemVideo.findViewById(R.id.img_thumb);
            s.setImageAlpha(1000);

            final TextView txt_username = itemVideo.findViewById(R.id.txt_username);
            final TextView txt_title = itemVideo.findViewById(R.id.txt_title);

            btn_download.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    vg_progress.setVisibility(View.VISIBLE);
                    txt_progress_download.setText("0%");
                    progress_download.setProgress(0);
                    btn_download.setOnClickListener(null);
                    btn_download.setAlpha(0.5f);

                    s.setImageAlpha(20);

                    new DownloadFileFromInstagramResource(instagramResource, new DownloadFileFromInstagramResource.DownloadFileUtils() {
                        @Override
                        public void onProgress(int progress,int totalDownloads, int currentDownload) {
                            String pgs = progress + "%";
                            txt_progress_download.setText(pgs);
                            progress_download.setProgress(progress);
                            String avanco = "Downloading... "+currentDownload+"/"+totalDownloads;
                            txt_username.setText(avanco);
                        }

                        @Override
                        public void onFinish(InstagramResource instagramResource) {
                            UserPreferences.addEventVideo(MainActivity.this);

                            s.setImageAlpha(1000);
                            vg_progress.setVisibility(View.GONE);
                            img_video.setVisibility(View.VISIBLE);
                            img_video.setImageResource(R.drawable.ic_check);
                            img_video.setBackgroundResource(R.drawable.circle_pink);
                            SoundHelper.check(MainActivity.this);

                            DatabaseManager.saveInstagramResource(MainActivity.this, instagramResource);

                            instagramResources = DatabaseManager.getAllInstagramResources(MainActivity.this);
                            historicoFragment.setupRecycler(instagramResources, MainActivity.this);
                            inicioFragment.updateLastResource(instagramResource);

                            Toast.makeText(MainActivity.this, R.string.download_concluido, Toast.LENGTH_SHORT).show();
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    hideDownloadContainer();
                                    hideVideoContainer();
                                }
                            });

                            QIRating.addPositiveEvent(MainActivity.this);
                            if(!isFinishing()) {
                                if(!validateRating()){
                                    customApplication.showVideoDownloadAds(MainActivity.this);
                                }
                            }

                            Bundle b = new Bundle();
                            String tp = instagramResource.isVideo() ? "video" : "image";
                            b.putString("tipo",tp);
                            analytics.logEvent("download",b);


                        }

                        @Override
                        public void onError(String error) {
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    hideDownloadContainer();
                                    Toast.makeText(MainActivity.this, R.string.nao_foi_possivel_download, Toast.LENGTH_SHORT).show();
                                }
                            });
                        }

                        @Override
                        public void onPermissionDenied(final String url) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    inicioFragment.onReceivingText(url);
                                    requestPermission();
                                    hideVideoContainer();
                                    Toast.makeText(MainActivity.this, R.string.habilitar_permissao_para_continuar, Toast.LENGTH_SHORT).show();
                                }
                            });

                        }
                    },MainActivity.this).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                }
            });
        }
    }

    public void startPreviewActivity(String path){
        Intent it = new Intent(MainActivity.this,PreviewActivity.class);
        it.putExtra("path",path);
        startActivity(it);
    }

    public void hideDownloadContainer(){
        container_download.setVisibility(View.GONE);
    }

    public void showLoadingContainer(){
        container_loading.setVisibility(View.VISIBLE);
    }

    public void hideLoadingContainer(){
        container_loading.setVisibility(View.GONE);
    }


//    private void configPremium(){
//        billingClient = BillingClient.newBuilder(this)
//                .setListener(this)
//                .enablePendingPurchases()
//                .build();
//        billingClient.startConnection(new BillingClientStateListener() {
//            @Override
//            public void onBillingSetupFinished(BillingResult billingResult) {
//                if (billingResult.getResponseCode() ==  BillingClient.BillingResponseCode.OK) {
//                    // The BillingClient is ready. You can query purchases here.
//                    getSku();
//                }
//            }
//            @Override
//            public void onBillingServiceDisconnected() {
//                // Try to restart the connection on the next request to
//                // Google Play by calling the startConnection() method.
//            }
//        });
////        Intent serviceIntent =
////                new Intent("com.android.vending.billing.InAppBillingService.BIND");
////        serviceIntent.setPackage("com.android.vending");
////        bindService(serviceIntent, mServiceConn, Context.BIND_AUTO_CREATE);
//    }
//
//    public String getPrice() {
//        return price;
//    }
//
//    private void getSku(){
//        List<String> skuList = new ArrayList<> ();
//        skuList.add("premium");
//        SkuDetailsParams.Builder params = SkuDetailsParams.newBuilder();
//        params.setSkusList(skuList).setType(BillingClient.SkuType.INAPP);
//        billingClient.querySkuDetailsAsync(params.build(), new SkuDetailsResponseListener() {
//            @Override
//            public void onSkuDetailsResponse(BillingResult billingResult,List<SkuDetails> skuDetailsList) {
//                try {
//                    for (SkuDetails sku : skuDetailsList) {
//                        String SKU = sku.getSku();
//                        if (SKU.equals("premium")) {
//                            price = sku.getPrice();
//                            skuDetails = sku;
//                        }
//                    }
//                }catch (Exception e){
//
//                }
//            }
//        });
//    }
//
//    public void purchaseItem(){
//        if(!verificaCompraNoApp()) {
//            if(skuDetails != null){
//                BillingFlowParams billingFlowParams = BillingFlowParams.newBuilder()
//                        .setSkuDetails(skuDetails)
//                        .build();
//                billingClient.launchBillingFlow(this,billingFlowParams);
//                //billingClient.acknowledgePurchase();
//            }else{
//                Toast.makeText(this, R.string.erro_ao_comprar, Toast.LENGTH_SHORT).show();
//            }
//
//        }else{
//            Toast.makeText(this, R.string.conta_reativada_com_sucesso, Toast.LENGTH_SHORT).show();
//        }
//    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

//    @Override
//    public void onPurchasesUpdated(@NonNull BillingResult billingResult, @Nullable List<Purchase> list) {
//
//        if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK && list != null) {
//            Purchase purchase = list.get(0);
//
//            if (purchase.getPurchaseState() == Purchase.PurchaseState.PURCHASED) {
//                if (!purchase.isAcknowledged()) {
//                    AcknowledgePurchaseParams acknowledgePurchaseParams =
//                            AcknowledgePurchaseParams.newBuilder()
//                                    .setPurchaseToken(purchase.getPurchaseToken())
//                                    .build();
//                    billingClient.acknowledgePurchase(acknowledgePurchaseParams, new AcknowledgePurchaseResponseListener() {
//                        @Override
//                        public void onAcknowledgePurchaseResponse(@NonNull BillingResult billingResult) {
//                            UserPreferences.initUserPremium(MainActivity.this);
//                            analytics.logEvent("initialize_user_premium",null);
//                        }
//                    });
//                }
//            }
//
//        } else if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.USER_CANCELED) {
//            // Handle an error caused by a user cancelling the purchase flow.
//            Toast.makeText(this, R.string.erro_ao_comprar, Toast.LENGTH_SHORT).show();
//        } else {
//            // Handle any other error codes.
//            Toast.makeText(this, R.string.erro_ao_comprar, Toast.LENGTH_SHORT).show();
//        }
//    }
//
//    public boolean verificaCompraNoApp(){
//        boolean result = false;
//        List<Purchase> purchases = billingClient.queryPurchases(BillingClient.SkuType.INAPP).getPurchasesList();
//
//        if (purchases != null) {
//            for (Purchase pr:purchases) {
//                if (pr.getPurchaseState() == Purchase.PurchaseState.PURCHASED){
//                    if(pr.isAcknowledged()){
//                        //já comprou
//                        result = true;
//                        UserPreferences.initUserPremium(this);
//                    }else{
//                        //ativar compra
//                        AcknowledgePurchaseParams acknowledgePurchaseParams =
//                                AcknowledgePurchaseParams.newBuilder()
//                                        .setPurchaseToken(pr.getPurchaseToken())
//                                        .build();
//                        billingClient.acknowledgePurchase(acknowledgePurchaseParams, new AcknowledgePurchaseResponseListener() {
//                            @Override
//                            public void onAcknowledgePurchaseResponse(@NonNull BillingResult billingResult) {
//                                UserPreferences.initUserPremium(MainActivity.this);
//                                analytics.logEvent("initialize_user_premium",null);
//                            }
//                        });
//                    }
//                }
//
//            }
//        }
//
//        return result;
//
//    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase));
    }

    @Override
    public void onBackPressed() {
        if(webviewVisible){
            webView.setVisibility(View.INVISIBLE);
            hideVideoContainer();
        }else {
            if (menuOpcaoOpen) {
                menu_opcoes.closeMenu();
            } else if (videoContainerOpen) {
                hideVideoContainer();
            }
        else if (qiBottomDrawerAds != null) {
                qiBottomDrawerAds.show();
            }
        }
    }

    public void avaliar(){
        String caminhoApp = "https://play.google.com/store/apps/details?id=com.qiapps.superdownloaderig";
        Uri uri = Uri.parse(caminhoApp);
        Intent it = new Intent(Intent.ACTION_VIEW);
        it.setData(uri);
        startActivity(it);
    }

    public boolean validateRating(){
        int color = getResources().getColor(R.color.pink);
        if(!jaMostrouAvaliacao){
            boolean isShow = QIRating.show(this,color,getPackageName());
            if(isShow){
                jaMostrouAvaliacao = true;
            }
            return isShow;
        }
        return false;
    }

    private void requestPermission(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) { // Android 11+

        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) { // Android 6 a 10
            String[] permissions = {
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
            };

            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED &&
                    ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {

            } else {
                ActivityCompat.requestPermissions(this, permissions, REQUEST_CODE);
            }
        } else {
            // Para versões abaixo do Android 6, a permissão é concedida automaticamente na instalação

        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE) {
            if (grantResults.length > 0) {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            inicioFragment.forceDownload();
                        }
                    });

                } else {
                    Toast.makeText(this, R.string.habilitar_permissao_para_continuar, Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        if(qiNativeAds!=null) {
            qiNativeAds.destroy();
        }
        if(qiBottomDrawerAds!=null) {
            qiBottomDrawerAds.destroy();
        }
        super.onDestroy();
    }
}
