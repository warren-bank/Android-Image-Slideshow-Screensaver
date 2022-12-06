package com.github.warren_bank.slideshow_screensaver;

import android.app.Activity;
import android.content.pm.PackageManager;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.Manifest.permission;
import android.os.Build;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.widget.Toast;

public class SettingsActivity extends Activity implements OnSharedPreferenceChangeListener {
  private static final int REQUEST_READ_STORAGE = 0;

  private SettingsFragment settingsFragment;
  private String keypref_imageduration;
  private String keypref_usemediastore;
  private String keypref_directoryselector;
  private String keypref_recursedirectory;
  private String keypref_shuffle;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    if ((Build.VERSION.SDK_INT >= 23) && (checkSelfPermission(permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)) {
      requestStoragePermission();
    }
    else {
      replaceFragment();
    }
  }

  private void requestStoragePermission() {
    requestPermissions(new String[] {permission.READ_EXTERNAL_STORAGE}, REQUEST_READ_STORAGE);
  }

  @Override
  public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
    super.onRequestPermissionsResult(requestCode, permissions, grantResults);

    switch (requestCode) {
      case REQUEST_READ_STORAGE : {
        // If request is cancelled, the result arrays are empty.
        if ((grantResults.length > 0) && (grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
          replaceFragment();
        }
        else {
          Toast.makeText(this, getString(R.string.toast_storage_permission_denied), Toast.LENGTH_LONG).show();
          requestStoragePermission();
        }
        break;
      }
    }
  }

  private void replaceFragment() {
    keypref_imageduration     = getString(R.string.pref_imageduration_key);
    keypref_usemediastore     = getString(R.string.pref_usemediastore_key);
    keypref_directoryselector = getString(R.string.pref_directoryselector_key);
    keypref_recursedirectory  = getString(R.string.pref_recursedirectory_key);
    keypref_shuffle           = getString(R.string.pref_shuffle_key);

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

    updateSummary( sharedPreferences, keypref_imageduration);
    updateSummaryB(sharedPreferences, keypref_usemediastore);
    updateSummary( sharedPreferences, keypref_directoryselector);
    updateSummaryB(sharedPreferences, keypref_recursedirectory);
    updateSummaryB(sharedPreferences, keypref_shuffle);

    enable_directoryselector(sharedPreferences);
  }

  @Override
  protected void onPause() {
    super.onPause();

    SharedPreferences sharedPreferences = settingsFragment.getPreferenceScreen().getSharedPreferences();
    sharedPreferences.unregisterOnSharedPreferenceChangeListener(this);
  }

  @Override
  public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
    if (
         key.equals(keypref_imageduration)
      || key.equals(keypref_directoryselector)
    ) {
      updateSummary(sharedPreferences, key);
    }

    if (
         key.equals(keypref_usemediastore)
      || key.equals(keypref_recursedirectory)
      || key.equals(keypref_shuffle)
    ) {
      updateSummaryB(sharedPreferences, key);
    }

    if (key.equals(keypref_usemediastore)) {
      enable_directoryselector(sharedPreferences);
    }
  }

  private void updateSummary(SharedPreferences sharedPreferences, String key) {
    updateSummary(sharedPreferences, key, /* defaultValue= */ "");
  }

  private void updateSummary(SharedPreferences sharedPreferences, String key, String defaultValue) {
    Preference updatedPref = settingsFragment.findPreference(key);

    updatedPref.setSummary(sharedPreferences.getString(key, defaultValue));
  }

  private void updateSummaryB(SharedPreferences sharedPreferences, String key) {
    updateSummaryB(sharedPreferences, key, /* defaultValue= */ false);
  }

  private void updateSummaryB(SharedPreferences sharedPreferences, String key, boolean defaultValue) {
    Preference updatedPref = settingsFragment.findPreference(key);

    updatedPref.setSummary(sharedPreferences.getBoolean(key, defaultValue)
      ? getString(R.string.pref_value_enabled)
      : getString(R.string.pref_value_disabled));
  }

  private void updateSummaryList(SharedPreferences sharedPreferences, String key) {
    ListPreference updatedPref = (ListPreference) settingsFragment.findPreference(key);

    updatedPref.setSummary(updatedPref.getEntry());
  }

  private void enable_directoryselector(SharedPreferences sharedPreferences) {
    if (sharedPreferences == null)
      sharedPreferences = settingsFragment.getPreferenceScreen().getSharedPreferences();

    boolean is_enabled = !sharedPreferences.getBoolean(keypref_usemediastore, false);

    settingsFragment.findPreference(keypref_directoryselector).setEnabled(is_enabled);
    settingsFragment.findPreference(keypref_recursedirectory ).setEnabled(is_enabled);
    settingsFragment.findPreference(keypref_shuffle          ).setEnabled(is_enabled);
  }

}
