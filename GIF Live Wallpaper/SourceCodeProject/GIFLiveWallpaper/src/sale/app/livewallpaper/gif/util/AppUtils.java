/**
 * 
 */
package sale.app.livewallpaper.gif.util;

import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.NetworkInfo.State;
import android.view.WindowManager;
import android.widget.RelativeLayout;

import com.google.ads.AdRequest;
import com.google.ads.AdSize;
import com.google.ads.AdView;

import sale.app.livewallpaper.gif.config.AppConfig;

/**
 * The class to store utility methods to used in whole application
 * 
 * @author VishalMobitech
 * 
 */
public class AppUtils {

	/**
	 * Method to add and show ads banner view
	 * 
	 * @param context
	 * @param view
	 */
	public static void addAdsBannerView(Activity context, RelativeLayout view) {
		AdView bannerView = new AdView(context, AdSize.BANNER,
				AppConfig.ADMOB_ADS_ID);
		AdRequest adRequest = new AdRequest();
		adRequest.setTesting(AppConfig.TESTING_ENABLED);
		bannerView.loadAd(new AdRequest());
		view.removeAllViews();
		view.addView(bannerView);
	}

	/**
	 * Method to check internet
	 * 
	 * @param context
	 * @return boolean
	 */
	public static boolean isInternetConnected(Context context) {
		ConnectivityManager manager = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo info = manager.getActiveNetworkInfo();
		if (info == null) {
			return false;
		}
		if (info.getState() != State.CONNECTED) {
			return false;
		}
		return true;
	}

	/**
	 * This method used to get the result that the given device is tablet or not
	 * on basis of screen layout.
	 * 
	 * @param context
	 * @return result
	 */
	public static boolean isTabletDevice(Context context) {
		return (context.getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) >= Configuration.SCREENLAYOUT_SIZE_LARGE;
	}

	/**
	 * This method used to get the device width.
	 * 
	 * @param context
	 * @return device width
	 */
	@SuppressWarnings("deprecation")
	public static int getDeviceWidth(Context context) {
		WindowManager winMgr = (WindowManager) context
				.getSystemService(Context.WINDOW_SERVICE);
		return winMgr.getDefaultDisplay().getWidth();
	}

	/**
	 * This method used to get the device height.
	 * 
	 * @param context
	 * @return device height
	 */
	@SuppressWarnings("deprecation")
	public static int getDeviceHeight(Context context) {
		WindowManager winMgr = (WindowManager) context
				.getSystemService(Context.WINDOW_SERVICE);
		return winMgr.getDefaultDisplay().getHeight();
	}

}
