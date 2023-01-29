package com.github.warren_bank.slideshow_screensaver;

import com.github.warren_bank.slideshow_screensaver.utils.RuntimePermissionUtils;

import com.bumptech.glide.MemoryCategory;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

/** Displays a {@link HorizontalGalleryFragment}. */
@RequiresApi(17)
public class MainActivity extends FragmentActivity implements RuntimePermissionUtils.RuntimePermissionListener {

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    setContentView(R.layout.main_activity);
    GlideApp.get(this).setMemoryCategory(MemoryCategory.HIGH);

    RuntimePermissionUtils.requestPermissions(MainActivity.this, MainActivity.this);
  }

  // ---------------------------------------------------------------------------
  // request runtime permissions

  @Override
  public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
    super.onRequestPermissionsResult(requestCode, permissions, grantResults);

    RuntimePermissionUtils.onRequestPermissionsResult(MainActivity.this, MainActivity.this, requestCode, permissions, grantResults);
  }

  @Override
  protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);

    RuntimePermissionUtils.onActivityResult(MainActivity.this, MainActivity.this, requestCode, resultCode, data);
  }

  @Override
  public void onRequestPermissionsGranted() {
    replaceFragment();
  }

  @Override
  public void onRequestPermissionsDenied() {
    Toast.makeText(this, getString(R.string.toast_storage_permission_denied), Toast.LENGTH_LONG).show();
  }

  // ---------------------------------------------------------------------------
  // display horizontal gallery fragment

  private void replaceFragment() {
    Fragment fragment = new HorizontalGalleryFragment();
    getSupportFragmentManager()
      .beginTransaction()
      .replace(R.id.fragment_container, fragment)
      .commit();
  }

}
