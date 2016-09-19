package com.assignment.xiaoduo.survivalguide.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.widget.Button;

import com.assignment.xiaoduo.survivalguide.configurations.LocalConfiguration;
import com.assignment.xiaoduo.survivalguide.util.Util;
import com.assignment.xiaoduo.survivalguidefit4039assignment.R;
import com.edmodo.cropper.CropImageView;

public class CroperActivity extends Activity {

    // Static final constants
    private static final int DEFAULT_ASPECT_RATIO_VALUES = 100;
    private static final int ROTATE_NINETY_DEGREES = 90;
    private static final int ON_TOUCH = 1;
    Bitmap croppedImage;
    public static final String IMAGE_PATH = "image_path";
    public static final String GET_IMAGE_MODE = "get_image_mode";

    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_croper);
        String imagePath;
        int degree;
        Uri uri;
        final CropImageView cropImageView = (CropImageView) findViewById(R.id.CropImageView);
        final Button rotateButton = (Button) findViewById(R.id.Button_rotate);
        final Button cropButton = (Button) findViewById(R.id.Button_crop);

        cropImageView.setFixedAspectRatio(true);
        cropImageView.setGuidelines(ON_TOUCH);
        // Sets initial aspect ratio to 10/10, for demonstration purposes
        cropImageView.setAspectRatio(DEFAULT_ASPECT_RATIO_VALUES,
                DEFAULT_ASPECT_RATIO_VALUES);

        rotateButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                cropImageView.rotateImage(ROTATE_NINETY_DEGREES);
            }
        });

        cropButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Bitmap tempImage = cropImageView
                        .getCroppedImage();
                tempImage = scaleDownBitmap(tempImage, 100, CroperActivity.this);
                Intent data = new Intent();
                data.putExtra("croped_image", tempImage);
                setResult(RESULT_OK, data);
                finish();
            }
        });

        int getImageMode = getIntent().getIntExtra(GET_IMAGE_MODE, -1);

        if (getImageMode == MainActivity.PICK_Camera_IMAGE) {
            imagePath = getIntent().getStringExtra(IMAGE_PATH);
            degree = Util.readPictureDegree(imagePath);
            croppedImage = Util.rotateBitmap(
                    Util.decodeFile(imagePath, LocalConfiguration.bitMapCameraCompress),
                    degree);
        } else if (getImageMode == MainActivity.PICK_IMAGE) {
            uri = getIntent().getData();
            try {
                croppedImage = Util.decodeFile(croppedImage, uri, LocalConfiguration.bitMapCompress, this);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        cropImageView.setImageBitmap(croppedImage);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public void onDestroy() {
        croppedImage.recycle();
        super.onDestroy();
    }

    public static Bitmap scaleDownBitmap(Bitmap photo, int newHeight, Context context) {

        final float densityMultiplier = context.getResources().getDisplayMetrics().density;

        int h = (int) (newHeight * densityMultiplier);
        int w = (int) (h * photo.getWidth() / ((double) photo.getHeight()));

        photo = Bitmap.createScaledBitmap(photo, w, h, true);

        return photo;
    }
}
