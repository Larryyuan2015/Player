package com.goertek.ground.utils.ui;

import android.annotation.SuppressLint;
import android.os.Build;
import android.os.Handler;
import android.view.View;

/**
 * Created  on 16-1-17.
 * copy from android studio 1.5.1 FullscreenActivity sample.
 */
public class FullScreenHelper {

    public FullScreenHelper(View contentView) {
        mContentView = contentView;
    }

    /**
     * Some older devices needs a small delay between UI widget updates
     * and a change of the status and navigation bar.
     */
    private static final int UI_ANIMATION_DELAY = 300;
    private final Handler mHideHandler = new Handler();
    private View mContentView;
    private final Runnable mHidePart2Runnable = new Runnable() {
        @SuppressLint("InlinedApi")
        @Override
        public void run() {
            // Delayed removal of status and navigation bar

            // Note that some of these constants are new as of API 16 (Jelly Bean)
            // and API 19 (KitKat). It is safe to use them, as they are inlined
            // at compile-time and do nothing on earlier devices.
            int newVis = View.SYSTEM_UI_FLAG_LOW_PROFILE | View.SYSTEM_UI_FLAG_FULLSCREEN;

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                newVis |= View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        ;
            }
            // | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
            //该flag表示沉浸式状态栏，能自动隐藏
            mContentView.setSystemUiVisibility(newVis);
        }
    };


    public void doFullscreen() {
        mHideHandler.postDelayed(mHidePart2Runnable, UI_ANIMATION_DELAY);
    }

    @SuppressLint("InlinedApi")
    public void restoreNormal() {
        // Show the system bar
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            mContentView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    /*| View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION*/);
        } else {
            mContentView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);
        }

        // Schedule a runnable to display UI elements after a delay
        mHideHandler.removeCallbacks(mHidePart2Runnable);
    }

}
