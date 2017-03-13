package com.goertek.ground.utils.ui;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Parcelable;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;


public class LocalBroadcastHelper {
    private static final String TAG = "LocalBroadcastHelper";

    public static final String ARG_DATA = "ARG_DATA";

    public static final String ARG_DATA1 = "ARG_DATA1";

    /**
     * 刷新在路上页面
     */
    public static final String LOCAL_BROADCAST_NORMAL_PHOTO_LIST_NEED_REFRESH = "LOCAL_BROADCAST_NORMAL_PHOTO_LIST_NEED_REFRESH";

    /**
     * 刷新标签列表页面
     */
    public static final String LOCAL_BROADCAST_TAG_PHOTO_LIST_NEED_REFRESH = "LOCAL_BROADCAST_TAG_PHOTO_LIST_NEED_REFRESH";

    /**
     *刷新本地照片页面
     */
    public static final String LOCAL_BROADCAST_LOCAL_PHOTO_LIST_NEED_REFRESH = "LOCAL_BROADCAST_LOCAL_PHOTO_LIST_NEED_REFRESH";


    /**
     *关闭除了ActivityMain以外的所有页面包括dialog和fragment
     */
    public static final String LOCAL_BROADCAST_CLOSE_ALL = "LOCAL_BROADCAST_CLOSE_ALL";

    /**
     *通知ActivityMain显示在路上页面
     */
    public static final String LOCAL_BROADCAST_SHOW_ON_ROAD = "LOCAL_BROADCAST_SHOW_ON_ROAD";




    //--------------------------------------------------------------------------------------
    public static void sendShowOnRoadBroadcast(Context context){
        sendLocalBroadcast(context, LOCAL_BROADCAST_SHOW_ON_ROAD);
    }

    public static void registerShowOnRoadBroadcast(Context context, BroadcastReceiver receiver){
        registerLocalBroadcast(context, LOCAL_BROADCAST_SHOW_ON_ROAD, receiver);
    }

    //--------------------------------------------------------------------------------------
    public static void sendCloseAllBroadcast(Context context){
        sendLocalBroadcast(context, LOCAL_BROADCAST_CLOSE_ALL);
    }

    public static void registerCloseAllBroadcast(Context context, BroadcastReceiver receiver){
        registerLocalBroadcast(context, LOCAL_BROADCAST_CLOSE_ALL, receiver);
    }


    //--------------------------------------------------------------------------------------
    public static void sendLocalPhotoListNeedRefreshBroadcast(Context context){
        sendLocalBroadcast(context, LOCAL_BROADCAST_LOCAL_PHOTO_LIST_NEED_REFRESH);
    }

    public static void registerLocalPhotoListNeedRefreshBroadcast(Context context, BroadcastReceiver receiver){
        registerLocalBroadcast(context, LOCAL_BROADCAST_LOCAL_PHOTO_LIST_NEED_REFRESH, receiver);
    }

    //--------------------------------------------------------------------------------------
    public static void sendNormalPhotoListNeedRefreshBroadcast(Context context){
        sendLocalBroadcast(context, LOCAL_BROADCAST_NORMAL_PHOTO_LIST_NEED_REFRESH);
    }

    public static void registerNormalPhotoListNeedRefreshBroadcast(Context context, BroadcastReceiver receiver){
        registerLocalBroadcast(context, LOCAL_BROADCAST_NORMAL_PHOTO_LIST_NEED_REFRESH, receiver);
    }

    //--------------------------------------------------------------------------------------
    public static void sendTagPhotoListNeedRefreshBroadcast(Context context){
        sendLocalBroadcast(context, LOCAL_BROADCAST_TAG_PHOTO_LIST_NEED_REFRESH);
    }

    public static void registerTagPhotoListNeedRefreshBroadcast(Context context, BroadcastReceiver receiver){
        registerLocalBroadcast(context, LOCAL_BROADCAST_TAG_PHOTO_LIST_NEED_REFRESH, receiver);
    }

    //--------------------------------------------------------------------------------------

    public static void sendCommentChangeBroadcast(Context context, Intent intent){
        if (null == intent) return;
        sendLocalBroadcast(context, intent);
    }

    //--------------------------------------------------------------------------------------
    private static void registerLocalBroadcast(Context context, String localBroadcast, BroadcastReceiver receiver){
        IntentFilter filter = new IntentFilter();
        filter.addAction(localBroadcast);
        try {
            LocalBroadcastManager.getInstance(context).registerReceiver(receiver, filter);
        } catch (Exception e) {
            Log.e(TAG, "registerLocalBroadcast", e);
        }
    }

    public static void unregisterLocalBroadcast(Context context, BroadcastReceiver receiver) {
        try {
            Log.d(TAG, "unregisterLocalBroadcast " + receiver);
            LocalBroadcastManager.getInstance(context).unregisterReceiver(receiver);
        } catch (Exception e) {
            Log.e(TAG, "registerLocalBroadcast", e);
        }
    }

    private static void sendLocalBroadcast(Context context, String localBroadcast, Parcelable data){
        Log.d(TAG, "sendLocalBroadcast " + localBroadcast);
        Intent intent = new Intent(localBroadcast);
        intent.putExtra(ARG_DATA, data);
        LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
    }

    private static void sendLocalBroadcast(Context context, String localBroadcast){
        Log.d(TAG,"sendLocalBroadcast "+localBroadcast);
        LocalBroadcastManager.getInstance(context).sendBroadcast(new Intent(localBroadcast));
    }

    private static void sendLocalBroadcast(Context context, Intent intent){
        Log.d(TAG,"sendLocalBroadcast intent = "+intent);
        LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
    }

}
