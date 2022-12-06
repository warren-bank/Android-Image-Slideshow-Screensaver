package com.github.warren_bank.slideshow_screensaver;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import java.io.File;

public class SettingsHelper {
  private Context context;
  private SharedPreferences sharedPref;

  public SettingsHelper(Context context) {
    PreferenceManager.setDefaultValues(context, R.xml.preferences, false);
    this.context    = context;
    this.sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
  }

  public int getImageDuration() {
    return get_integer(R.string.pref_imageduration_key, R.string.pref_imageduration_default);
  }

  public boolean useMediaStore() {
    return get_boolean(R.string.pref_usemediastore_key, R.string.pref_usemediastore_default);
  }

  public String getDirectoryPath() {
    return get_string(R.string.pref_directoryselector_key, R.string.pref_directoryselector_default);
  }

  public File getDirectory() {
    try {
      String path = getDirectoryPath();
      File file = new File(path);
      return file.isDirectory() ? file : null;
    }
    catch(Exception e) {
      return null;
    }
  }

  public boolean useDirectoryRecursion() {
    return get_boolean(R.string.pref_recursedirectory_key, R.string.pref_recursedirectory_default);
  }

  public boolean useShuffle() {
    return get_boolean(R.string.pref_shuffle_key, R.string.pref_shuffle_default);
  }

  private int get_integer(int resource_key, int resource_default) {
    String pref_value = get_string(resource_key, resource_default);
    return Integer.parseInt(pref_value, 10);
  }

  private String get_string(int resource_key, int resource_default) {
    String pref_key     = context.getString(resource_key);
    String pref_default = context.getString(resource_default);
    String pref_value   = sharedPref.getString(pref_key, pref_default);
    return pref_value;
  }

  private boolean get_boolean(int resource_key, int resource_default) {
    String  pref_key     = context.getString(resource_key);
    boolean pref_default = context.getString(resource_default).equals("true");
    boolean pref_value   = sharedPref.getBoolean(pref_key, pref_default);
    return pref_value;
  }

}
