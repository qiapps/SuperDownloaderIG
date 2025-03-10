package com.qiapps.superdownloaderig.ViewPager;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.qiapps.superdownloaderig.Fragment.HistoricoFragment;
import com.qiapps.superdownloaderig.Fragment.InicioFragment;
import com.qiapps.superdownloaderig.Fragment.SettingsFragment;

public class MyPagerAdapter extends FragmentStateAdapter {

    HistoricoFragment historicoFragment;
    InicioFragment inicioFragment;

    public MyPagerAdapter(@NonNull FragmentActivity fragmentActivity,HistoricoFragment historicoFragment,InicioFragment inicioFragment) {
        super(fragmentActivity);
        this.historicoFragment = historicoFragment;
        this.inicioFragment = inicioFragment;
    }



    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position){
            case 0:
                return inicioFragment;
            case 1:
                return historicoFragment;
            case 2:
                return new SettingsFragment();
            default: return new InicioFragment();
        }
    }

    @Override
    public int getItemCount() {
        return 3;
    }
}
