package com.github.warren_bank.slideshow_screensaver;

import android.app.Activity;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;

public class SettingsActivity extends Activity implements OnSharedPreferenceChangeListener {
  private SettingsFragment settingsFragment;
  private String keyprefDuration;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    keyprefDuration = getString(R.string.pref_imageduration_key);

    settingsFragment = new SettingsFragment();
    getFragmentManager()
      .beginTransaction()
      .replace(android.R.id.content, settingsFragment)
      .commit();
  }

  @Override
  protected void onResume() {
    super.onResume();

    SharedPreferences sharedPreferences = settingsFragment.getPreferenceScreen().getSharedPreferences();
    sharedPreferences.registerOnSharedPreferenceChangeListener(this);

    updateSummary(sharedPreferences, keyprefDuration);
  }

  @Override
  protected void onPause() {
    super.onPause();

    SharedPreferences sharedPreferences = settingsFragment.getPreferenceScreen().getSharedPreferences();
    sharedPreferences.unregisterOnSharedPreferenceChangeListener(this);
  }

  @Override
  public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
    if (key.equals(keyprefDuration)) {
      updateSummary(sharedPreferences, key);
    }
  }

  private void updateSummary(SharedPreferences sharedPreferences, String key) {
    Preference updatedPref = settingsFragment.findPreference(key);

    updatedPref.setSummary(sharedPreferences.getString(key, ""));
  }

  private void updateSummaryB(SharedPreferences sharedPreferences, String key) {
    Preference updatedPref = settingsFragment.findPreference(key);

    updatedPref.setSummary(sharedPreferences.getBoolean(key, true)
      ? getString(R.string.pref_value_enabled)
      : getString(R.string.pref_value_disabled));
  }

  private void updateSummaryList(SharedPreferences sharedPreferences, String key) {
    ListPreference updatedPref = (ListPreference) settingsFragment.findPreference(key);

    updatedPref.setSummary(updatedPref.getEntry());
  }

}
