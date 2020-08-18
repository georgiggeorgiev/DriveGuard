package com.joro.driveguard.vision;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.ViewGroup;

import androidx.core.app.ActivityCompat;

import com.google.android.gms.common.images.Size;
import com.google.android.gms.vision.CameraSource;

import java.io.IOException;

public class CameraSourcePreview extends ViewGroup
{
    private static final String TAG = "CameraSourcePreview";

    private Context context;
    private SurfaceView surfaceView;
    private boolean isStartRequested;
    private boolean isSurfaceAvailable;
    private CameraSource cameraSource;

    private GraphicsOverlay graphicsOverlay;

    public CameraSourcePreview(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        this.context = context;
        isStartRequested = false;
        isSurfaceAvailable = false;

        surfaceView = new SurfaceView(context);
        surfaceView.getHolder().addCallback(new SurfaceCallback());
        addView(surfaceView);
    }

    public void start(CameraSource cameraSource) throws IOException
    {
        if (cameraSource == null)
        {
            stop();
        }

        this.cameraSource = cameraSource;

        if (this.cameraSource != null)
        {
            isStartRequested = true;
            startIfReady();
        }
    }

    public void start(CameraSource cameraSource, GraphicsOverlay overlay) throws IOException
    {
        graphicsOverlay = overlay;
        start(cameraSource);
    }

    public void stop()
    {
        if (cameraSource != null)
        {
            cameraSource.stop();
        }
    }

    public void release()
    {
        if (cameraSource != null)
        {
            cameraSource.release();
            cameraSource = null;
        }
    }

    private void startIfReady() throws IOException
    {
        if (isStartRequested && isSurfaceAvailable)
        {
            if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED)
            {
                // com.karumi:dexter:4.2.0
                return;
            }
            cameraSource.start(surfaceView.getHolder());
            if (graphicsOverlay != null)
            {
                Size size = cameraSource.getPreviewSize();
                int min = Math.min(size.getWidth(), size.getHeight());
                int max = Math.max(size.getWidth(), size.getHeight());
                if (isPortraitMode())
                {
                    // Portrait mode is rotated by 90 deg
                    graphicsOverlay.setCameraInfo(min, max, cameraSource.getCameraFacing());
                }
                else
                {
                    graphicsOverlay.setCameraInfo(max, min, cameraSource.getCameraFacing());
                }
                graphicsOverlay.clear();
            }
            isStartRequested = false;
        }
    }

    private class SurfaceCallback implements SurfaceHolder.Callback
    {
        @Override
        public void surfaceCreated(SurfaceHolder surface)
        {
            isSurfaceAvailable = true;
            try
            {
                startIfReady();
            }
            catch (IOException e)
            {
                Log.e(TAG, "Could not start camera source", e);
            }
        }

        @Override
        public void surfaceDestroyed(SurfaceHolder surface)
        {
            isSurfaceAvailable = false;
        }

        @Override
        public void surfaceChanged(SurfaceHolder holder, int format, int width, int height)
        {
        }
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom)
    {
        int previewWidth = 320;
        int previewHeight = 240;

        if (cameraSource != null)
        {
            Size size = cameraSource.getPreviewSize();
            if (size != null)
            {
                previewWidth = size.getWidth();
                previewHeight = size.getHeight();
            }
        }

        if (isPortraitMode())
        {
            // Portrait mode is rotated by 90 deg
            int temp = previewWidth;
            previewWidth = previewHeight;
            previewHeight = temp;
        }

        final int viewWidth = right - left;
        final int viewHeight = bottom - top;

        int childWidth;
        int childHeight;
        int childXOffset = 0;
        int childYOffset = 0;
        float widthRatio = (float) viewWidth / (float) previewWidth;
        float heightRatio = (float) viewHeight / (float) previewHeight;

        if (widthRatio > heightRatio)
        {
            childWidth = viewWidth;
            childHeight = (int) ((float) previewHeight * widthRatio);
            childYOffset = (childHeight - viewHeight) / 2;
        }
        else
        {
            childWidth = (int) ((float) previewWidth * heightRatio);
            childHeight = viewHeight;
            childXOffset = (childWidth - viewWidth) / 2;
        }

        for (int i = 0; i < getChildCount(); ++i)
        {
            getChildAt(i).layout
            (
                -1 * childXOffset, -1 * childYOffset,
                childWidth - childXOffset, childHeight - childYOffset
            );
        }

        try
        {
            startIfReady();
        }
        catch (IOException e)
        {
            Log.e(TAG, "Could not start camera source", e);
        }
    }

    private boolean isPortraitMode()
    {
        int orientation = context.getResources().getConfiguration().orientation;
        if (orientation == Configuration.ORIENTATION_LANDSCAPE)
        {
            return false;
        }
        if (orientation == Configuration.ORIENTATION_PORTRAIT)
        {
            return true;
        }

        // Default
        return false;
    }
}
