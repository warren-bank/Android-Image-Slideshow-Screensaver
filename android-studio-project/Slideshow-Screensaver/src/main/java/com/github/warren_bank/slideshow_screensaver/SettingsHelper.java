package com.github.warren_bank.slideshow_screensaver;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

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

  private int get_integer(int resource_key, int resource_default) {
    String pref_key     = context.getString(resource_key);
    String pref_default = context.getString(resource_default);
    String pref_value   = sharedPref.getString(pref_key, pref_default);
    return Integer.parseInt(pref_value, 10);
  }

}
