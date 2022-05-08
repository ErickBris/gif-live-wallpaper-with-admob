package sale.app.livewallpaper.gif;

import android.app.Application;
import android.content.Context;
import sale.app.livewallpaper.gif.util.AppConstants;
import sale.app.livewallpaper.gif.util.AppUtils;

/**
 * This class is a application entry level class .
 * 
 * @author vishalbodkhe
 * 
 */
public class GifLiveWallpaperApplication extends Application {

	/** Application context */
	private static Context mContext;
	private static String mAppUrl;
	public static boolean isTablet;

	@Override
	public void onCreate() {
		super.onCreate();
		setContext(getApplicationContext());
		mAppUrl = AppConstants.PLAYSTORE_URL + mContext.getPackageName();
		isTablet = AppUtils.isTabletDevice(getApplicationContext());
	}

	@Override
	public void onLowMemory() {
		super.onLowMemory();
	}

	@Override
	public void onTerminate() {
		super.onTerminate();
	}

	/**
	 * @return the mAppUrl
	 */
	public static String getAppUrl() {
		return mAppUrl;
	}

	/**
	 * This method used to set the context.
	 * 
	 * @param context
	 */
	private static void setContext(Context context) {
		mContext = context;
	}

	/**
	 * This method used to get the context.
	 * 
	 * @return mContext
	 */
	public static Context getAppContext() {
		return mContext;
	}

}
