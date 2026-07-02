package com.kotlin.dvijaypatient.camera;

import static android.provider.MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;
import androidx.multidex.BuildConfig;

import com.kotlin.dvijaypatient.global.ClassGlobal;
import com.kotlin.dvijaypatient.utils.CheckForSDCard;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class CameraUtils {
    Context context;

    public static void refreshGallery(Context context, String filePath) {
        // ScanFile so it will be appeared on Gallery
        MediaScannerConnection.scanFile(context,
                new String[]{filePath}, null,
                new MediaScannerConnection.OnScanCompletedListener() {
                    public void onScanCompleted(String path, Uri uri) {
                    }
                });
    }

    public static boolean checkPermissions(Context context) {
        return ActivityCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE) ==
                PackageManager.PERMISSION_GRANTED;
    }

    public static Uri getOutputMediaFileUri(Context context, File file) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            return FileProvider.getUriForFile(context, context.getPackageName() + ".provider", file);
        } else {
            return Uri.fromFile(getOutputMediaFile(context, MEDIA_TYPE_IMAGE));
        }
    }

    public static void openSettings(Context context) {
        Intent intent = new Intent();
        intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        intent.setData(Uri.fromParts("package", BuildConfig.APPLICATION_ID, null));
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    public static File getOutputMediaFile(Context context, int type) {

       /* // External sdcard location
        File mediaStorageDir = new File(
                Environment.getExternalStorageDirectory(),
                ClassGlobal.APP_NAME);

        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists()) {
            mediaStorageDir.mkdirs();
            if (!mediaStorageDir.mkdirs()) {
                Log.e(ClassGlobal.APP_NAME, "Oops! Failed create "
                        + ClassGlobal.APP_NAME + " directory");
                return null;
            }
        }*/

        File mediaStorageDir = null;
        if (new CheckForSDCard().isSDCardPresent()) {
            mediaStorageDir = new File(
                    context.getExternalFilesDir("MyDirectory") + "/"
                            + ClassGlobal.APP_NAME + "/");
        } else
            Toast.makeText(context, "Oops!! There is no SD Card.", Toast.LENGTH_SHORT).show();
        // outputFile = new File(mediaStorageDir, downloadFileName);
        //If File is not present create directory
        if (!mediaStorageDir.exists()) {
            mediaStorageDir.mkdirs();
            Log.e("TAG", "Directory Created.");
        }
        // Preparing media file naming convention
        // adds timestamp
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HH:mm:ss",
                Locale.ENGLISH).format(new Date());
        File mediaFile;
        if (type == 1) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator
                    + "IMG_" + timeStamp + "." + "jpg");
        } else {
            return null;
        }
        return mediaFile;
    }
}
