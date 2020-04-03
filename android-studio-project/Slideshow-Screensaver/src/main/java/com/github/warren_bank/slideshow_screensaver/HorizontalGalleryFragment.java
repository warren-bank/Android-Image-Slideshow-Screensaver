package com.github.warren_bank.slideshow_screensaver;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
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
import java.util.List;

/** Displays media store data in a recycler view. */
public class HorizontalGalleryFragment extends Fragment implements Loader.OnLoadCompleteListener<List<MediaStoreData>> {

  private Context context;
  private RecyclerView recyclerView;

  public HorizontalGalleryFragment() {
    this((Context) null);
  }

  public HorizontalGalleryFragment(Context context) {
    super();

    this.context = context;

    if (context != null)
      init();
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
    init();
  }

  @Override
  public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    View result = inflater.inflate(R.layout.recycler_view, container, false);
    recyclerView = (RecyclerView) result.findViewById(R.id.recycler_view);
    LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
    layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
    recyclerView.setLayoutManager(layoutManager);
    recyclerView.setHasFixedSize(true);

    return result;
  }

  public void init() {
    Loader<List<MediaStoreData>> loader = new MediaStoreDataLoader(getContext());
    loader.registerListener(R.id.loader_id_media_store_data, this);
    loader.startLoading();
  }

  @Override
  public void onLoadComplete(Loader<List<MediaStoreData>> loader, List<MediaStoreData> mediaStoreData) {
    GlideRequests glideRequests = GlideApp.with(getContext());
    RecyclerAdapter adapter = new RecyclerAdapter(getContext(), mediaStoreData, glideRequests);
    RecyclerViewPreloader<MediaStoreData> preloader = new RecyclerViewPreloader<>(glideRequests, adapter, adapter, 3);
    recyclerView.addOnScrollListener(preloader);
    recyclerView.setAdapter(adapter);

    initSlideshow(adapter);
  }

  private boolean        slideshow_running;
  private SettingsHelper settings;
  private int            duration;
  private int            current_image_index;
  private Handler        handler;
  private Runnable       slideshow;

  private void initSlideshow(RecyclerAdapter adapter) {
    if (adapter == null) {
      slideshow_running = false;
      return;
    }

    settings            = new SettingsHelper(/* context= */ getContext());
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

    recyclerView.addOnItemTouchListener(new RecyclerView.SimpleOnItemTouchListener() {
      @Override
      public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
        // consume touch event
        return true;
      }
    });

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
  }

  @Override
  public void onPause() {
    super.onPause();

    if ((handler != null) && slideshow_running) {
      handler.removeCallbacks(slideshow);
      slideshow_running = false;
    }
  }
}
