package com.blueobject.app.alive.helper;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.ContentUris;
import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Camera;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraManager;
import android.net.Uri;
import android.os.Build;
import android.os.Vibrator;
import android.provider.ContactsContract;
import android.provider.Settings;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.widget.EditText;
import android.widget.RemoteViews;
import android.widget.Spinner;

import com.blueobject.app.alive.Global;
import com.blueobject.app.alive.R;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by nrgie on 2017.08.22..
 */

public class Utils {

    public static boolean flashing = false;

    public static double mapValueFromRangeToRange(double value, double fromLow, double fromHigh, double toLow, double toHigh) {
        return toLow + ((value - fromLow) / (fromHigh - fromLow) * (toHigh - toLow));
    }

    public static double clamp(double value, double low, double high) {
        return Math.min(Math.max(value, low), high);
    }

    public static void flashandvibrate() {
        vibrate();
        if(flashing) {
            flash(false);
        } else {
            flash(true);
        }
    }

    public static void vibrate() {
        Vibrator v = (Vibrator) Global.appContext.getSystemService(Context.VIBRATOR_SERVICE);
        long[] pattern = {500, 200, 500};
        v.vibrate(pattern, -1);
    }

    public static void flash(boolean on) {

        if(on) {
            flashing = true;
        } else {
            flashing = false;
        }

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                CameraManager cameraManager = (CameraManager) Global.appContext.getSystemService(Context.CAMERA_SERVICE);
                for (String id : cameraManager.getCameraIdList()) {
                    if (cameraManager.getCameraCharacteristics(id).get(CameraCharacteristics.FLASH_INFO_AVAILABLE)) {
                        cameraManager.setTorchMode(id, on);
                    }
                }
            } else {
                Camera cam = Camera.open();
                Camera.Parameters p = cam.getParameters();
                p.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
                cam.setParameters(p);
                if(on)  cam.startPreview();
                else {
                    cam.stopPreview();
                    cam.release();
                }
            }
        } catch (CameraAccessException e) {
            Log.e("APP", "Failed to interact with camera.", e);
        }
    }


    public static void putSOSnotify() {

        RemoteViews v = new RemoteViews(Global.appContext.getPackageName(), R.layout.custom_push);
        v.setImageViewResource(R.id.image, R.drawable.smllogotr);

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(Global.appContext)
                .setSmallIcon(R.drawable.smllogotr)
                .setContent(v);

        Notification notification = mBuilder.build();
        notification.flags |= Notification.FLAG_AUTO_CANCEL;
        notification.defaults |= Notification.DEFAULT_SOUND;
        notification.defaults |= Notification.DEFAULT_VIBRATE;
        NotificationManager manager = (NotificationManager) Global.appContext.getSystemService(Context.NOTIFICATION_SERVICE);
        manager.notify(1, notification);

    }


    public static Bitmap getContactPhoto(Long contactID) {
        Bitmap photo = null;
        try {
            InputStream inputStream = ContactsContract.Contacts.openContactPhotoInputStream(Global.appContext.getContentResolver(), ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI, new Long(contactID)));
            if (inputStream != null) {
                photo = BitmapFactory.decodeStream(inputStream);
                inputStream.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return photo;
    }

    public static Bitmap openDisplayPhoto(long contactId) {
        Bitmap photo = null;
        Uri contactUri = ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI, contactId);
        Uri photoUri = Uri.withAppendedPath(contactUri, ContactsContract.Contacts.Photo.CONTENT_DIRECTORY);
        Cursor cursor = Global.appContext.getContentResolver().query(photoUri, new String[] {ContactsContract.Contacts.Photo.PHOTO}, null, null, null);
        if (cursor == null) { return null; }
        try {
            if (cursor.moveToFirst()) {
                byte[] data = cursor.getBlob(0);
                if (data != null) {
                    photo = BitmapFactory.decodeStream(new ByteArrayInputStream(data));
                }
            }
        } finally {
            cursor.close();
        }
        return photo;
    }
}
