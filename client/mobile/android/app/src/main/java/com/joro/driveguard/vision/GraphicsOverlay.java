package com.joro.driveguard.vision;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.View;

import com.google.android.gms.vision.CameraSource;

import java.util.HashSet;
import java.util.Set;

public class GraphicsOverlay extends View
{
    private final Object lock = new Object();
    private int previewWidth;
    private float widthScaleFactor = 1.0f;
    private int previewHeight;
    private float heightScaleFactor = 1.0f;
    private int cameraFacing = CameraSource.CAMERA_FACING_BACK;
    private Set<Graphic> graphicSet = new HashSet<>();

    public static abstract class Graphic
    {
        private GraphicsOverlay overlay;

        public Graphic(GraphicsOverlay overlay)
        {
            this.overlay = overlay;
        }

        public abstract void draw(Canvas canvas);

        public float scaleX(float horizontal)
        {
            // Apply horizontal scaling
            return horizontal * overlay.widthScaleFactor;
        }

        public float scaleY(float vertical)
        {
            // Apply vertical scaling
            return vertical * overlay.heightScaleFactor;
        }

        public float translateX(float x)
        {
            // Preview coordinates to view coordinates
            if (overlay.cameraFacing == CameraSource.CAMERA_FACING_FRONT)
            {
                return overlay.getWidth() - scaleX(x);
            } else
            {
                return scaleX(x);
            }
        }

        public float translateY(float y)
        {
            // Preview coordinates to view coordinates
            return scaleY(y);
        }

        public void postInvalidate()
        {
            overlay.postInvalidate();
        }
    }

    public GraphicsOverlay(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    public void clear()
    {
        // Clear overlay
        synchronized (lock)
        {
            graphicSet.clear();
        }
        postInvalidate();
    }

    public void add(Graphic graphic)
    {
        synchronized (lock)
        {
            graphicSet.add(graphic);
        }
        postInvalidate();
    }

    public void remove(Graphic graphic)
    {
        synchronized (lock)
        {
            graphicSet.remove(graphic);
        }
        postInvalidate();
    }

    public void setCameraInfo(int previewWidth, int previewHeight, int facing)
    {
        // Init camera attributes
        synchronized (lock)
        {
            this.previewWidth = previewWidth;
            this.previewHeight = previewHeight;
            cameraFacing = facing;
        }
        postInvalidate();
    }

    @Override
    protected void onDraw(Canvas canvas)
    {
        super.onDraw(canvas);

        synchronized (lock)
        {
            if ((previewWidth != 0) && (previewHeight != 0))
            {
                widthScaleFactor = (float) canvas.getWidth() / (float) previewWidth;
                heightScaleFactor = (float) canvas.getHeight() / (float) previewHeight;
            }

            for (Graphic graphic : graphicSet)
            {
                graphic.draw(canvas);
            }
        }
    }
}
