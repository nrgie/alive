package org.bitbucket.stefanodp91.utils;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.blueobject.app.alive.R;
import com.bumptech.glide.Glide;



import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;


/**
 * Created by stefano on 26/11/2015.
 */
public class ImageUtils {
    private static final String TAG = ImageUtils.class.getName();

    private Context context;

    public ImageUtils(Context context) {
        this.context = context;
    }

    public void loadBitmap(Intent data, File file) {
        Uri imageUri = data.getData();

        // retrieve bitmap from content resolver
        Bitmap bitmap = getBitmapFromUri(imageUri);

        if (bitmap != null) {
            // save the bitmap to a file
            saveBitmapToFile(bitmap, file);
        } else {
            Log.e(TAG, "loadBitmap(...) ==> bitmap is null");
        }
    }

    public void display(ImageView imageView, Uri imageUri) {
        imageView.setVisibility(View.VISIBLE); // if not

        Log.d(TAG, "display(...) ==> imageUri == " + imageUri);

        String convertedUrl;

        if(imageUri.toString().contains("file://")) {
            convertedUrl = String.valueOf(imageUri);
            Glide.with(context)
                    .load(convertedUrl) // Uri of the picture
                    .into(imageView);
        } else {
            Glide.with(context)
                    .load(new File(String.valueOf(imageUri))) // Uri of the picture
                    .into(imageView);
        }


    }

    // check if the values is an image uri
    public boolean isPhotoFile(String string) {
        boolean isUri = false;

        if (string.contains("content://") || string.contains("file://") || string.contains("/storage/")) {
            isUri = true;
        }
        return isUri;
    }

    public File createImageFile() {
        //Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";

        File storageDir = new File(
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
                context.getString(R.string.image_dir_name));

        // create thi file folder if not exists
        if (!storageDir.mkdirs()) {
            Log.d(TAG, "createImageFile() ==> directory already exists");
        }

        File image = null;
        try {
            image = File.createTempFile(
                    imageFileName,  /* prefix */
                    ".jpg",         /* suffix */
                    storageDir      /* directory */
            );
        } catch (IOException e) {
            Log.e(TAG, "createImageFile(...) failed with IOException " + e.getMessage());
        }

        return image;
    }

    private void saveBitmapToFile(Bitmap bitmap, File file) {
        FileOutputStream out = null;
        try {
            out = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
        } catch (Exception e) {
            Log.e(TAG, "saveBitmapToFile(...) failed with IOException " + e.getMessage());
        } finally {
            try {
                if (out != null) {
                    out.close();
                }
            } catch (IOException e) {
                Log.e(TAG, "saveBitmapToFile(...) failed with IOException " + e.getMessage());
            }
        }
    }

    private Bitmap getBitmapFromUri(Uri imageUri) {
        Bitmap bitmap = null;
        try {
            bitmap = MediaStore.Images.Media.getBitmap(context.getContentResolver(), imageUri);
        } catch (IOException e) {
            Log.e(TAG, "getBitmapFromUri(...) failed with IOException " + e.getMessage());
        }

        return bitmap;
    }
}