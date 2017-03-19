package com.goertek.ground.player;


import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.goertek.ground.utils.ui.FullScreenHelper;
import com.goertek.ground.utils.ui.StatusBarCompat;
import com.goertek.ground.utils.ui.TabFragment;

public class FragmentCamera extends TabFragment implements View.OnClickListener {
	private static final String TAG = "FragmentCamera";
	private CameraLiveView mCameraLiveView;

	private Context mContext = null;
	private LinearLayout mTabBtnRecVideo;
	private LinearLayout mTabBtnShareVideo;
	private LinearLayout mTabBtnScreenShot;
	private View mLayoutLandscape;
	private View mRootView;
	private boolean mFullScreen = false;
	private FullScreenHelper mFullScreenHelper;
	private String mOutputPath;
	private Button btn_play;

	@Override
	public void onAttach(Context context) {
		super.onAttach(context);
		onAttachToContext(context);
	}

	/*
     * Called when the fragment attaches to the context
     */
	protected void onAttachToContext(Context context) {
		mContext = context;
	}

	private void initUIHider() {
		View contentView = mRootView.findViewById(R.id.surfaceview);
		mFullScreenHelper = new FullScreenHelper(contentView);
	}

//	@Override
//	public void onCreate(Bundle savedInstanceState) {
//		super.onCreate(savedInstanceState);
		//去除title
//		getActivity().getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
//				WindowManager.LayoutParams.FLAG_FULLSCREEN);
//		getActivity().getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
//		getActivity().requestWindowFeature(Window.FEATURE_NO_TITLE);

//		getActivity().setContentView(R.layout.activity_main);
//		mCameraLiveView=new CameraLiveView(mContext);
//		getActivity().setContentView(mCameraLiveView);
//	}

