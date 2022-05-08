package sale.app.livewallpaper.gif.service;

import android.graphics.Canvas;

import android.graphics.Movie;
import android.os.Handler;
import android.os.SystemClock;
import android.service.wallpaper.WallpaperService;
import android.view.SurfaceHolder;
import sale.app.livewallpaper.gif.R;
import sale.app.livewallpaper.gif.util.AppUtils;

import java.io.IOException;
import java.io.InputStream;

/**
 * This class is service class which run the live wallpaper service.
 * 
 * @author vishalbodkhe
 * 
 */
public class LiveWallpaperService extends WallpaperService {
	private AnimationEngine mEngine;
	private int mDeviceWidth;
	private int mDeviceHeight;
	private int mResourceId = R.raw.gif_image;
	private static final Handler mDrawHandler = new Handler();

	@Override
	public void onCreate() {
		super.onCreate();
		mDeviceWidth = AppUtils.getDeviceWidth(LiveWallpaperService.this);
		mDeviceHeight = AppUtils.getDeviceHeight(LiveWallpaperService.this);
	}

	@Override
	public Engine onCreateEngine() {
		try {
			mEngine = new AnimationEngine();
			return mEngine;
		} catch (IOException e) {
			stopSelf();
			return null;
		}
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
	}

	public void doProcessing() throws IOException {
		if (mEngine != null) {
			mEngine.process(mResourceId);
		}
	}

	/**
	 * The class is used to start live wallpaper engine.
	 * 
	 * @author vishalbodkhe
	 * 
	 */
	class AnimationEngine extends Engine {
		private Movie mMovie;
		private int mMovieDuration;
		private Runnable mAnimationRunnable;
		float mScaleX;
		float mScaleY;
		int mWhen;
		long mStart;

		AnimationEngine() throws IOException {
			process(mResourceId);
			mWhen = -1;
			mAnimationRunnable = new Runnable() {
				public void run() {
					drawAnimation();
				}
			};
		}

		public void process(int resId) throws IOException {
			InputStream is = getResources().openRawResource(resId);
			if (is != null) {
				try {
					mMovie = Movie.decodeStream(is);
					mMovieDuration = mMovie.duration();
					mScaleX = mDeviceWidth / (1f * mMovie.width());
					mScaleY = mDeviceHeight / (1f * mMovie.height());
				} catch (Exception e) {
				} finally {
					is.close();
				}
			} else {
				throw new IOException("Unable to open input gif file");
			}

			mWhen = -1;
			mAnimationRunnable = new Runnable() {
				public void run() {
					drawAnimation();
				}
			};
		}

		@Override
		public void onDestroy() {
			super.onDestroy();
			mDrawHandler.removeCallbacks(mAnimationRunnable);
			mAnimationRunnable = null;
		}

		@Override
		public void onVisibilityChanged(boolean visible) {
			super.onVisibilityChanged(visible);
			if (visible) {
				drawAnimation();
			} else {
				mDrawHandler.removeCallbacks(mAnimationRunnable);
			}
		}

		@Override
		public void onSurfaceChanged(SurfaceHolder holder, int format,
				int width, int height) {
			super.onSurfaceChanged(holder, format, width, height);
			mDeviceHeight = height;
			mDeviceWidth = width;
			mScaleX = width / (1f * mMovie.width());
			mScaleY = height / (1f * mMovie.height());
			drawAnimation();
		}

		@Override
		public void onOffsetsChanged(float xOffset, float yOffset,
				float xOffsetStep, float yOffsetStep, int xPixelOffset,
				int yPixelOffset) {
			super.onOffsetsChanged(xOffset, yOffset, xOffsetStep, yOffsetStep,
					xPixelOffset, yPixelOffset);
			drawAnimation();
		}

		/**
		 * Method to draw animation
		 */
		private void drawAnimation() {
			Canvas canvas = null;
			SurfaceHolder surfaceHolder = getSurfaceHolder();
			try {
				findDuration();
				canvas = surfaceHolder.lockCanvas(null);
				synchronized (surfaceHolder) {
					drawPicture(canvas);
				}

				if (canvas != null) {
					surfaceHolder.unlockCanvasAndPost(canvas);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}

			mDrawHandler.removeCallbacks(mAnimationRunnable);
			if (isVisible()) {
				mDrawHandler.postDelayed(mAnimationRunnable, 1000L / 25L);
			}
		}

		/**
		 * Method to get duration
		 */
		private void findDuration() {
			if (mWhen == -1L) {
				mWhen = 0;
				mStart = SystemClock.uptimeMillis();
			} else {
				long mDiff = SystemClock.uptimeMillis() - mStart;
				if (mMovieDuration > 0) {
					mWhen = (int) (mDiff % mMovieDuration);
				} else {
					mMovieDuration = mMovie.duration();
				}
			}
		}

		/*
		 * Method to draw images on canvas
		 */
		private void drawPicture(Canvas canvas) {
			canvas.save();
			canvas.scale(mScaleX, mScaleY);
			mMovie.setTime(mWhen);
			mMovie.draw(canvas, 0, 0);
			canvas.restore();
		}
	}
}