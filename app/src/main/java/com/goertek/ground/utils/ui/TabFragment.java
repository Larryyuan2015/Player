package com.goertek.ground.utils.ui;


import android.app.Fragment;
import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

//import com.trello.rxlifecycle.components.RxFragment;
import com.trello.rxlifecycle.components.support.RxFragment;

public abstract class TabFragment extends RxFragment {
    protected boolean isVisible = false, isEnter = false;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                onDelayCreate();
            }
        });
        return null;
    }

    @Override
    public void onPause() {
        if(isVisible){
            onLeave();
        }
        super.onPause();
    }

    @Override
    public void onResume() {
        if(isVisible){
            onEnter();
        }
        super.onResume();
    }

    @Override
    public void onDestroyView(){
        if(isVisible){
            onLeave();
        }
        super.onDestroyView();
    }

    /**
     * 在这里实现Fragment数据的缓加载.
     *
     * @param isVisibleToUser
     */
    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            onVisible();
        } else {
            onInvisible();
        }

        if (isVisible != isVisibleToUser){
            isVisible = isVisibleToUser;
            if (isVisible){
                onEnter();
            }
            else
                onLeave();
        }
    }

    public void setVisibleOnlyVariate(boolean isVisible){
        this.isVisible = isVisible;
    }

    public void setVisible(boolean isVisible){
        if (this.isVisible == isVisible) return;
        this.isVisible = isVisible;
        if (isVisible){
            onEnter();
        }
        else
            onLeave();
    }

    /**
     * Called when the hidden state (as returned by {@link #isHidden()} of
     * the fragment has changed.  Fragments start out not hidden; this will
     * be called whenever the fragment changes state from that.
     *
     * @param hidden True if the fragment is now hidden, false if it is not
     *               visible.
     */
    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);

        if (hidden) {
            onInvisible();
        } else {
            onVisible();
        }
    }

    private void onDelayCreate() {
        if (getActivity() == null)
            return;
        if (getUserVisibleHint()) {
            onRealVisible();
        }
    }

    protected void onVisible() {
        if (getView() == null) {
            return;
        }
        onRealVisible();
    }

    public void onRealVisible() {
    }


    public void onInvisible() {

    }

    private void onEnter() {
        if(!isEnter){
            onPageStart();
            isEnter = true;
        }
    }


    private void onLeave() {
        if(isEnter){
            onPageEnd();
            isEnter = false;
        }
    }

    //打点使用 页面进入
    public void onPageStart(){

    }

    public void onPageEnd(){

    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return false;
    }

    public boolean onTouchEvent(MotionEvent event) {
        return false;
    }
}
