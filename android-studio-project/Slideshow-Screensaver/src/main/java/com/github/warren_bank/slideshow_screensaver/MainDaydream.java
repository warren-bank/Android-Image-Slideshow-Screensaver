package com.github.warren_bank.slideshow_screensaver;

import com.github.warren_bank.slideshow_screensaver.utils.SensorOrientationChangeNotifier;

import com.bumptech.glide.MemoryCategory;

import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import android.content.pm.ActivityInfo;
import android.service.dreams.DreamService;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;

/** Displays a {@link HorizontalGalleryFragment}. */
@RequiresApi(17)
public class MainDaydream extends DreamService implements SensorOrientationChangeNotifier.Listener {
  private Fragment fragment;

  @Override
  public void onAttachedToWindow() {
    super.onAttachedToWindow();

    setFullscreen(true);
    setScreenBright(true);

    GlideApp.get(this).setMemoryCategory(MemoryCategory.HIGH);

    fragment = new HorizontalGalleryFragment(this);
    View view = fragment.onCreateView(LayoutInflater.from(this), /* (ViewGroup) */ null, /* (Bundle) */ null);
    setContentView(view);
  }

  @Override
  public void onDreamingStarted() {
    super.onDreamingStarted();

    if (fragment != null) {
      fragment.onResume();

      SensorOrientationChangeNotifier.getInstance(this).addListener(this);
    }
  }

  @Override
  public void onDreamingStopped() {
    super.onDreamingStopped();

    if (fragment != null)
      fragment.onPause();

    SensorOrientationChangeNotifier.getInstance(this).remove(this);
  }

  @Override
  public void onDetachedFromWindow() {
    super.onDetachedFromWindow();

    if (fragment != null) {
      fragment.onDestroyView();
      fragment.onDestroy();
      fragment = null;
    }
  }

  @Override
  public void onWindowAttributesChanged(WindowManager.LayoutParams attrs) {
    super.onWindowAttributesChanged(attrs);

    if ((fragment != null) && (fragment instanceof HorizontalGalleryFragment)) {
      boolean usePortraitOrientation = (attrs.screenOrientation != ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
      ((HorizontalGalleryFragment)fragment).updateOrientation(usePortraitOrientation);
    }
  }

  /*
   * interface: SensorOrientationChangeNotifier.Listener
   */
  @Override
  public void onOrientationChange(int orientation) {
    if ((fragment != null) && (fragment instanceof HorizontalGalleryFragment)) {
      boolean usePortraitOrientation = SensorOrientationChangeNotifier.getInstance(this).isPortrait();
      ((HorizontalGalleryFragment)fragment).updateOrientation(usePortraitOrientation);
    }
  }

}
