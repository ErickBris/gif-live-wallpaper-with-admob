package sale.app.livewallpaper.gif.activity;

import android.app.Activity;
import android.app.WallpaperManager;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.RelativeLayout;

import com.google.ads.Ad;
import com.google.ads.AdListener;
import com.google.ads.AdRequest;
import com.google.ads.AdRequest.ErrorCode;
import com.google.ads.InterstitialAd;

import sale.app.livewallpaper.gif.GifLiveWallpaperApplication;
import sale.app.livewallpaper.gif.R;
import sale.app.livewallpaper.gif.config.AppConfig;
import sale.app.livewallpaper.gif.service.LiveWallpaperService;
import sale.app.livewallpaper.gif.util.AppConstants;
import sale.app.livewallpaper.gif.util.AppUtils;

/**
 * This class is a home class which show the app home contents.
 * 
 * @author vishalbodkhe
 * 
 */
public class HomeActivity extends Activity {

	private Button mSetButton;
	private Button mCancelButton;
	private RelativeLayout mMoreAppContainerView;
	private RelativeLayout mShareAppContainerView;
	private RelativeLayout mAdsView;
	private InterstitialAd mInterstitialAds;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (!GifLiveWallpaperApplication.isTablet) {
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		}
		setContentView(R.layout.home_layout);
		initViews();
		initAds();
	}

	/**
	 * Method to set init component.
	 */
	private void initViews() {
		mSetButton = (Button) findViewById(R.id.set_button);
		mCancelButton = (Button) findViewById(R.id.cancel_button);
		mMoreAppContainerView = (RelativeLayout) findViewById(R.id.more_app_container_view);
		mShareAppContainerView = (RelativeLayout) findViewById(R.id.share_container_view);
		if (AppConfig.MORE_APP_ENABLED) {
			mMoreAppContainerView.setVisibility(View.VISIBLE);
			mMoreAppContainerView.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View view) {
					moreAppClicked();
				}
			});
		} else {
			mMoreAppContainerView.setVisibility(View.INVISIBLE);
		}
		if (AppConfig.SHARE_APP_ENABLED) {
			mShareAppContainerView.setVisibility(View.VISIBLE);
			mShareAppContainerView.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View view) {
					if (AppUtils.isInternetConnected(HomeActivity.this)
							&& AppConfig.INTERSTITIAL_ADS_ENABLED) {
						if (mInterstitialAds.isReady()) {
							mInterstitialAds.show();
						}
					}
					shareClicked(getString(R.string.share_subject),
							GifLiveWallpaperApplication.getAppUrl());
				}
			});
		} else {
			mShareAppContainerView.setVisibility(View.INVISIBLE);
		}
		mCancelButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View view) {
				finish();
			}
		});
		mSetButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View view) {
				Intent intent = new Intent();
				if (Build.VERSION.SDK_INT > Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1) {
					intent.setAction(WallpaperManager.ACTION_LIVE_WALLPAPER_CHOOSER);
					String packageName = LiveWallpaperService.class
							.getPackage().getName();
					String canonicalName = LiveWallpaperService.class
							.getCanonicalName();
					intent.putExtra(
							WallpaperManager.WALLPAPER_PREVIEW_META_DATA,
							new ComponentName(packageName, canonicalName));
				} else {
					intent.setAction(WallpaperManager.ACTION_LIVE_WALLPAPER_CHOOSER);
				}
				startActivityForResult(intent, 0);
			}
		});

	}

	/**
	 * Method to inits the ads banner view
	 */
	private void initAds() {
		if (mAdsView == null) {
			mAdsView = (RelativeLayout) findViewById(R.id.bottom_ads_view);
		}
		if (AppConfig.BANNER_ADS_ENABLED) {
			AppUtils.addAdsBannerView(HomeActivity.this, mAdsView);
			mAdsView.setVisibility(View.VISIBLE);
		} else {
			mAdsView.setVisibility(View.GONE);
		}
		if (AppUtils.isInternetConnected(HomeActivity.this)
				&& AppConfig.INTERSTITIAL_ADS_ENABLED) {
			mInterstitialAds = new InterstitialAd(this, AppConfig.ADMOB_ADS_ID);
			AdRequest adRequest = new AdRequest();
			mInterstitialAds.loadAd(adRequest);

			mInterstitialAds.setAdListener(new AdListener() {

				@Override
				public void onDismissScreen(Ad arg0) {
				}

				@Override
				public void onFailedToReceiveAd(Ad arg0, ErrorCode arg1) {
				}

				@Override
				public void onLeaveApplication(Ad arg0) {
				}

				@Override
				public void onPresentScreen(Ad arg0) {
				}

				@Override
				public void onReceiveAd(Ad arg0) {

				}
			});
		}
	}

	/**
	 * Method to share app via different available share apps
	 */
	private void shareClicked(String subject, String text) {
		Intent intent = new Intent(Intent.ACTION_SEND);
		intent.setType("text/plain");
		intent.putExtra(Intent.EXTRA_SUBJECT, subject);
		intent.putExtra(Intent.EXTRA_TEXT, text);
		startActivity(Intent.createChooser(intent,
				getString(R.string.share_via)));
	}

	/**
	 * Method to Show more apps from Developer
	 */
	private void moreAppClicked() {
		try {
			startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(String
					.format("market://search?q=pub:%s",
							AppConfig.PLAYSTORE_ACCOUNT_NAME))));
		} catch (ActivityNotFoundException anfe) {
			startActivity(new Intent(
					Intent.ACTION_VIEW,
					Uri.parse(String
							.format("https://play.google.com/store/apps/developer?id=%s&hl=en",
									AppConfig.PLAYSTORE_ACCOUNT_NAME))));
		}
	}

	@Override
	protected void onDestroy() {
		if (mInterstitialAds != null) {
			mInterstitialAds.stopLoading();
			mInterstitialAds = null;

		}
		mMoreAppContainerView = null;
		mShareAppContainerView = null;
		mSetButton = null;
		mCancelButton = null;
		mAdsView = null;
		super.onDestroy();
	}
}
