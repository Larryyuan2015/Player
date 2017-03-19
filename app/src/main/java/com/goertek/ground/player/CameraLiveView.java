package com.goertek.ground.player;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;


public class CameraLiveView extends SurfaceView implements SurfaceHolder.Callback {
	private final static String TAG = "CameraLiveView";
	private Bitmap bitmap;
	private Matrix matrix;
	private SurfaceHolder m_softHolder=null;
	private int m_surfaceWidth = 0;
	private int m_surfaceHeight = 0;
	private int mFormat = 0;
	private Context mContext;
	private int m_SoftPixelFmt = PixelFormat.RGBA_8888;//PixelFormat.RGBA_8888;//
	private boolean mFullScreen;
	private volatile boolean bCanceled = false;
	private Thread mThreadDecode = null;
	private Thread mThreadDraw = null;
	private Surface surface = null;
	private boolean isRecording = false;
	private boolean isSharing = false;
	private boolean isPlaying = false;

	public native void initialWithUrl(String url);
	public native void play( Bitmap bitmap);
	public native void stop();
	public native int startRecVideo(String outputpath);
	public native int stopRecVideo();

	public native int nstartShareVideo(String outputurl);
	public native int nstopShareVideo();


	public CameraLiveView(Context context) {
		super(context);
		init(context);
//		m_softHolder = this.getHolder();
//		m_softHolder.addCallback(this);
//		matrix=new Matrix();
//		bitmap = Bitmap.createBitmap(640, 480, Bitmap.Config.ARGB_8888);
//		bitmap = Bitmap.createBitmap(1280, 720, Bitmap.Config.ARGB_8888);
//		bitmap = Bitmap.createBitmap(1280, 720, Bitmap.Config.RGB_565);
		Log.i(TAG, "begin");
	}

	public CameraLiveView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	public CameraLiveView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		init(context);
	}

	@TargetApi(Build.VERSION_CODES.LOLLIPOP)
	public CameraLiveView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
		super(context, attrs, defStyleAttr, defStyleRes);
		init(context);
	}

	private void init(Context context) {
		mContext = context;
		m_softHolder = getHolder();
		m_softHolder.addCallback(this);
//		m_softHolder.setFormat(m_SoftPixelFmt);
		matrix=new Matrix();
		bitmap = Bitmap.createBitmap(1280, 720, Bitmap.Config.ARGB_8888);
		Log.i(TAG, "init finished");
	}

	public void setFullScreen(boolean bFullScreen) {
		setVisibility(View.INVISIBLE);
		bCanceled= true;
		mFullScreen = bFullScreen;
		restartPlay();

//		invalidate();
	}

	public void startPlay(String url) {
//		mFullScreen = true;
		isPlaying = true;
		setVisibility(View.VISIBLE);
		Log.i(TAG, "init startPlay");
//		surfaceCreated(m_softHolder);
//        m_ui_ctrl.start();
	}

	public void stopPlay() {
		isPlaying = false;
//		setVisibility(View.INVISIBLE);
		Log.i(TAG, "stopPlay");
		stop();
	}

	public void restartPlay() {
		final Handler mHandler = new Handler();
		bCanceled = false;
//		setVisibility(View.VISIBLE);
		Log.i(TAG, "restartPlay");


		new Thread(new Runnable() {
			public void run() {
//				try {
//					Thread.sleep(1000);
//				} catch (InterruptedException e) {
//					Log.e(TAG, "restartPlay", e);
//				}
				stopDrawThread();
				mHandler.post(new Runnable() {
					@Override
					public void run() {
//						startPlay();
						setVisibility(View.VISIBLE);
					}
				});
			}
		}).start();
	}




	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
		Log.i(TAG, "surface Changed width: " + width + " height " + height + " hash " + holder.getSurface().hashCode());
		m_surfaceWidth = width;
		m_surfaceHeight = height;
		m_softHolder= holder;
		mFormat = format;
