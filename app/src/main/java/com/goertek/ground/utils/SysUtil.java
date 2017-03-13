package com.goertek.ground.utils;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.Dialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.os.Build;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyCharacterMap;
import android.view.KeyEvent;
import android.view.ViewConfiguration;
import android.widget.TextView;

import java.lang.reflect.Method;
import java.net.URLEncoder;
import java.util.List;


public class SysUtil {
	private static final String TAG = "SysUtil";
	private static final String DEFAULT_IMEI = "Goer_DEFAULT_IMEI";
	private static String sImei = null;
	private static String sImei_md5;
	public static String sImei2;
	private static String status2;
	private static String mModel;
	private static final String APP_STORE_IMEI = "app_store_imei";
	private static final String APP_STORE_STATUS = "app_store_status";
	private static final String APP_STORE_IMEI0 = "app_store_imei0_new";


	// 提供一个新的设备硬件编号，Daemon和Baohe统一用这个接口获取硬件编号
	public static String getIMEI2(Context context) {
		if (TextUtils.isEmpty(sImei2)) {
			String imei = getIMEI(context);
			String androidId = Settings.System.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
			String serialNo = getDeviceSerial();
			sImei2 = MD5.getStringMd5(imei + androidId + serialNo);
			Log.d(TAG,"getIMEI2 imei = "+imei+" androidId = "+androidId+" serialNo = "+serialNo+" !! sImei2 = "+sImei2);
		}
		return sImei2;
	}

	// 老版本获取的imei是错误的，但是wif密码会用这个加密，所以还要兼容一下
	public static String getOldIMEI2(Context context) {
		String imei = getOldIMEI(context);
		String androidId = Settings.System.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
		String serialNo = getDeviceSerial();
        return MD5.getStringMd5("" + imei + androidId + serialNo);
	}

	/**
	 * 获取设备ID号(国际移动设备身份码，储存在移动设备中，全球唯一的一组号码)
	 * 老版本获取的imei是错误的，但是wif密码会用这个加密，所以还要兼容一下
	 */
	public static String getOldIMEI(Context c) {
		return DEFAULT_IMEI;
	}

	private static String getDeviceSerial() {
		String serial = "";
		try {
			Class<?> c = Class.forName("android.os.SystemProperties");
			Method get = c.getMethod("get", String.class);
			serial = (String) get.invoke(c, "ro.serialno");
		} catch (Exception ignored) {
		}
		return serial;
	}

	//宝盒进程的IMEI号算法
	public static String getBaoHeIMei(Context context) {
		try {
			return MD5.getStringMd5(getIMEI(context)).toLowerCase();
		} catch (Exception e) {
			return "none";
		}
	}

