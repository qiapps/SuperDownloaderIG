package com.qiapps.superdownloaderig.Components;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;

import com.qiapps.superdownloaderig.Database.DatabaseManager;
import com.qiapps.superdownloaderig.Helper.FileManager;
import com.qiapps.superdownloaderig.Helper.MyDownloadManager;
import com.qiapps.superdownloaderig.Helper.ResourceHelper;
import com.qiapps.superdownloaderig.Model.InstagramResource;
import com.qiapps.superdownloaderig.R;

public class MenuOpcoes {


    private Activity activity;
    private InstagramResource instagramResource;
    private View menu;
    private MenuUtils menuUtils;
    private ViewGroup excluir,menu_container_itens;
    private float dp;
    private static final int MENU_HEIGHT = 330;

    public MenuOpcoes(Activity activity, InstagramResource instagramResource, MenuUtils menuUtils) {
        this.activity = activity;
        this.instagramResource = instagramResource;
        this.menuUtils = menuUtils;
        DisplayMetrics metrics = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(metrics);
        dp = metrics.density;
    }

    public View create(){
        menu = activity.getLayoutInflater().inflate(R.layout.menu_opcoes,null);
        menu.setVisibility(View.GONE);

        final ImageView close = menu.findViewById(R.id.btn_close);
        ViewGroup compartilhar = menu.findViewById(R.id.menu_share);
        ViewGroup ver_instagram = menu.findViewById(R.id.menu_ir_instagram);
        ViewGroup repostar = menu.findViewById(R.id.menu_repost);
        excluir = menu.findViewById(R.id.menu_delete);
        ViewGroup copiarLink = menu.findViewById(R.id.menu_copyUrl);
        ViewGroup download = menu.findViewById(R.id.menu_download);
        View container = menu.findViewById(R.id.menu_container);
        menu_container_itens = menu.findViewById(R.id.menu_container_itens);

        menu_container_itens.getBackground().setTint(ResourceHelper.getColor(activity,R.color.white));

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                closeMenu();
            }
        });

        container.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                closeMenu();
            }
        });

        compartilhar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(FileManager.isUrlVideo(instagramResource.getFilepath().split(";")[0])){

                    FileManager.shareVideo(activity,instagramResource.getFilepath());
                }else{
                    FileManager.shareImg(activity,instagramResource.getFilepath());
                }
                closeMenu();
                Toast.makeText(activity, R.string.aguarde, Toast.LENGTH_SHORT).show();
            }
        });

        ver_instagram.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FileManager.openInstagramWithUrl(activity,instagramResource.getInstagramPath());
                closeMenu();
            }
        });

        repostar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //FileManager.repostInstagram(activity,instagramResource.getFilepath());
                FileManager.openMediaInGallery(activity,instagramResource.getFilepath());
                closeMenu();
            }
        });

        excluir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                String[]filePaths = instagramResource.getFilepath().split(";");
//                for (String f:filePaths) {
//                    FileManager.deleteFile(new File(f),activity);
//                }
                DatabaseManager.deleteByPath(activity,instagramResource.getFilepath());
                menuUtils.onItemDelete();
                Toast.makeText(activity, R.string.excluido_com_sucesso, Toast.LENGTH_SHORT).show();
                closeMenu();

            }
        });

        copiarLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ClipboardManager clipboardManager = (ClipboardManager) activity.getSystemService(Context.CLIPBOARD_SERVICE);
                clipboardManager.setPrimaryClip(ClipData.newPlainText("text",instagramResource.getInstagramPath()));
                Toast.makeText(activity, R.string.link_copiado, Toast.LENGTH_SHORT).show();
                closeMenu();
            }
        });

        if(!FileManager.isFileExists(activity,instagramResource.getFilepath().split(";")[0]) && !MyDownloadManager.isStories(instagramResource.getInstagramPath())){
            compartilhar.setVisibility(View.GONE);
            repostar.setVisibility(View.GONE);
            download.setVisibility(View.VISIBLE);
            download.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    menuUtils.onClickDownload(instagramResource.getInstagramPath());
                    closeMenu();
                }
            });
        }

        if(MyDownloadManager.isStories(instagramResource.getInstagramPath())){
            ver_instagram.setVisibility(View.GONE);
        }

        showMenu();

        //repostar.setVisibility(View.GONE);

        return menu;
    }

    public void hideExcluir(){
        if(excluir != null) {
            excluir.setVisibility(View.GONE);
        }
    }

    public void closeMenu(){
        ObjectAnimator animation = ObjectAnimator.ofFloat(menu_container_itens, "translationY", dp*MENU_HEIGHT);
        animation.setDuration(200);
        animation.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {

            }

            @Override
            public void onAnimationEnd(Animator animator) {
                menu.setVisibility(View.GONE);
                menuUtils.onMenuHide();
            }

            @Override
            public void onAnimationCancel(Animator animator) {

            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        });
        animation.start();
    }

    public void showMenu(){
        menu.setVisibility(View.VISIBLE);
        menu_container_itens.setTranslationY(dp * MENU_HEIGHT);
        ObjectAnimator animation = ObjectAnimator.ofFloat(menu_container_itens, "translationY", 0f);
        animation.setDuration(200);
        animation.start();
        menuUtils.onShowMenu();
    }

    public void configDarkMode(){

        menu_container_itens.getBackground().setTint(ResourceHelper.getColor(activity,R.color.black2));

        ImageView imgshare = menu.findViewById(R.id.img_share);
        imgshare.setImageResource(R.drawable.ic_share_2);

        ImageView imgir = menu.findViewById(R.id.img_ir_instagram);
        imgir.setImageResource(R.drawable.ic_public_2);

        ImageView imgrepeat = menu.findViewById(R.id.img_repost);
        imgrepeat.setImageResource(R.drawable.ic_repeat_2);

        ImageView imgcopy = menu.findViewById(R.id.img_copyUrl);
        imgcopy.setImageResource(R.drawable.ic_link_2);
    }


    public interface MenuUtils{
        public abstract void onItemDelete();
        public abstract void onClickDownload(String url);
        public abstract void onMenuHide();
        public abstract void onShowMenu();
    }
}