//		SurfaceChangeProc(ConstVal.SOFT_CODEC, holder, format, width, height);
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		Log.i(TAG, "surface Created");
		mThreadDecode = new Thread(mRunableOnDecodeFrame);
		mThreadDecode.start();

		mThreadDraw = new Thread(mRunableOnDrawFrame);
		mThreadDraw.start();
	}


	Runnable mRunableOnDecodeFrame = new Runnable() {
		@Override
		public void run() {
			Log.i(TAG, "play initialWithUrl");
			initialWithUrl(ActivityMain.RTSPURL);
			play(bitmap);
			Log.i(TAG, "playing");
		}
	};

	Runnable mRunableOnDrawFrame = new Runnable() {
		@Override
		public void run() {
			Log.i(TAG, "bCanceled =" + bCanceled);

			while (!bCanceled) {
				if ((bitmap != null)) {
					// System.out.println("begin");
					Log.i(TAG, "canvas drawBitmap begin");

					Canvas canvas = m_softHolder.lockCanvas(null);
					if(canvas == null) {
						bCanceled = true;
						//发出消息，重连？
						break;
					}
					Paint paint = new Paint();
					paint.setAntiAlias(true);
					paint.setStyle(Style.FILL);
					int mWidth = bitmap.getWidth();
					int mHeight = bitmap.getHeight();
					matrix.reset();
					matrix.setScale((float) m_surfaceWidth / mWidth, (float) m_surfaceHeight
							/ mHeight);

					if(canvas == null) {
						Log.i(TAG, "canvas is null");
//							canvas = m_softHolder.lockCanvas(null);
					}

					canvas.drawBitmap(bitmap, matrix, paint);
					m_softHolder.unlockCanvasAndPost(canvas);
					Log.i(TAG, "canvas drawBitmap finished");
				}
			}
		}
	};


	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		Log.i(TAG, "surfaceDestroyed");
	}


	public void cancelDrawThread()
	{
		bCanceled = true;
	}


	public void stopDrawThread()
	{

		if(mThreadDecode !=null){
//			mThreadDecode.stop();
			mThreadDecode =null;
		}

		if(mThreadDraw !=null){
//			mThreadDraw.stop();
			mThreadDraw =null;
		}

	}


	public boolean isRecording(){
		if (isRecording)
			return true;
		return false;
	}

	public boolean isSharing(){
		if (isSharing)
			return true;
		return false;
	}

	public boolean isPlaying(){
		if (isPlaying)
			return true;
		return false;
	}

	//开始录制视频
	public void startRecordVideo(String outputPath){
		if (TextUtils.isEmpty(outputPath))
			return;

		isRecording = true;
		startRecVideo(outputPath);
//		if (null != m_ui_ctrl){
//			mRecVideoOutputPath = outputPath;
//			//outputPath += ".tmp";
//			m_ui_ctrl.RecVideo(outputPath);
//			Log.i(TAG, "startRecVideo outputPath=" + outputPath);
//		}
	}

	public void stopRecordVideo(){
		if(isRecording)
			stopRecVideo();
		isRecording=false;
		//异步返回时，mRecVideoState = RECORD_STATE.FINISH；
	}

	//开始分享视频
	public void startShareVideo(String outputPath){
//		if (TextUtils.isEmpty(outputPath))
//			return;
		isSharing = true;
		nstartShareVideo(outputPath);
	}

	public void stopShareVideo(){
		if(isSharing)
			nstopShareVideo();
		isSharing=false;
		//异步返回时，mRecVideoState = RECORD_STATE.FINISH；
	}

	//callback by native
	public void setBitmapSize(int width, int height) {
		Log.i("Sur", "setsize");
//		bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
		bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
	}

	static {
		System.loadLibrary("ffmpeg");
		System.loadLibrary("rtspclient");
	}



	private int m_nResWidth = 0;
	private int m_nResHeight = 0;

	@SuppressLint("NewApi")
	public void getPhyResolution() {
		int width = 0, height = 0;
		final DisplayMetrics metrics = new DisplayMetrics();
		Display display = ((Activity) mContext).getWindowManager().getDefaultDisplay();

		display.getRealMetrics(metrics);
		width = metrics.widthPixels;
		height = metrics.heightPixels;

		// should care about orientation here
		int orient = getResources().getConfiguration().orientation;
		if (orient == Configuration.ORIENTATION_PORTRAIT) {
			if (width > height) {
				int tmp = width;
				width = height;
				height = tmp;
			}
		} else if (orient == Configuration.ORIENTATION_LANDSCAPE) {
			if (width < height) {
				int tmp = width;
				width = height;
				height = tmp;
			}
		}

		m_nResWidth = width;
		m_nResHeight = height;
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

		getPhyResolution();
		if (mFullScreen) {
			int width = m_nResWidth;
			int height = m_nResHeight;
			Log.d(TAG, "onMeasure1 width=" + width + "height=" + height);
			setMeasuredDimension(width, height);
			return;
		}else {
			int width = Math.min(MeasureSpec.getSize(widthMeasureSpec), m_nResWidth);
			if (width > 0) {
				int height = Math.min(MeasureSpec.getSize(heightMeasureSpec), m_nResHeight);
				Log.d(TAG, "onMeasure3 width=" + width + "height=" + height);
				setMeasuredDimension(width, height);
				return;
			}
		}
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
	}

	@Override
	protected void onLayout(boolean changed, int left, int top, int right, int bottom) {

		Log.d(TAG, "onLayout changed=" + changed + "left=" + left + "top=" + top+ "right=" + right + "bottom=" + bottom);

		super.onLayout(changed, left, top, right, bottom);
	}
}
