#### [Slideshow Screensaver](https://github.com/warren-bank/Android-Image-Slideshow-Screensaver/tree/v01)

Android daydream/screensaver app to display images on local device as a slideshow

#### Background:

* Android 4.2 (API 17)
  - [introduces](http://android-developers.blogspot.com/2012/12/daydream-interactive-screen-savers.html) the [DreamService](https://developer.android.com/reference/android/service/dreams/DreamService.html)

#### Quirks:

* the _DreamService_ can only be configured to run while the device is either charging or docked
* the _DreamService_ can only support changes to device orientation if started while the top-most Activity supports changes to device orientation
  - [issue](https://issuetracker.google.com/issues/36959012)
  - examples:
    * the default "home" screen application launcher does __not__ support changes to device orientation
      - the _DreamService_ always runs in portrait orientation
    * the default "settings" screen __does__ support changes to device orientation
      - the _DreamService_ rotates in coordination with the device orientation
  - status:
    * Android 4.2 (2012)
      - issue was reported
    * Android 8.1.0 (2017)
      - commenter reports that the issue is fixed in Oreo
      - issue was closed (won't fix)
      - issue is observed on my stock Oreo device (not fixed)

#### Credits:

* [Glide](https://github.com/bumptech/glide) image loading framework for Android
  - [the "gallery" sample from v4.11.0](https://github.com/bumptech/glide/tree/v4.11.0/samples/gallery) is the basis for this project

* [Directory Selector Dialog Preference](https://github.com/lemberg/directory-selector-dialog-preference) library by [Lemberg Solutions](https://lembergsolutions.com/)
  - makes it simple to add a directory chooser dialog to a `PreferenceFragment` through xml

#### Legal:

* copyright: [Warren Bank](https://github.com/warren-bank)
* license: [GPL-2.0](https://www.gnu.org/licenses/old-licenses/gpl-2.0.txt)
