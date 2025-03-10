package com.qiapps.superdownloaderig.Adapter;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import com.qiapps.superdownloaderig.Activity.CustomApplication;
import com.qiapps.superdownloaderig.Activity.MainActivity;
import com.qiapps.superdownloaderig.Helper.FileManager;
import com.qiapps.superdownloaderig.Helper.ItemHelper;
import com.qiapps.superdownloaderig.Helper.UserPreferences;
import com.qiapps.superdownloaderig.Model.InstagramResource;
import com.qiapps.superdownloaderig.R;

public class LineAdapter extends RecyclerView.Adapter<LineHolder> {


    private ArrayList<InstagramResource> resources;
    private MainActivity activity;

    public LineAdapter(ArrayList<InstagramResource> resources, MainActivity activity) {
        this.resources = resources;
        this.activity = activity;
    }

    @NonNull
    @Override
    public LineHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new LineHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_video,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull LineHolder holder, int position) {

        final InstagramResource instagramResource = resources.get(position);
        ItemHelper.buildItemVideo(holder.itemView,instagramResource,activity);

        holder.itemView.findViewById(R.id.btn_opcoes).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                activity.openMenu(instagramResource);
            }
        });

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(FileManager.isFileExists(activity,instagramResource.getFilepath().split(";")[0])) {
                    activity.startPreviewActivity(instagramResource.getFilepath());
                    CustomApplication customApplication = (CustomApplication)activity.getApplication();
                    customApplication.showEntradaAds();
                }else{
                    Toast.makeText(activity, R.string.arquivo_removido_dispositivo, Toast.LENGTH_SHORT).show();
                }
            }
        });

    }


    @Override
    public int getItemCount() {
        return resources.size();
    }

    public void deleteItem(InstagramResource ir){

        int pos = resources.indexOf(ir);
        if(pos >=0) {
            resources.remove(pos);
            notifyItemRemoved(pos);
        }
    }
}
