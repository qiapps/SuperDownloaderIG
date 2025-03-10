package com.qiapps.superdownloaderig.Helper;

import android.content.Context;
import android.widget.TextView;

import com.qiapps.superdownloaderig.R;

public class ResourceHelper {

    public static int getColor(Context context, int colorResource){

        int col = 0;
        col = context.getResources().getColor(colorResource);

        return col;

    }

    public static void setTextColorPink(Context context, TextView ...textViews){
        for (TextView t:textViews) {
            t.setTextColor(getColor(context, R.color.pink));
        }
    }


}
