package lumio.com.lumio;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Bundle;
import android.view.SurfaceView;
import android.view.View;
import android.widget.ImageButton;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

public class LumioActivity extends Activity {

    private static Parameters params;
    private static Camera camera;
    ImageButton btnSwitch;
    CameraPreview mPreview;
    MediaPlayer mp;
    private boolean isFlashOn;
    private boolean hasFlash;

    /**
     * A safe way to get an instance of the Camera object.
     */
    public static Camera getCameraInstance() {

        try {
            camera = Camera.open(0); // attempt to get a Camera instance
            //c.setPreviewDisplay(mHolder);
            params = camera.getParameters();
        } catch (Exception e) {
            // Camera is not available (in use or does not exist)
            e.printStackTrace();
        }
        return camera; // returns null if camera is unavailable
    }

    @SuppressWarnings("deprecation")
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        // First check if device is supporting flashlight or not
        hasFlash = getApplicationContext().getPackageManager()
                .hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH);

        if (!hasFlash) {
            // device doesn't support flash
            // Show alert message and close the application
            AlertDialog alert = new AlertDialog.Builder(LumioActivity.this)
                    .create();
            alert.setTitle("Error");
            alert.setMessage("Sorry, your device doesn't support flash light!");
            alert.setButton("OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    // closing the application
                    finish();
                }
            });
            alert.show();
            return;
        }


        setContentView(R.layout.activity_lumio);

        AdView adView = (AdView) this.findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().addTestDevice("E3701ED24111B22A5D023B68FE77F45A").build();
        adView.loadAd(adRequest);

        // flash switch button
        btnSwitch = (ImageButton) findViewById(R.id.btnSwitch);
        // preview = (SurfaceView) findViewById(R.id.PREVIEW);

        camera = getCameraInstance();

        mPreview = new CameraPreview(this, camera);
        SurfaceView preview = (SurfaceView) findViewById(R.id.camera_preview);
        //preview.addView(mPreview);

        // displaying button image
        toggleButtonImage();

        // Switch button click event to toggle flash on/off
        btnSwitch.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (isFlashOn) {
                    // turn off flash
                    turnOffFlash();
                } else {
                    // turn on flash
                    try {
                        turnOnFlash();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });


    }

    // Turning On flash
    private void turnOnFlash() {

        if (!isFlashOn) {
            if (camera == null || params == null) {
                return;
            }
            // play sound
            playSound();

            params = camera.getParameters();
            params.setFlashMode(Parameters.FLASH_MODE_TORCH);
            camera.setParameters(params);
            camera.startPreview();
            isFlashOn = true;

            // changing button/switch image
            toggleButtonImage();
        }

    }


    // Turning Off flash
    private void turnOffFlash() {

        if (isFlashOn) {
            if (camera == null || params == null) {
                return;
            }
            // play sound
            playSound();

            params = camera.getParameters();
            params.setFlashMode(Parameters.FLASH_MODE_OFF);
            camera.setParameters(params);
            camera.stopPreview();

            isFlashOn = false;

            // changing button/switch image
            toggleButtonImage();
        }
    }


    // Playing sound
    // will play button toggle sound on flash on / off
    private void playSound() {

        if(isFlashOn){
            mp = MediaPlayer.create(LumioActivity.this, R.raw.light_switch_off);
        }else{
            mp = MediaPlayer.create(LumioActivity.this, R.raw.light_switch_on);
        }
        mp.setOnCompletionListener(new OnCompletionListener() {

            @Override
            public void onCompletion(MediaPlayer mp) {

                mp.release();
            }
        });
        mp.start();
    }

    /*
     * Toggle switch button images
     * changing image states to on / off
     * */
    private void toggleButtonImage() {

        if(isFlashOn){
            btnSwitch.setImageResource(R.mipmap.light_on);
        }else{
            btnSwitch.setImageResource(R.mipmap.light_off);
        }
    }

    @Override
    protected void onDestroy() {

        super.onDestroy();
    }

    @Override
    protected void onPause() {

        super.onPause();
        // on pause turn off the flash
        turnOffFlash();
    }

    @Override
    protected void onRestart() {

        super.onRestart();
    }

    @Override
    protected void onResume() {
        super.onResume();

        // on resume turn on the flash
        if(hasFlash)
            turnOnFlash();
    }


    @Override
    protected void onStop() {

        super.onStop();

        // on stop release the camera
        if (camera != null) {
            camera.release();
            camera = null;
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        // on starting the app get the camera params
        getCameraInstance();
    }


}