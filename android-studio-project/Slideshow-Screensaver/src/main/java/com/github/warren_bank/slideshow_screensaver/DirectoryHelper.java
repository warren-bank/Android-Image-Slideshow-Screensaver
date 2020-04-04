package com.github.warren_bank.slideshow_screensaver;

import android.graphics.BitmapFactory;
import android.media.ExifInterface;
import android.net.Uri;

import java.io.File;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Arrays;

public class DirectoryHelper {

  public static ArrayList<MediaStoreData> getImagesInDirectory(File directory, boolean recurse) {
    ArrayList<MediaStoreData> mediaStoreData = new ArrayList<MediaStoreData>();

    getImagesInDirectory(directory, recurse, mediaStoreData);
    return mediaStoreData;
  }

  public static void getImagesInDirectory(File directory, boolean recurse, ArrayList<MediaStoreData> mediaStoreData) {
    try {
      File[] contents = directory.listFiles();
      ArrayList<File> subdirs = null;
      String mime;
      Uri uri;
      long rowId;
      long dateModified;
      int orientation;

      if (contents == null)
        return;

      if (recurse)
        subdirs = new ArrayList<File>();

      // sort filenames within current directory
      Arrays.sort(contents);

      for (File file : contents) {
        if (file.isDirectory()) {
          if (recurse) subdirs.add(file);
          continue;
        }

        if (!file.isFile())
          continue;

        mime = getImageMimeType(file);
        if (mime == null)
          continue;
        if (!mime.startsWith("image"))
          continue;

        rowId        = mediaStoreData.isEmpty() ? 1l : (mediaStoreData.get(mediaStoreData.size() - 1).rowId + 1l);
        uri          = Uri.fromFile(file);
        dateModified = file.lastModified();
        orientation  = getImageOrientation(file);

        mediaStoreData.add(
          new MediaStoreData(rowId, uri, mime, /* dateTaken= */ dateModified, dateModified, orientation, MediaStoreData.Type.IMAGE)
        );
      }

      if (recurse) {
        while (!subdirs.isEmpty()) {
          getImagesInDirectory(subdirs.remove(0), recurse, mediaStoreData);
        }
      }
    }
    catch(Exception e) {}
  }

  private static String getImageMimeType(File file) {
    String mime = null;

    try {
      mime = URLConnection.guessContentTypeFromName(file.getAbsolutePath());
    }
    catch (Exception e) {
      mime = null;
    }

    if ((mime == null) || mime.isEmpty()) {
      try {
        // Test mime type by loading the image
        BitmapFactory.Options opt = new BitmapFactory.Options();
        opt.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(file.getAbsolutePath(), opt);
        mime = opt.outMimeType;
      }
      catch(Exception e) {
        mime = null;
      }
    }

    return mime;
  }

  private static int getImageOrientation(File file) {
    try {
      ExifInterface exif = new ExifInterface(file.getAbsolutePath());
      int orientation    = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
      return orientation;
    }
    catch(Exception e) {
      return ExifInterface.ORIENTATION_NORMAL;
    }
  }

}
