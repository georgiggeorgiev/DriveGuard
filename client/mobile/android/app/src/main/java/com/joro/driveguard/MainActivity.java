package com.joro.driveguard;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.MultiProcessor;
import com.google.android.gms.vision.Tracker;
import com.google.android.gms.vision.face.Face;
import com.google.android.gms.vision.face.FaceDetector;
import com.google.android.gms.vision.face.LargestFaceFocusingProcessor;
import com.google.android.material.snackbar.Snackbar;

import java.io.IOException;

import com.joro.driveguard.vision.CameraSourcePreview;
import com.joro.driveguard.vision.FaceTracker;
import com.joro.driveguard.vision.GraphicsOverlay;

public final class MainActivity extends AppCompatActivity
{
    private static final String TAG = "EyeTracker";

    private static final int RC_HANDLE_GMS = 9001;

    // Permission request codes need to be < 256
    private static final int RC_HANDLE_CAMERA_PERM = 2;

    private CameraSource cameraSource = null;
    private CameraSourcePreview cameraSourcePreview;
    private GraphicsOverlay graphicsOverlay;

    private boolean isFrontFacing = true;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        setContentView(R.layout.activity_main);

        cameraSourcePreview = findViewById(R.id.preview);
        graphicsOverlay = findViewById(R.id.faceOverlay);

        // Camera permission
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED)
        {
            createCameraSource();
        } else
        {
            requestCameraPermission();
        }
    }

    @Override
    public void onBackPressed()
    {
        // Disable going back
        moveTaskToBack(true);
    }

    private void requestCameraPermission()
    {
        Log.d(TAG, "Camera permission is not granted. Requesting permission");

        final String[] permissions = new String[]{Manifest.permission.CAMERA};

        if (!ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.CAMERA))
        {
            ActivityCompat.requestPermissions(this, permissions, RC_HANDLE_CAMERA_PERM);
            return;
        }

        final Activity thisActivity = this;

        View.OnClickListener listener = view -> ActivityCompat.requestPermissions(thisActivity, permissions, RC_HANDLE_CAMERA_PERM);

        Snackbar.make(graphicsOverlay, "Необходим е достъп до камерата",
                Snackbar.LENGTH_INDEFINITE)
                .setAction("ОК", listener)
                .show();
    }

    @Override
    protected void onResume()
    {
        super.onResume();

        startCameraSource();
    }

    @Override
    protected void onPause()
    {
        super.onPause();
        cameraSourcePreview.stop();
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        if (cameraSource != null)
        {
            cameraSource.release();
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults)
    {
        if (requestCode != RC_HANDLE_CAMERA_PERM)
        {
            Log.d(TAG, "Permission error: " + requestCode);
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
            return;
        }

        if (grantResults.length != 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
        {
            Log.d(TAG, "Initialize camera source");
            createCameraSource();
            return;
        }

        Log.d(TAG, "Permission not granted: results len = " + grantResults.length + " Result code = " + (grantResults.length > 0 ? grantResults[0] : "(empty)"));

        DialogInterface.OnClickListener listener = (dialog, id) -> finish();

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Face Tracker sample")
                .setMessage("Приложението не може да работи без достъп до камерата")
                .setPositiveButton("ОК", listener)
                .show();
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle savedInstanceState)
    {
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putBoolean("IsFrontFacing", isFrontFacing);
    }

    private View.OnClickListener flipButtonListener = v ->
    {
        isFrontFacing = !isFrontFacing;

        if (cameraSource != null)
        {
            cameraSource.release();
            cameraSource = null;
        }

        createCameraSource();
        startCameraSource();
    };

    @NonNull
    private FaceDetector createFaceDetector(Context context)
    {

        FaceDetector detector = new FaceDetector.Builder(context)
                .setLandmarkType(FaceDetector.ALL_LANDMARKS)
                .setClassificationType(FaceDetector.ALL_CLASSIFICATIONS)
                .setTrackingEnabled(true)
                .setMode(FaceDetector.FAST_MODE)
                .setProminentFaceOnly(isFrontFacing)
                .setMinFaceSize(isFrontFacing ? 0.35f : 0.15f)
                .build();

        Detector.Processor<Face> processor;
        if (isFrontFacing)
        {
            // Front facing
            Tracker<Face> tracker = new FaceTracker(graphicsOverlay);
            processor = new LargestFaceFocusingProcessor.Builder(detector, tracker).build();
        } else
        {
            // Rear facing
            MultiProcessor.Factory<Face> factory = face -> new FaceTracker(graphicsOverlay);
            processor = new MultiProcessor.Builder<>(factory).build();
        }

        detector.setProcessor(processor);

        if (!detector.isOperational())
        {
            Log.d(TAG, "Face detector dependencies are not yet available");

            // Check storage
            IntentFilter lowStorageFilter = new IntentFilter(Intent.ACTION_DEVICE_STORAGE_LOW);
            boolean hasLowStorage = registerReceiver(null, lowStorageFilter) != null;

            if (hasLowStorage)
            {
                final String lowStorage = "Приложението не може да работи поради недостиг на памет";
                Toast.makeText(this, lowStorage, Toast.LENGTH_LONG).show();
                Log.d(TAG, lowStorage);
            }
        }
        return detector;
    }

    private void createCameraSource()
    {
        Context context = getApplicationContext();
        FaceDetector detector = createFaceDetector(context);

        int facing = CameraSource.CAMERA_FACING_FRONT;

        if (!isFrontFacing)
        {
            facing = CameraSource.CAMERA_FACING_BACK;
        }

        cameraSource = new CameraSource.Builder(context, detector)
                .setFacing(facing)
                .setRequestedPreviewSize(320, 240)
                .setRequestedFps(60.0f)
                .setAutoFocusEnabled(true)
                .build();
    }


    private void startCameraSource()
    {
        // Check google play
        int code = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(getApplicationContext());

        if (code != ConnectionResult.SUCCESS)
        {
            Dialog dlg = GoogleApiAvailability.getInstance().getErrorDialog(this, code, RC_HANDLE_GMS);
            dlg.show();
        }

        if (cameraSource != null)
        {
            try
            {
                cameraSourcePreview.start(cameraSource, graphicsOverlay);
            } catch (IOException e)
            {
                Log.d(TAG, "Unable to start camera source", e);
                cameraSource.release();
                cameraSource = null;
            }
        }
    }
}
