package com.github.warren_bank.slideshow_screensaver;

import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.Loader;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.integration.recyclerview.RecyclerViewPreloader;
import java.util.List;

/** Displays media store data in a recycler view. */
public class HorizontalGalleryFragment extends Fragment
    implements LoaderManager.LoaderCallbacks<List<MediaStoreData>> {

  private RecyclerView recyclerView;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    getLoaderManager().initLoader(R.id.loader_id_media_store_data, null, this);
  }

  @Override
  public View onCreateView(
      @NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    View result = inflater.inflate(R.layout.recycler_view, container, false);
    recyclerView = (RecyclerView) result.findViewById(R.id.recycler_view);
    GridLayoutManager layoutManager = new GridLayoutManager(getActivity(), 1);
    layoutManager.setOrientation(RecyclerView.HORIZONTAL);
    recyclerView.setLayoutManager(layoutManager);
    recyclerView.setHasFixedSize(true);

    return result;
  }

  @Override
  public Loader<List<MediaStoreData>> onCreateLoader(int i, Bundle bundle) {
    return new MediaStoreDataLoader(getActivity());
  }

  @Override
  public void onLoadFinished(
      Loader<List<MediaStoreData>> loader, List<MediaStoreData> mediaStoreData) {
    GlideRequests glideRequests = GlideApp.with(this);
    RecyclerAdapter adapter = new RecyclerAdapter(getActivity(), mediaStoreData, glideRequests);
    RecyclerViewPreloader<MediaStoreData> preloader =
        new RecyclerViewPreloader<>(glideRequests, adapter, adapter, 3);
    recyclerView.addOnScrollListener(preloader);
    recyclerView.setAdapter(adapter);

    initSlideshow(adapter);
  }

  @Override
  public void onLoaderReset(Loader<List<MediaStoreData>> loader) {
    // Do nothing.
  }

  private final int delay = 2500;
  private int       current_image_index;
  private Handler   handler;
  private Runnable  slideshow;
  private boolean   slideshow_running;

  private void initSlideshow(RecyclerAdapter adapter) {
    if (adapter == null) {
      slideshow_running = false;
      return;
    }

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

        handler.postDelayed(this, delay);
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
  public void onResume () {
    super.onResume();

    if ((handler != null) && !slideshow_running) {
      handler.postDelayed(slideshow, delay);
      slideshow_running = true;
    }
  }

  @Override
  public void onPause () {
    super.onPause();

    if ((handler != null) && slideshow_running) {
      handler.removeCallbacks(slideshow);
      slideshow_running = false;
    }
  }
}