	/**
	 * 获取设备ID号(国际移动设备身份码，储存在移动设备中，全球唯一的一组号码)
	 * @return String
	 */
	public static String getIMEI(Context c) {
		try {
			//sImei = ApplicationConfig.getInstance().getString(APP_STORE_IMEI0, "");
			if (!TextUtils.isEmpty(sImei)) {
				return sImei;
			}
			sImei = ((TelephonyManager) c.getSystemService(Context.TELEPHONY_SERVICE)).getDeviceId();
			if (!TextUtils.isEmpty(sImei)) {
				return sImei;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return DEFAULT_IMEI;
	}

	public static String getIMEIEx(Context c) {
		try {
			if (null == sImei_md5 || sImei_md5.length() == 0) {
				sImei_md5 = ((TelephonyManager) c.getSystemService(Context.TELEPHONY_SERVICE)).getDeviceId();
				if (!TextUtils.isEmpty(sImei_md5)) {
					sImei_md5 = MD5.getStringMd5(sImei_md5).toLowerCase();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return sImei_md5;
	}

	//获取手机型号
	public static synchronized String getModel() {
		if (null != mModel && !mModel.isEmpty())
			return mModel;

		String model = Build.MODEL;
		if (null == model) {
			mModel = null;
			return mModel;
		}

		//处理机型被篡改为6s Ρlus的情况
		model = model.replace("Ρ", "p");
		model = model.replace("ｐ", "p");
		try
		{
			String str = new String(model.getBytes(), "UTF-8");
			model = URLEncoder.encode(str, "UTF-8");
		}
		catch (Exception localException)
		{
			Log.e(TAG, "getModel() ", localException);
		}
		mModel = model;
		return mModel;
	}

	public static synchronized void setModel(String model){
		mModel = model;
	}

	public static boolean isMobileDataOn(Context context) {
		// 先检查sim卡
		TelephonyManager telManager = (TelephonyManager)context.getSystemService(Activity.TELEPHONY_SERVICE);
		int simState = telManager.getSimState();
		Log.d(TAG, "isMobileDataOn SIM=" + simState);
		switch (simState) {
			case TelephonyManager.SIM_STATE_ABSENT:
			case TelephonyManager.SIM_STATE_NETWORK_LOCKED:
				return false;
			default:
				break;
		}
		boolean isOpen = false;
		ConnectivityManager connManager = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);

		try {
			String methodName = "getMobileDataEnabled";
			Class cmClass = connManager.getClass();

			Method method = cmClass.getMethod(methodName);
			isOpen = (Boolean) method.invoke(connManager);
			Log.d(TAG, "isMobileDataOn isOpen = " + isOpen);
		} catch (Exception e) {
			Log.e(TAG, "isMobileDataOn", e);
		}

		return isOpen;

	}

	public static void gotoMobileDataSetting(Activity activity){
//		Intent intent = new Intent("android.settings.APN_SETTINGS");
		Intent intent= new Intent();
		intent.setClassName("com.android.settings", "com.android.settings.Settings$DataUsageSummaryActivity");
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		try {
			activity.startActivity(intent);
		} catch (Exception e){
			Log.e(TAG, "gotoMobileDataSetting", e);
		}
	}

	public static boolean isComponentExisted(Context context, Intent intent) {
		ComponentName cmpName = intent.resolveActivity(context.getPackageManager());
		return isComponentExisted(context, cmpName);
	}

	public static boolean isComponentExisted(Context context, ComponentName cmpName) {
		boolean bIsExist = false;
		if (cmpName != null) { // 说明系统中存在这个activity
			ActivityManager am = (ActivityManager) context
					.getSystemService(Activity.ACTIVITY_SERVICE);
			List<ActivityManager.RunningTaskInfo> taskInfoList = am.getRunningTasks(10);
			for (ActivityManager.RunningTaskInfo taskInfo : taskInfoList) {
				if (taskInfo.baseActivity.equals(cmpName)) { // 说明它已经启动了
					bIsExist = true;
					break;
				}
			}
		}
		return bIsExist;
	}

	private static String mAppVer = null;
	public static String GetCurAppVer(Context context) {
		if (mAppVer == null) {
			try {
				mAppVer = context.getPackageManager().getPackageInfo(
						context.getPackageName(), 0).versionName;
			} catch (PackageManager.NameNotFoundException e) {
				Log.e(TAG, "GetCurAppVer ", e);
			}
		}
		return mAppVer;
	}

	public static boolean isAppForeground(Context context) {
		if(Build.VERSION.SDK_INT > 20){
			return isAppForegroundAfterLollipop(context);
		}else{
			return isAppForegroundBeforeLollipop(context);
		}
	}

	public static boolean isAppForegroundAfterLollipop(Context context) {
		ActivityManager activityManager = (ActivityManager) context
				.getSystemService(Context.ACTIVITY_SERVICE);
		List<ActivityManager.RunningAppProcessInfo> appProcesses = activityManager
				.getRunningAppProcesses();
		if(appProcesses == null || appProcesses.size() <= 0){
			return false;
		}
		for (ActivityManager.RunningAppProcessInfo appProcess : appProcesses) {
			if (appProcess.processName.equals(context.getPackageName())) {
				if (appProcess.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
					Log.d(TAG, "IMPORTANCE_FOREGROUND "+appProcess.processName + " appProcess.importance = "+appProcess.importance);
					return true;
				} else {
					Log.d(TAG, "IMPORTANCE_BACKGROUND " + appProcess.processName + " appProcess.importance = "+appProcess.importance);
					return false;
				}
			}
		}
		return false;
	}

	public static boolean isAppForegroundBeforeLollipop(Context context) {
		if (context == null) {
			return false;
		}

		String myPkgName = context.getPackageName();

		ActivityManager activityManager = (ActivityManager) context
				.getSystemService(Context.ACTIVITY_SERVICE);
		List<ActivityManager.RunningTaskInfo> runningTasks = activityManager.getRunningTasks(1);
		ActivityManager.RunningTaskInfo taskInfo = runningTasks.get(0);
		ComponentName component = taskInfo.topActivity;
		String topPkgName = component.getPackageName();

		if (topPkgName.contains(myPkgName)) {
			Log.d(TAG,"isAppForeground return true");
			return true;
		}
		Log.d(TAG,"isAppForeground return false");
		return false;
	}

	public static boolean checkDeviceHasNavigationBar(Context activity) {
		//通过判断设备是否有返回键、菜单键(不是虚拟键,是手机屏幕外的按键)来确定是否有navigation bar
		boolean hasMenuKey = ViewConfiguration.get(activity)
				.hasPermanentMenuKey();
		boolean hasBackKey = KeyCharacterMap
				.deviceHasKey(KeyEvent.KEYCODE_BACK);

		//noinspection RedundantIfStatement
		if (!hasMenuKey && !hasBackKey) {
			// 做任何你需要做的,这个设备有一个导航栏
			return true;
		}
		return false;
	}

	public static boolean isGpsSwitchOn(Context context) {
		return Settings.Secure.isLocationProviderEnabled(context.getContentResolver(),
				LocationManager.GPS_PROVIDER);
	}


	public static void gotoGpsSettings(Context context){
		Intent intent = new Intent("android.settings.LOCATION_SOURCE_SETTINGS");
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		context.startActivity(intent);
	}
}