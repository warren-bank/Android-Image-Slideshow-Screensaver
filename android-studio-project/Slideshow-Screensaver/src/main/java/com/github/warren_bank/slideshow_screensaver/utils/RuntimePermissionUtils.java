package com.github.warren_bank.slideshow_screensaver.utils;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.Manifest.permission;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.Settings;

import java.util.ArrayList;
import java.util.List;

public final class RuntimePermissionUtils {

  private static final int REQUEST_RUNTIME_PERMISSIONS = 0;

  // ---------------------------------------------------------------------------
  // Listener interface

  public interface RuntimePermissionListener {
    public void onRequestPermissionsGranted ();
    public void onRequestPermissionsDenied  ();
  }

  // ---------------------------------------------------------------------------
  // public API

  public static void onRequestPermissionsResult(Activity activity, RuntimePermissionListener listener, int requestCode, String[] permissions, int[] grantResults) {
    if (requestCode != REQUEST_RUNTIME_PERMISSIONS) return;

    boolean OK = (grantResults != null) && (grantResults.length > 0);

    if (OK) {
      for (int result : grantResults) {
        OK &= (result != PackageManager.PERMISSION_DENIED);
        if (!OK) break;
      }
    }

    if (OK)
      listener.onRequestPermissionsGranted();
    else
      requestPermissions(activity, listener, true);
  }

  public static void onActivityResult(Activity activity, RuntimePermissionListener listener, int requestCode, int resultCode, Intent data) {
    if (requestCode != REQUEST_RUNTIME_PERMISSIONS) return;

    requestPermissions(activity, listener, true);
  }

  public static void requestPermissions(Activity activity, RuntimePermissionListener listener) {
    requestPermissions(activity, listener, false);
  }

  private static void requestPermissions(Activity activity, RuntimePermissionListener listener, boolean verbose) {
    List<String> permissions = new ArrayList<String>();
    List<Intent> intents   = new ArrayList<Intent>();

    Uri uri = Uri.parse("package:" + activity.getPackageName());

    if (Build.VERSION.SDK_INT >= 33) {
      if (activity.checkSelfPermission(permission.READ_MEDIA_IMAGES) != PackageManager.PERMISSION_GRANTED) {
        permissions.add(permission.READ_MEDIA_IMAGES);
      }
    }

    if (Build.VERSION.SDK_INT >= 30) {
      if (!Environment.isExternalStorageManager()) {
        intents.add(
          new Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION, uri)
        );
      }
    }
    else if (Build.VERSION.SDK_INT >= 23) {
      if (activity.checkSelfPermission(permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
        permissions.add(permission.READ_EXTERNAL_STORAGE);
      }
    }

    if (!permissions.isEmpty()) {
      activity.requestPermissions(permissions.toArray(new String[permissions.size()]), REQUEST_RUNTIME_PERMISSIONS);
    }

    if (!intents.isEmpty()) {
      for (Intent intent : intents) {
        activity.startActivityForResult(intent, REQUEST_RUNTIME_PERMISSIONS);
      }
    }

    if (permissions.isEmpty() && intents.isEmpty()) {
      listener.onRequestPermissionsGranted();
    }
    else if (verbose) {
      listener.onRequestPermissionsDenied();
    }
  }
}
