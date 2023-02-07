package com.github.warren_bank.slideshow_screensaver;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.loader.content.Loader;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.integration.recyclerview.RecyclerViewPreloader;
import java.io.File;
import java.util.Collections;
import java.util.List;

/** Displays media store data in a recycler view. */
public class HorizontalGalleryFragment extends Fragment implements Loader.OnLoadCompleteListener<List<MediaStoreData>> {

  private Context context;
  private RecyclerView recyclerView;
  private boolean forceRefresh;
  private GestureDetector gestures;
  private boolean usePortraitOrientation;
  private SettingsHelper settings;
  private RecyclerAdapter adapter;

  public HorizontalGalleryFragment() {
    this((Context) null);
  }

  public HorizontalGalleryFragment(Context context) {
    super();

    this.context = context;
  }

  @Override
  public Context getContext() {
    if (context != null)
      return context;

    context = getActivity();
    if (context != null)
      return context;

    context = super.getContext();
    return context;
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
  }

  @Override
  public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    View result = inflater.inflate(R.layout.recycler_view, container, false);
    recyclerView = (RecyclerView) result.findViewById(R.id.recycler_view);
    LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
    layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
    recyclerView.setLayoutManager(layoutManager);
    recyclerView.setHasFixedSize(true);

    initGestureDetector();

    // cannot initialize the list of images in onCreate() because
    // onLoadComplete requires that recyclerView has been defined.
    init();

    return result;
  }

  private void initGestureDetector() {
    forceRefresh = false;

    gestures = new GestureDetector(/* context= */ getContext(), new GestureDetector.SimpleOnGestureListener(){
      @Override
      public void onLongPress(MotionEvent e) {
        // open SettingsActivity
        Intent intent = new Intent(/* context= */ getContext(), SettingsActivity.class);
        startActivity(intent);
        forceRefresh = true;
      }
    });
  }

  private void init() {
    usePortraitOrientation = true;
    updateOrientation(getResources().getConfiguration().orientation);

    settings = new SettingsHelper(/* context= */ getContext());

    boolean use_mediastore = settings.useMediaStore();
    File directory = null;

    if (!use_mediastore) {
      directory = settings.getDirectory();

      if (directory == null)
        use_mediastore = true;
    }

    if (use_mediastore) {
      Loader<List<MediaStoreData>> loader = new MediaStoreDataLoader(getContext());
      loader.registerListener(R.id.loader_id_media_store_data, this);
      loader.startLoading();
    }
    else {
      boolean recurse = settings.useDirectoryRecursion();
      boolean shuffle = settings.useShuffle();
      List<MediaStoreData> mediaStoreData = DirectoryHelper.getImagesInDirectory(directory, recurse);

      if (shuffle)
        Collections.shuffle(mediaStoreData);

      onLoadComplete(/* loader= */ null, mediaStoreData);
    }

    recyclerView.addOnItemTouchListener(new RecyclerView.SimpleOnItemTouchListener() {
      @Override
      public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
        // forward event
        gestures.onTouchEvent(e);

        // consume event
        return true;
      }

      @Override
      public void onTouchEvent(RecyclerView rv, MotionEvent e) {
        // forward event
        gestures.onTouchEvent(e);
      }
    });
  }

  @Override
  public void onLoadComplete(Loader<List<MediaStoreData>> loader, List<MediaStoreData> mediaStoreData) {
    GlideRequests glideRequests = GlideApp.with(getContext());
    adapter = new RecyclerAdapter(getContext(), mediaStoreData, glideRequests);
    RecyclerViewPreloader<MediaStoreData> preloader = new RecyclerViewPreloader<>(glideRequests, adapter, adapter, 3);
    recyclerView.addOnScrollListener(preloader);
    recyclerView.setAdapter(adapter);

    initSlideshow();
  }

  private boolean        slideshow_running;
  private int            duration;
  private int            current_image_index;
  private Handler        handler;
  private Runnable       slideshow;

  private void initSlideshow() {
    if (adapter == null) {
      slideshow_running = false;
      return;
    }

    duration            = settings.getImageDuration();
    current_image_index = -1;
    handler             = new Handler();
    slideshow           = new Runnable() {
      @Override
      public void run() {
        int total = adapter.getItemCount();
        int next  = current_image_index + 1;

        if (next >= total)
          next = 0;

        if (next != current_image_index) {
          current_image_index = next;
          recyclerView.scrollToPosition(current_image_index);
        }

        handler.postDelayed(this, duration);
      }
    };

    handler.post(slideshow);
    slideshow_running = true;
  }

  @Override
  public void onResume() {
    super.onResume();

    if ((handler != null) && !slideshow_running) {
      handler.postDelayed(slideshow, duration);
      slideshow_running = true;
    }

    if (forceRefresh) {
      forceRefresh = false;
      getActivity().recreate();
    }
  }

  @Override
  public void onPause() {
    super.onPause();

    if ((handler != null) && slideshow_running) {
      handler.removeCallbacks(slideshow);
      slideshow_running = false;
    }
  }

  @Override
  public void onConfigurationChanged(Configuration newConfig) {
    super.onConfigurationChanged(newConfig);

    updateOrientation(newConfig.orientation);
  }

  private void updateOrientation(int newOrientation) {
    boolean usePortraitOrientation = (newOrientation != Configuration.ORIENTATION_LANDSCAPE);
    updateOrientation(usePortraitOrientation);
  }

  public void updateOrientation(boolean usePortraitOrientation) {
    if (this.usePortraitOrientation != usePortraitOrientation) {
      this.usePortraitOrientation = usePortraitOrientation;

      adapter.updateOrientation(usePortraitOrientation);
      recyclerView.requestLayout();
    }
  }
}
