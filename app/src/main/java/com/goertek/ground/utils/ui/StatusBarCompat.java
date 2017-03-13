package com.goertek.ground.utils.ui;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import com.goertek.ground.player.R;

/**
 * Created by peijucai on 2016/1/7.
 */
public class StatusBarCompat {
    private static final int INVALID_VAL = -1;
    private static final int COLOR_DEFAULT =  Color.parseColor("#FF000000");

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public static void compat(Activity activity, int statusColor)
    {
        if (null == activity) return;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window window = activity.getWindow();
            // Translucent status bar
            window.setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
        {
            if (statusColor != INVALID_VAL)
            {
                View contentView = activity.findViewById(R.id.status_bar);
                if (null != contentView){
                    ViewGroup.LayoutParams lp = contentView.getLayoutParams();
                    lp.height = getStatusBarHeight(activity);
                    contentView.setLayoutParams(lp);
                    contentView.setBackgroundColor(statusColor);
                }
                activity.getWindow().setStatusBarColor(statusColor);
            }
            return;
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT && Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP)
        {
            View contentView = activity.findViewById(R.id.status_bar);
            if (null != contentView){
                ViewGroup.LayoutParams lp = contentView.getLayoutParams();
                lp.height = getStatusBarHeight(activity);
                contentView.setLayoutParams(lp);
                contentView.setBackgroundColor(statusColor);
            }
        }
    }

    public static void setStatusBarColor(Activity activity, int statusColor){
        if (null == activity) return;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
        {
            if (statusColor != INVALID_VAL)
            {
                View contentView = activity.findViewById(R.id.status_bar);
                if (null != contentView)
                    contentView.setBackgroundColor(statusColor);
                activity.getWindow().setStatusBarColor(statusColor);
            }
            return;
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT && Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP)
        {
            View contentView = activity.findViewById(R.id.status_bar);
            if (null != contentView)
                contentView.setBackgroundColor(statusColor);
        }
    }

    public static void showStatusBar(Activity activity, boolean bShow){
        if (null == activity) return;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            View contentView = activity.findViewById(R.id.status_bar);
            if (null != contentView){
                contentView.setVisibility(bShow ? View.VISIBLE : View.GONE);
            }
        }

    }

    public static int getStatusBarHeight(Context context)
    {
        int result = 0;
        int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0)
        {
            result = context.getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }
}
