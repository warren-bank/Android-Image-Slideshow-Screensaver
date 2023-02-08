package com.github.warren_bank.slideshow_screensaver;

import com.bumptech.glide.ListPreloader;
import com.bumptech.glide.RequestBuilder;
import com.bumptech.glide.load.Key;
import com.bumptech.glide.signature.MediaStoreSignature;
import com.bumptech.glide.util.Preconditions;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.widget.ImageView;

import java.util.Collections;
import java.util.List;

/** Displays {@link com.github.warren_bank.slideshow_screensaver.MediaStoreData} in a recycler view. */
class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.ListViewHolder>
    implements ListPreloader.PreloadSizeProvider<MediaStoreData>,
        ListPreloader.PreloadModelProvider<MediaStoreData> {

  private final Context context;
  private final List<MediaStoreData> data;
  private final GlideRequest<Drawable> requestBuilder;

  private int[] screenDimensions;

  RecyclerAdapter(Context context, List<MediaStoreData> data, GlideRequests glideRequests) {
    this.context        = context;
    this.data           = data;
    this.requestBuilder = glideRequests.asDrawable().fitCenter();

    updateOrientation(/* usePortraitOrientation= */ true);
    setHasStableIds(true);
  }

  @NonNull
  @Override
  public ListViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
    LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
    final View view = inflater.inflate(R.layout.recycler_item, viewGroup, false);

    return new ListViewHolder(view);
  }

  @Override
  public void onBindViewHolder(@NonNull ListViewHolder viewHolder, int position) {
    updateFrameWidth(viewHolder);

    try {
      MediaStoreData current = data.get(position);
      Key signature          = new MediaStoreSignature(current.mimeType, current.dateModified, current.orientation);

      requestBuilder.clone().signature(signature).load(current.uri).into(viewHolder.image);
    }
    catch(Exception e) {
      viewHolder.image.setImageDrawable(context.getResources().getDrawable(R.drawable.launcher));
    }
  }

  @Override
  public long getItemId(int position) {
    return data.get(position).rowId;
  }

  @Override
  public int getItemCount() {
    return data.size();
  }

  @Override
  public int getItemViewType(int position) {
    return 0;
  }

  @NonNull
  @Override
  public List<MediaStoreData> getPreloadItems(int position) {
    return data.isEmpty()
        ? Collections.<MediaStoreData>emptyList()
        : Collections.singletonList(data.get(position));
  }

  @Nullable
  @Override
  public RequestBuilder<Drawable> getPreloadRequestBuilder(@NonNull MediaStoreData item) {
    MediaStoreSignature signature =
        new MediaStoreSignature(item.mimeType, item.dateModified, item.orientation);
    return requestBuilder.clone().signature(signature).load(item.uri);
  }

  @Nullable
  @Override
  public int[] getPreloadSize(@NonNull MediaStoreData item, int adapterPosition, int perItemPosition) {
    return screenDimensions;
  }

  public void updateOrientation(boolean usePortraitOrientation) {
    screenDimensions = getScreenDimensions(context, usePortraitOrientation);
  }

  public void updateFrameWidth(@NonNull ListViewHolder viewHolder) {
    if (screenDimensions != null)
      viewHolder.updateFrameWidth(screenDimensions[0]);
  }

  /*
   * return: [width, height]
   */
  @SuppressWarnings("deprecation")
  private static int[] getScreenDimensions(Context context, boolean usePortraitOrientation) {
    WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
    Display display = Preconditions.checkNotNull(wm).getDefaultDisplay();
    Point size = new Point();
    display.getSize(size);
    int[] screenDimensions = usePortraitOrientation
      ? new int[]{ Math.min(size.x, size.y), Math.max(size.x, size.y) }  // portrait
      : new int[]{ Math.max(size.x, size.y), Math.min(size.x, size.y) }; // landscape
    return screenDimensions;
  }

  /**
   * ViewHolder containing views to display individual {@link
   * com.github.warren_bank.slideshow_screensaver.MediaStoreData}.
   */
  static final class ListViewHolder extends RecyclerView.ViewHolder {

    private final View      frame;
    private final ImageView image;

    ListViewHolder(View itemView) {
      super(itemView);
      frame = itemView;
      image = itemView.findViewById(R.id.image);
    }

    public void updateFrameWidth(int width) {
      frame.getLayoutParams().width = width;
      frame.requestLayout();
    }
  }
}
