package com.qiapps.superdownloaderig.Fragment;


import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.analytics.FirebaseAnalytics;

import com.qiapps.superdownloaderig.Activity.MainActivity;
import com.qiapps.superdownloaderig.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class SettingsFragment extends Fragment {

    private ViewGroup avaliar,status_saver,compartilhar,feedback,mais_apps,premium,reativar,sticker_maker;
    private View view;
    private MainActivity activity;
    private FirebaseAnalytics analytics;
    private TextView price;

    public SettingsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_settings, container, false);

        avaliar = view.findViewById(R.id.vg_avaliar);
        status_saver = view.findViewById(R.id.vg_status_saver);
        sticker_maker = view.findViewById(R.id.vg_sticker_maker);
        compartilhar = view.findViewById(R.id.vg_compartilhar);
        feedback = view.findViewById(R.id.vg_feedback);
        mais_apps = view.findViewById(R.id.vg_mais_apps);
        premium = view.findViewById(R.id.vg_premium);
        price = view.findViewById(R.id.price);
        reativar = view.findViewById(R.id.vg_reativar);

        activity = (MainActivity)getActivity();
        analytics = activity.getAnalytics();

        //String pr = activity.getPrice();
//        if(!pr.equals("")) {
//            price.setText(pr);
//        }

        avaliar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                activity.avaliar();
                if(analytics!=null){
                    analytics.logEvent("clicou_opção_avaliar",null);
                }
            }
        });

        status_saver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String caminhoApp = "https://play.google.com/store/apps/details?id=br.com.qiapps.qistatussaver";
                Uri uri = Uri.parse(caminhoApp);
                Intent it = new Intent(Intent.ACTION_VIEW);
                it.setData(uri);
                startActivity(it);

                if(analytics!=null){
                    analytics.logEvent("clicou_opção_status_saver",null);
                }
            }
        });

        sticker_maker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String caminhoApp = "https://play.google.com/store/apps/details?id=com.qiapps.stickermaker";
                Uri uri = Uri.parse(caminhoApp);
                Intent it = new Intent(Intent.ACTION_VIEW);
                it.setData(uri);
                startActivity(it);

                if(analytics!=null){
                    analytics.logEvent("clicou_opção_sticker_maker",null);
                }
            }
        });

        compartilhar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String link = "https://play.google.com/store/apps/details?id=com.qiapps.superdownloaderig";
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("text/plain");
                intent.putExtra(Intent.EXTRA_TEXT,link);
                startActivity(Intent.createChooser(intent,"Compartilhar com:"));
                if(analytics!=null){
                    analytics.logEvent("clicou_opção_compartilhar",null);
                }
            }
        });

        feedback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = "qiapps.contato@gmail.com";
                String assunto = "Feedback para o app InstaDownloader";
                Intent intent = new Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:" + email));
                intent.putExtra(Intent.EXTRA_SUBJECT,assunto);
                startActivity(Intent.createChooser(intent,"Enviar email com..."));
            }
        });

        mais_apps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String caminhoDesenvolvedor = "https://play.google.com/store/apps/developer?id=QIApps";
                Uri uri = Uri.parse(caminhoDesenvolvedor);
                Intent it = new Intent(Intent.ACTION_VIEW);
                it.setData(uri);
                startActivity(it);

                if(analytics!=null){
                    analytics.logEvent("clicou_opção_mais_apps",null);
                }
            }
        });

        premium.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //activity.purchaseItem();
            }
        });

        reativar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                if(activity.verificaCompraNoApp()){
//                    Toast.makeText(activity, R.string.conta_reativada_com_sucesso, Toast.LENGTH_SHORT).show();
//                }else{
//                    Toast.makeText(activity, R.string.compra_nao_encontrada, Toast.LENGTH_SHORT).show();
//                }
            }
        });

        return view;
    }

}
