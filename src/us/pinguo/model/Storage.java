package us.pinguo.model;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.location.Location;
import android.media.ExifInterface;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;

import java.io.File;
import java.io.IOException;

/**
 * Created by Mr 周先森 on 2015/4/20.
 */
public class Storage {

    private static final String TAG = Storage.class.getSimpleName();

    // Add the image to media store.
    public static Uri addImage(ContentResolver resolver, String title,
                               long date, Location location, int orientation,
                               File file) {

        String filePath = file.getPath();
        String filename = file.getName();
        long jpegLength = file.length();

        Log.i(TAG, "Add image:" + date);
        // Insert into MediaStore.
        ContentValues values = new ContentValues(9);
        values.put(MediaStore.Images.ImageColumns.TITLE, title);
        values.put(MediaStore.Images.ImageColumns.DISPLAY_NAME, filename);
        values.put(MediaStore.Images.ImageColumns.DATE_TAKEN, date);
        values.put(MediaStore.Images.ImageColumns.MIME_TYPE, "image/jpeg");
        // Clockwise rotation in degrees. 0, 90, 180, or 270.
        values.put(MediaStore.Images.ImageColumns.ORIENTATION, orientation);
        values.put(MediaStore.Images.ImageColumns.DATA, filePath);
        values.put(MediaStore.Images.ImageColumns.SIZE, jpegLength);
        if (location != null) {
            values.put(MediaStore.Images.ImageColumns.LATITUDE, location.getLatitude());
            values.put(MediaStore.Images.ImageColumns.LONGITUDE, location.getLongitude());
        }

        try {
            return resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
        } catch (Throwable th) {
            // This can happen when the external volume is already mounted, but
            // MediaScanner has not notify MediaProvider to add that volume.
            // The picture is still safe and MediaScanner will find it and
            // insert it into MediaProvider. The only problem is that the user
            // cannot click the thumbnail to review the picture.
            Log.e(TAG, "Failed to write MediaStore" + th);
            return null;
        }
    }

    public static void addStorage(Context context, String path) {
        File scanFile = new File(path);
        if (scanFile.exists()) {
            String orientation = null;
            try {
                ExifInterface exifInterface = new ExifInterface(path);
                orientation = exifInterface.getAttribute(ExifInterface.TAG_ORIENTATION);
            } catch (IOException e) {
                e.printStackTrace();
            }
            addImage(context.getContentResolver(),
                    scanFile.getName(), scanFile.lastModified(), null, Integer.parseInt(orientation), scanFile);
        }
    }
}
