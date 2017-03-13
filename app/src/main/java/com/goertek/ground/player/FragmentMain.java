package com.goertek.ground.player;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.goertek.ground.utils.ui.TabFragment;
import com.trello.rxlifecycle.components.support.RxFragment;


public class FragmentMain extends TabFragment implements
        View.OnClickListener
{
    private static final String TAG = "FragmentMain";

    private FragmentCamera fgCamera;
//    private FragmentMap fgMap;
//    private FragmentGallery fgGallery;
//    private FragmentSetting fgSetting;
    private FRAGMENT_PAGE_ID mCurrentShowPage;
    private LinearLayout mTabBtnCommunity;
    private ActivityMain mActivityMain;
    private View mTabBottom;
    private LinearLayout mTabBtnCamera;
    private LinearLayout mTabBtnGallery;
    private LinearLayout mTabBtnSettings;


    public FragmentMain() {
    }

    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mActivityMain = (ActivityMain)activity;
        } catch (ClassCastException e) {
            //
        }
    }

    private void showPage(Fragment page) {
        //防止有人在后台时调用导致崩溃
        try {
            FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
            transaction.hide(fgCamera).show(page);
            transaction.commit();

            if (fgCamera != page)
                fgCamera.setVisible(false);
            ((TabFragment)page).setVisible(true);
        } catch (Exception e){
            Log.i(TAG, "showPage" , e);
        }

    }

    private void showPage(FRAGMENT_PAGE_ID id) {
//        Fragment lastFragment = getFragmentPage(mCurrentShowPage);
//        if (null != lastFragment){
//            lastFragment.setUserVisibleHint(false);
//        }
        mCurrentShowPage = id;
        Fragment fragment = getFragmentPage(id);
        if (null != fragment){
            showPage(fragment);
//            fragment.setUserVisibleHint(true);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView savedInstanceState" + savedInstanceState);
        super.onCreateView(inflater, container, savedInstanceState);

        View view = inflater.inflate(R.layout.fragment_main, container, false);

        initViewPage(view);
        initTabBtn(view);

        mCurrentShowPage = FRAGMENT_PAGE_ID.CAMERA;
        showPage(fgCamera);
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        Fragment fragment = getCurrentFragment();

        if(fragment != null){
            fragment.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    public Fragment getCurrentFragment(){
        return getFragmentPage(getCurrentFragmentPageId());
    }

    @Override
    public void onResume() {
        super.onResume();

        onClickPublishVideo(mTagId, mTagName);

    }

    @Override
    public void onPause() {
        super.onPause();
    }

    private void initViewPage(View view) {
        FragmentManager fragmentManager = getChildFragmentManager();

        if (fragmentManager == null)
            return;

        fgCamera = (FragmentCamera) fragmentManager.findFragmentById(R.id.id_fragment_camera);
//        view.findViewById(R.id.btn_tab_camera).setOnClickListener(this);
//        fgMAP = (FragmentMAP) fragmentManager.findFragmentById(R.id.id_fragment_map);
//        fgGallery = (FragmentGallery) fragmentManager.findFragmentById(R.id.id_fragment_gallery);
//        fgSetting = (FragmentSetting)fragmentManager.findFragmentById(R.id.id_fragment_setting);

    }

    private void initTabBtn(View view) {
//        mTabBottom = view.findViewById(R.id.id_status_top);
        mTabBtnCamera = (LinearLayout) view.findViewById(R.id.id_tab_camera);
//        mTabBtnMAP = (LinearLayout)view.findViewById(R.id.id_tab_map);
//        mTabBtnGallery = (LinearLayout) view.findViewById(R.id.id_tab_gallery);
//        mTabBtnSettings = (LinearLayout) view.findViewById(R.id.id_tab_setting);

        mTabBtnCamera.setOnClickListener(this);
//        mTabBtnGallery.setOnClickListener(this);
//        mTabBtnMap.setOnClickListener(this);
//        mTabBtnSettings.setOnClickListener(this);
    }

    protected void selectTabBtn(int nId) {
//        mTabBtnCamera.setSelected((R.id.btn_tab_camera == nId) || (R.id.id_tab_camera == nId));

    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
//            case R.id.id_tab_gallery:
            default:
                selectTabBtn(id);
                Log.i(TAG, "onClick tab id=" + String.valueOf(btnId2PagePosition(id)));
                FRAGMENT_PAGE_ID pageId = btnId2PageId(id);
//                gotoPage(pageId);
                gotoPage(FRAGMENT_PAGE_ID.CAMERA);
                break;
        }
    }

    public void goFragmentCamera(){
        try {
            gotoPage(FRAGMENT_PAGE_ID.CAMERA);
            showTabButtons(true);
        }catch (Throwable e){
            Log.e(TAG, "gotoPage", e);
        }
    }


    public void gotoPage(FRAGMENT_PAGE_ID id) {
        showPage(id);
        //int page_id = id.ordinal();
        //setViewPagerCurrentItem(page_id);
        Log.i(TAG, "gotoPage id=" + id);
    }

    private FRAGMENT_PAGE_ID btnId2PageId(int nId) {
        switch (nId) {
            case R.id.id_tab_camera:
                return FRAGMENT_PAGE_ID.CAMERA;
//            case R.id.id_tab_map:
//                return FRAGMENT_PAGE_ID.MAP;
//            case R.id.id_tab_gallery:
//                return FRAGMENT_PAGE_ID.GALLERY;
//            case R.id.id_tab_setting:
//                return FRAGMENT_PAGE_ID.SETTING;
            default:
            return FRAGMENT_PAGE_ID.UNKNOWN;
        }
    }

    private int btnId2PagePosition(int nId) {
        int nPos;
        switch (nId) {

            case R.id.id_tab_camera:
                nPos = 0;
                break;
//            case R.id.id_tab_map:
//                nPos = 1;
//                break;
//            case R.id.id_tab_gallery:
//                nPos = 2;
//                break;
//            case R.id.id_tab_setting:
//                nPos = 3;
//                break;
            default:
                nPos = -1;
        }

        return nPos;
    }

    public FRAGMENT_PAGE_ID getCurrentFragmentPageId() {
        return mCurrentShowPage;

    }

    public final Fragment getFragmentPage(FRAGMENT_PAGE_ID pageId) {
        if(pageId == null){
            return null;
        }
        switch (pageId) {
            case CAMERA:
                return fgCamera;
//            case MAP:
//                return fgMAP;
//            case GALLERY:
//                return fgGallery;
//            case SETTING:
//                return fgSetting;
            default:
                Log.e(TAG, "getFragmentPage ID unknown:" + pageId, new RuntimeException("ID(" + pageId + ") unknown"));
                break;
        }
        return  null;
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        Fragment fragment = getFragmentPage(getCurrentFragmentPageId());
        if (null != fragment && fragment instanceof TabFragment)
        {
            if (((TabFragment)fragment).onKeyDown(keyCode, event))
                return true;
        }
        return false;
    }


    private void startAutoConnectCamera(final FragmentCamera fragmentCamera){
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                //FragmentCamera.startAutoConnect();
            }
        },0);
    }


    private String mTagId, mTagName;

    public void onClickFastShare(String tagId, String tagName){
        mTagId = tagId;
        mTagName = tagName;
        showLocalPhoto(tagId, tagName);
    }

    public void onClickPublishVideo(String tagId, String tagName) {
        mTagId = tagId;
        mTagName = tagName;
        showLocalVideoPage(tagId, tagName);

    }


    private void showLocalPhoto(String tagId, String tagName){
//        showLocalPhoto(true, tagId, tagName);
    }

    private void showLocalVideoPage(String tagId, String tagName){
//        Intent intent = new Intent(getContext(), ActivityLocalVideo.class);
//        intent.putExtra(ActivityLocalVideo.ARG_TAG_ID, tagId);
//        intent.putExtra(ActivityLocalVideo.ARG_TAG_NAME, tagName);
//        startActivity(intent);
    }



    private boolean mLoginStateChanged = false;


//    @Override
//    public void doDisconnectEquipment() {
//        DrApplication.getApp().disconnectEquipment(!Network.isSupportVpn());
//    }

    public void showTabButtons(boolean show) {
        if (mTabBottom != null) {
            Log.d(TAG,"3344showTabButtons show = "+show);
            mTabBottom.setVisibility(show ? View.VISIBLE : View.GONE);
        }
    }


    public enum FRAGMENT_PAGE_ID {
        CAMERA,
        MAP,
        GALLERY,
        SETTING,
        UNKNOWN,
    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        Log.i(TAG, "onSaveInstanceState");
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onViewStateRestored(Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);

        Log.i(TAG, "onViewStateRestored");
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if ( (fgCamera != null) && fgCamera.isVisible() )
            if (fgCamera.onTouchEvent(event))
                return true;
        return super.onTouchEvent(event);
    }
}
