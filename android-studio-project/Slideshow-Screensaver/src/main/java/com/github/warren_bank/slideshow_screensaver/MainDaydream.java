package com.github.warren_bank.slideshow_screensaver;

import android.service.dreams.DreamService;
import android.view.LayoutInflater;
import android.view.View;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import com.bumptech.glide.MemoryCategory;

/** Displays a {@link HorizontalGalleryFragment}. */
@RequiresApi(17)
public class MainDaydream extends DreamService {
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

    fragment.onResume();
  }

  @Override
  public void onDreamingStopped() {
    super.onDreamingStopped();

    fragment.onPause();
  }

  @Override
  public void onDetachedFromWindow() {
    super.onDetachedFromWindow();

    fragment.onDestroyView();
    fragment.onDestroy();
    fragment = null;
  }

}
