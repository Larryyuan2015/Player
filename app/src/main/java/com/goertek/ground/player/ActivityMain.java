package com.goertek.ground.player;

import android.app.Activity;
import android.app.FragmentManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;

import com.goertek.ground.utils.SysUtil;
import com.goertek.ground.utils.ui.LocalBroadcastHelper;
import com.goertek.ground.utils.ui.StatusBarCompat;
import com.trello.rxlifecycle.components.support.RxFragmentActivity;

//RxFragmentActivity
public class ActivityMain extends RxFragmentActivity {
    private final static String TAG = "ActivityMain";
    public static String RTSPURL="";
    public static String UDPURL="udp://@:1234";
    private EditText text_rtsp;
    private Button btn_play,btn_cancle;

//    private FragmentCamera fgCamera;
//    private FragmentManager fragmentManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 去除title
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
//                WindowManager.LayoutParams.FLAG_FULLSCREEN);
//        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);
        text_rtsp=(EditText)findViewById(R.id.rtspurl);
        RTSPURL=text_rtsp.getText().toString();
        registerShowCameraBroadcast();
        StatusBarCompat.showStatusBar(this, false);
/*        btn_play=(Button)findViewById(R.id.btn_play);
        btn_cancle=(Button)findViewById(R.id.btn_cancle);
        btn_play.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                RTSPURL=text_rtsp.getText().toString();
                Intent i = new Intent(ActivityMain.this, FragmentCamera.class);
                startActivity(i);
                finish();
            }
        });

        btn_cancle.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                finish();
            }
        });*/


//        fragmentManager = getFragmentManager();
//        if (fgCamera == null) {
//            fgCamera = new FragmentCamera();
//            fragmentManager.beginTransaction().add(R.id.cameralayout_container, fgCamera).commit();
//        }
    }

    private final BroadcastReceiver mCloseReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(final Context context, Intent intent) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    if(!SysUtil.isAppForeground(context)){
                        return;
                    }
                    try {
                        goFragmentCamera();
                    }catch(Exception e){
                        Log.e(TAG,"mCloseReceiver goFragmentEquipment",e);
                    }
                }
            },500);

        }
    };

    private void registerShowCameraBroadcast(){
        LocalBroadcastHelper.registerShowOnRoadBroadcast(this, mCloseReceiver);
    }

    private void unregisterShowCameraBroadcast(){
        LocalBroadcastHelper.unregisterLocalBroadcast(this, mCloseReceiver);
    }
    private void goFragmentCamera() {
        FragmentMain fgMain = getFragmentMain();

        if (fgMain != null){
            fgMain.goFragmentCamera();
        }

        Log.i(TAG, "goFragmentEquipment()");
    }


    public FragmentMain.FRAGMENT_PAGE_ID getCurrentFragmentPageId() {
        FragmentMain fgMain = getFragmentMain();
        if (null == fgMain) return FragmentMain.FRAGMENT_PAGE_ID.UNKNOWN;
        return fgMain.getCurrentFragmentPageId();
    }

    protected FragmentMain getFragmentMain() {
        return (FragmentMain) getSupportFragmentManager().findFragmentById(R.id.fragment);
    }

    public final FragmentCamera getFragmentCamera() {
        return (FragmentCamera) getFragmentPage(FragmentMain.FRAGMENT_PAGE_ID.CAMERA);
    }


    private Fragment getFragmentPage(FragmentMain.FRAGMENT_PAGE_ID pageId) {
        FragmentMain fgMain = getFragmentMain();
        if (null == fgMain) return null;

        return fgMain.getFragmentPage(pageId);
    }


    @Override
    protected void onDestroy() {
        try {
            super.onDestroy();
            Log.i(TAG, "onDestroy()");
            unregisterShowCameraBroadcast();
//            if (mCheckPermissionHelper != null) {
//                mCheckPermissionHelper.destroy();
//            }
        } catch (Throwable e) {
            Log.e(TAG, "onDestroy ", e);
        }

    }

}
