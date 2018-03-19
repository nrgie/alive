package org.bitbucket.stefanodp91.ui;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.blueobject.app.alive.R;
import org.bitbucket.stefanodp91.model.Page;
import org.bitbucket.stefanodp91.utils.Constants;
import org.bitbucket.stefanodp91.utils.ImageUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;


public class ImageFragment extends Fragment implements View.OnClickListener {
    private static final String TAG = ImageFragment.class.getName();

    private static final String NEW_IMAGE_URI = "new_image_uri";
    private static final int GALLERY_REQUEST_CODE = 0;
    private static final int CAMERA_REQUEST_CODE = 1;

    protected static final String ARG_KEY = "key";

    private File photoFile = null;

    private PageFragmentCallbacks mCallbacks;
    private String mKey;
    private Page mPage;

    private ImageView imageView;
    private LinearLayout boxImagePreview;

    private Button cameraBtn, galleryBtn;

    private Uri mNewImageUri;
    private ImageUtils imageUtils;

    public static ImageFragment create(String key) {
        Bundle args = new Bundle();
        args.putString(ARG_KEY, key);

        ImageFragment f = new ImageFragment();
        f.setArguments(args);
        return f;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle args = getArguments();
        mKey = args.getString(ARG_KEY);
        mPage = mCallbacks.onGetPage(mKey);

        if (savedInstanceState != null) {
            String uriString = savedInstanceState.getString(NEW_IMAGE_URI);
            if (!TextUtils.isEmpty(uriString)) {
                mNewImageUri = Uri.parse(uriString);
            }
        }

        imageUtils = new ImageUtils(getContext());
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mNewImageUri != null) {
            outState.putString(NEW_IMAGE_URI, mNewImageUri.toString());
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_page_image, container, false);
        ((TextView) rootView.findViewById(android.R.id.title)).setText(mPage.getTitle());

        cameraBtn = (Button) rootView.findViewById(R.id.camera_btn);
        cameraBtn.setOnClickListener(this);

        galleryBtn = (Button) rootView.findViewById(R.id.gallery_btn);
        galleryBtn.setOnClickListener(this);

        boxImagePreview = (LinearLayout) rootView.findViewById(R.id.box_image_preview);

        imageView = (ImageView) rootView.findViewById(R.id.imageView);

        String imageData = mPage.getData().getString(Page.SIMPLE_DATA_KEY);
        if (!TextUtils.isEmpty(imageData)) {
            Uri imageUri = Uri.parse(imageData);
            boxImagePreview.setVisibility(View.VISIBLE);
            imageUtils.display(imageView, imageUri);
        } else {
            imageView.setImageResource(R.drawable.ic_person);
        }

        return rootView;
    }

    private void dispatchTakePictureFromGalleryIntent() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

        // Create the File where the photo should go
        photoFile = imageUtils.createImageFile();
        // path of the file
        mNewImageUri = Uri.fromFile(photoFile);

        // Continue only if the File was successfully created
        if (photoFile != null) {
            startActivityForResult(intent, GALLERY_REQUEST_CODE);
        }
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getActivity().getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = imageUtils.createImageFile();
            // path of the file
            mNewImageUri = Uri.fromFile(photoFile);

            // Continue only if the File was successfully created
            if (photoFile != null) {
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photoFile));
                startActivityForResult(takePictureIntent, CAMERA_REQUEST_CODE);
            }
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        if (!(activity instanceof PageFragmentCallbacks)) {
            throw new ClassCastException(
                    "Activity must implement PageFragmentCallbacks");
        }

        mCallbacks = (PageFragmentCallbacks) activity;
        imageUtils = new ImageUtils(getContext());
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCallbacks = null;
        imageUtils = null;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            boxImagePreview.setVisibility(View.VISIBLE);

            // switch on the request: gallery or photo
            if (requestCode == CAMERA_REQUEST_CODE) {
                getActivity().setResult(Activity.RESULT_OK);
                Log.d(TAG, "onActivityResult() ==> photo has been taken with success");
            } else if (requestCode == GALLERY_REQUEST_CODE) {
                Log.d(TAG, "onActivityResult() ==> photo has been picked from gallery with success");

                // load the photo from the intent
                imageUtils.loadBitmap(data, photoFile);

                // update the uri
                mNewImageUri = Uri.parse(photoFile.getAbsolutePath());
            }
            // display the photo
            imageUtils.display(imageView, mNewImageUri);
            saveResult();
        } else {
            // there are errors with result code
            Log.e(TAG, "onActivityResult(...) ==> resultCode != Activity.RESULT_OK");
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    private void saveResult() {
        JSONObject jsonObject = new JSONObject();

        try {
            jsonObject.put(Constants.JSON_TYPE, Constants.JSON_TYPE_IMAGE);
        } catch (JSONException e) {
            Log.e(TAG, "updateLocation() ==> JSON_TYPE failed with JSONException. " + e.getMessage());
        }

        try {
            jsonObject.put(Constants.JSON_PAGE_TITLE, mPage.getTitle());
        } catch (JSONException e) {
            Log.e(TAG, "updateLocation() ==> JSON_PAGE_TITLE failed with JSONException. " + e.getMessage());
        }

        try {
            jsonObject.put(Constants.JSON_IMAGE_URI, (mNewImageUri != null) ? mNewImageUri.toString() : null);
        } catch (JSONException e) {
            Log.e(TAG, "updateLocation() ==> JSON_IMAGE_URI failed with JSONException. " + e.getMessage());
        }

        mPage.getData().putString(Page.SIMPLE_DATA_KEY, jsonObject.toString());
        mPage.notifyDataChanged();

        getActivity().setResult(Activity.RESULT_OK);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == cameraBtn.getId()) {
            dispatchTakePictureIntent();
        } else if (id == galleryBtn.getId()) {
            dispatchTakePictureFromGalleryIntent();
        } else {
            Log.e(TAG, "onClick(...) default action");
        }
    }
}