	public FragmentCamera() {
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {
		Log.d(TAG, "onCreateView savedInstanceState" + savedInstanceState);
		super.onCreateView(inflater, container, savedInstanceState);

		mRootView = inflater.inflate(R.layout.fragment_camera, container, false);
		mCameraLiveView = (CameraLiveView) mRootView.findViewById(R.id.surfaceview);

		initCameraPage(mRootView);
		initUIHider();
		initTabBtn(mRootView);

		//处理横屏的问题
		if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE){
			setFullScreen(true);
		}
		else {
			showLandscapeUI(false);
		}

		mRootView.findViewById(R.id.btnBack_landscape).setOnClickListener(
				new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						if (null != getActivity()) {
							getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
						}
					}
				}
		);

		View btn_fullscreen = mRootView.findViewById(R.id.btn_fullscreen);
		if (null != btn_fullscreen) {
			btn_fullscreen.setOnClickListener(
					new View.OnClickListener() {
						@Override
						public void onClick(View v) {
							if (null != getActivity()){
//								autoRestoreSystemOritentationSensor(true);
								Log.d(TAG, "setOnClickListener btn_fullscreen = true");
								getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
							}
						}
					});
		}

		mLayoutLandscape = mRootView.findViewById(R.id.layout_landscape);

		mOutputPath =(new StringBuilder(String.valueOf(Environment.getExternalStorageDirectory().getPath(
		)))).append("/").toString();
		return mRootView;
	}

	private void showLandscapeUI(boolean bshow){

		mLayoutLandscape = mRootView.findViewById(R.id.layout_landscape);

		if (null != mLayoutLandscape)
			mLayoutLandscape.setVisibility(bshow ? View.VISIBLE : View.GONE);

		View btn_fullscreen = mRootView.findViewById(R.id.btn_fullscreen);
		if (null != btn_fullscreen)
			btn_fullscreen.setVisibility(bshow ? View.GONE : View.VISIBLE);
	}

	private void initCameraPage(View rootView){
		if (null == rootView) return;

		View layout_url = mRootView.findViewById(R.id.layout_url);
		if (layout_url != null) {
			layout_url.setVisibility(View.VISIBLE);
		}

		View id_tab_camera = mRootView.findViewById(R.id.id_tab_camera);
		if (id_tab_camera != null) {
			id_tab_camera.setVisibility(View.VISIBLE);
			id_tab_camera.setOnClickListener(this);
		}

		btn_play = (Button)mRootView.findViewById(R.id.btn_play);
		if (btn_play != null) {
			btn_play.setVisibility(View.VISIBLE);
			btn_play.setOnClickListener(this);
		}

		View btn_stop = mRootView.findViewById(R.id.btn_cancle);
		if (btn_stop != null) {
			btn_stop.setVisibility(View.VISIBLE);
			btn_stop.setOnClickListener(this);
		}


//		getActivity().getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
//				WindowManager.LayoutParams.FLAG_FULLSCREEN);
//		getActivity().getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
//		getActivity().requestWindowFeature(Window.FEATURE_NO_TITLE);

	}

	private void initTabBtn(View view) {

		mTabBtnRecVideo = (LinearLayout) view.findViewById(R.id.btn_rec_video_landscape);
		mTabBtnShareVideo = (LinearLayout) view.findViewById(R.id.btn_take_share_landscape);
		mTabBtnScreenShot = (LinearLayout)view.findViewById(R.id.btn_take_photo_landscape);


		mTabBtnRecVideo.setOnClickListener(this);
		mTabBtnShareVideo.setOnClickListener(this);
		mTabBtnScreenShot.setOnClickListener(this);

	}

	public void setFullScreen(boolean bFullScreen) {
		if (mFullScreen == bFullScreen) {
			return;
		}
		mFullScreen = bFullScreen;

		if (mContext == null)
			return;
		if (getActivity().getWindow() == null)
			return;

		StatusBarCompat.showStatusBar(getActivity(), !bFullScreen);
		hideUIForFullScreen(bFullScreen);
		showLandscapeUI(bFullScreen);

		CameraLiveView cameraView = getCameraView();
		if (cameraView == null)
			return;

		cameraView.setFullScreen(bFullScreen);

		ViewGroup.LayoutParams lp = cameraView.getLayoutParams();
		if (mFullScreen) {
			Log.i("ffmpeglog", "setFullScreen= %d");
			lp.height = ViewGroup.LayoutParams.MATCH_PARENT;
			cameraView.setLayoutParams(lp);
		}
//		cameraView.invalidate();
	}

	private CameraLiveView getCameraView() {
		return mCameraLiveView;
	}

	public void hideUIForFullScreen(boolean fullScreen) {
		Log.d(TAG, "hideUIForFullScreen fullScreen = " + fullScreen);
		if (fullScreen) {
			mFullScreenHelper.doFullscreen();

			View layout_url = mRootView.findViewById(R.id.layout_url);
			if (layout_url != null) {
				layout_url.setVisibility(View.GONE);
			}

			View id_tab_camera = mRootView.findViewById(R.id.id_tab_camera);
			if (id_tab_camera != null) {
				id_tab_camera.setVisibility(View.GONE);
			}

		} else {
			mFullScreenHelper.restoreNormal();

			View layout_url = mRootView.findViewById(R.id.layout_url);
			if (layout_url != null) {
				layout_url.setVisibility(View.VISIBLE);
			}

			View id_tab_camera = mRootView.findViewById(R.id.id_tab_camera);
			if (id_tab_camera != null) {
				id_tab_camera.setVisibility(View.VISIBLE);
			}

		}
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);

		if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE)
		{
			Log.d(TAG, "onConfigurationChanged fullScreen = true");
			setFullScreen(true);
		}
		else {
			Log.d(TAG, "onConfigurationChanged fullScreen = false");
			setFullScreen(false);
		}
	}

	@Override
	public void onClick(View v){
		switch (v.getId()){
			case R.id.btn_rec_video_landscape:
				if(!mCameraLiveView.isRecording()) {
					doRecVideo();
					//显示录像时间
					Toast.makeText(getActivity(), "record started", Toast.LENGTH_SHORT).show();
				}
				else {
					mCameraLiveView.stopRecordVideo();
					//结束录像提示
					Toast.makeText(getActivity(), "record finished", Toast.LENGTH_SHORT).show();
				}
				break;
			case R.id.btn_take_share_landscape:
				if(!mCameraLiveView.isSharing()) {
					doShareVideo();
					//显示录像时间
					Toast.makeText(getActivity(), "share started", Toast.LENGTH_SHORT).show();
				}
				else {
					mCameraLiveView.stopShareVideo();
					//结束录像提示
					Toast.makeText(getActivity(), "share finished", Toast.LENGTH_SHORT).show();
				}
				break;
			case R.id.btn_take_photo_landscape:
				//doScreenShot();
				break;
			case R.id.btn_play:
			case R.id.id_tab_camera:
//				if(!mCameraLiveView.isPlaying()) {
					startPlay();
//					btn_play.setText(R.string.cancle);
//				}
//				else {
//					mCameraLiveView.stopPlay();
//					btn_play.setText(R.string.play);
//				}

				break;
			case R.id.btn_cancle:
				mCameraLiveView.stopPlay();
				mCameraLiveView.stopDrawThread();


			default:
				break;
		}
	}

	private void startPlay() {

		Log.e("zzz", "startPlay");

		setRecVideoButtonEnable(true);

		mCameraLiveView.startPlay(ActivityMain.RTSPURL);

		if (getActivity() != null) {
			getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
			getActivity().getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		}
	}

	private boolean setRecVideoButtonEnable(boolean bEnable) {
		if (null == mRootView) return false;

		View btnRecVideoLandscape = mRootView.findViewById(R.id.btn_rec_video_landscape);
		if (null == btnRecVideoLandscape) return false;
		btnRecVideoLandscape.setEnabled(bEnable);
		return true;
	}

	private VideoRecorderTask mVideoRecorderTask;

	private void doRecVideo() {

//		mVideoRecorderView.setEnabled(false);
//		cancelVideoRecorderTask();

		mVideoRecorderTask = new VideoRecorderTask();
		mVideoRecorderTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
//		mVideoRecorderView = null;
	}


	class VideoRecorderTask extends AsyncTask<Void, Void, String> {
		private View mView;

		public VideoRecorderTask() {
//			mView = v;
		}

		@Override
		protected String doInBackground(Void... params) {
			return doRecordVideo();
		}

		@Override
		protected void onPostExecute(String filepath) {
			super.onPostExecute(filepath);
			Log.i(TAG, "onPostExecute start");
//			if (filepath != null) {
//				updateGallery(filepath);
//				DrApplication.showToast("本地录像完成");
//			} else {
//				DrApplication.showToast("本地录像失败");
//			}
//			mView.setEnabled(true);

			Log.i(TAG, "onPostExecute end");
		}
	}
	public String doRecordVideo() {
		Log.i(TAG, "doVideoRecorder start");
		String outfilename = mOutputPath+ System.currentTimeMillis() + ".mp4";
		mCameraLiveView.startRecordVideo(outfilename);

		return outfilename;
	}

	///////////////////////分享视频

	private VideoShareTask mVideoShareTask;

	private void doShareVideo() {

//		mVideoRecorderView.setEnabled(false);
//		cancelVideoRecorderTask();

		mVideoShareTask = new VideoShareTask();
		mVideoShareTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
	}


	class VideoShareTask extends AsyncTask<Void, Void, String> {
		private View mView;

		public VideoShareTask() {
//			mView = v;
		}

		@Override
		protected String doInBackground(Void... params) {
			return doShareVideo2();
		}

		@Override
		protected void onPostExecute(String filepath) {
			super.onPostExecute(filepath);
			Log.i(TAG, "onPostExecute start");

			Log.i(TAG, "onPostExecute end");
		}
	}
	public String doShareVideo2() {
		Log.i(TAG, "doVideoRecorder start");
		String outfileurl = "rtmp://192.168.253.1/oflaDemo/test";
		mCameraLiveView.startShareVideo(outfileurl);

		return outfileurl;
	}
}
