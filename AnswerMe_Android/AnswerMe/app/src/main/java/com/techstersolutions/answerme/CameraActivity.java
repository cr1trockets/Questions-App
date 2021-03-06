/**
 * CameraActivity.java
 * Techster Solutions
 * Jason John
 */
package com.techstersolutions.answerme;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.hardware.Camera;
import android.hardware.Camera.PictureCallback;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.FrameLayout;

import com.techstersolutions.answerme.R;


@SuppressWarnings("deprecation")
public class CameraActivity extends Activity {
    public static final String TAG = "QA_APP";
    Camera mCamera;
    CameraPreview mCamPreview;
    FrameLayout mLayout_Preview;
    Button mShutterButton;
    Bitmap mBitmap;
    Intent intent;
    private PictureCallback mPictureCallback;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        intent = new Intent(this, MsgActivity.class);
        mPictureCallback = new PictureCallback() {

            @Override
            public void onPictureTaken(byte[] data, Camera camera) {
//DATA IS JPEG!!!!
                if(data != null) {
                    Log.d(TAG, "Picture taken successfully, data populated");


// Convert to Bitmap
                    mBitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
                    mBitmap = Bitmap.createScaledBitmap(mBitmap, 500, 500, false);

//ROTATE
                    Matrix matrix = new Matrix();
                    matrix.postRotate(90);
                    mBitmap = Bitmap.createBitmap(mBitmap, 0, 0, 500, 500, matrix, true);
//END ROTATE

                    PicUtil.saveToCacheFile(mBitmap);

//                    startActivity(intent);
                    camera.release();


                    Intent result = new Intent();
                    result.putExtra("pic", PicUtil.getCacheFilename());
                    setResult(RESULT_OK, result);
                    finish();
                }
            }
        };
        setContentView(R.layout.activity_camera);

//checking camera hardware
        if(checkCameraHardware(this) == false) {
//post error
        }
/***************************************/
        mShutterButton = (Button) findViewById(R.id.button_capture);
        mShutterButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
// get an image from the camera
                mCamera.takePicture(null, null, mPictureCallback);
            }
        });
        mCamera = getCameraInstance();
        mCamPreview = new CameraPreview(this, mCamera);
        mLayout_Preview = (FrameLayout) findViewById(R.id.camera_preview);
        mLayout_Preview.addView(mCamPreview);

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
// Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.camera, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
// Handle action bar item clicks here. The action bar will
// automatically handle clicks on the Home/Up button, so long
// as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private boolean checkCameraHardware(Context context) {
        if (context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)){
// this device has a camera
            return true;
        } else {
// no camera on this device
            return false;
        }
    }

    public static Camera getCameraInstance(){
        Camera c = null;
        try {
            c = Camera.open(); // attempt to get a Camera instance
        }
        catch (Exception e){
// Camera is not available (in use or does not exist)
        }
        return c; // returns null if camera is unavailable
    }
}