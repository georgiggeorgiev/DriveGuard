package com.joro.driveguard.vision;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;

class EyeGraphics extends GraphicsOverlay.Graphic
{
    private static final float EYE_RADIUS_PROPORTION = 0.45f;
    private static final float IRIS_RADIUS_PROPORTION = EYE_RADIUS_PROPORTION / 2.0f;

    private Paint eyeWhitesPaint;
    private Paint eyeIrisPaint;
    private Paint eyeOutlinePaint;
    private Paint eyelidPaint;

    private EyePhysics leftEyePhysics = new EyePhysics();
    private EyePhysics rightEyePhysics = new EyePhysics();

    private volatile PointF leftEyePosition;
    private volatile boolean isLeftEyeOpen;

    private volatile PointF rightEyePosition;
    private volatile boolean isRightEyeOpen;

    public EyeGraphics(GraphicsOverlay overlay)
    {
        super(overlay);

        eyeWhitesPaint = new Paint();
        eyeWhitesPaint.setColor(Color.TRANSPARENT);
        eyeWhitesPaint.setStyle(Paint.Style.FILL);

        eyelidPaint = new Paint();
        eyelidPaint.setColor(Color.YELLOW);
        eyelidPaint.setStyle(Paint.Style.FILL);

        eyeIrisPaint = new Paint();
        eyeIrisPaint.setColor(Color.RED);
        eyeIrisPaint.setStyle(Paint.Style.STROKE);
        eyeIrisPaint.setStrokeWidth(2);

        eyeOutlinePaint = new Paint();
        eyeOutlinePaint.setColor(Color.BLACK);
        eyeOutlinePaint.setStyle(Paint.Style.STROKE);
        eyeOutlinePaint.setStrokeWidth(3);
    }

    void updateEyes(PointF leftPosition, boolean leftOpen, PointF rightPosition, boolean rightOpen)
    {
        this.leftEyePosition = leftPosition;
        isLeftEyeOpen = leftOpen;

        rightEyePosition = rightPosition;
        isRightEyeOpen = rightOpen;

        postInvalidate();
    }

    @Override
    public void draw(Canvas canvas)
    {
        PointF detectLeftPosition = leftEyePosition;
        PointF detectRightPosition = rightEyePosition;
        if ((detectLeftPosition == null) || (detectRightPosition == null))
        {
            return;
        }

        PointF leftPosition = new PointF(translateX(detectLeftPosition.x), translateY(detectLeftPosition.y));
        PointF rightPosition = new PointF(translateX(detectRightPosition.x), translateY(detectRightPosition.y));

        float distance = (float) Math.sqrt(Math.pow(rightPosition.x - leftPosition.x, 2) + Math.pow(rightPosition.y - leftPosition.y, 2));
        float eyeRadius = EYE_RADIUS_PROPORTION * distance;
        float irisRadius = IRIS_RADIUS_PROPORTION * distance;

        PointF leftIrisPosition = leftEyePhysics.nextIrisPosition(leftPosition, eyeRadius, irisRadius);
        drawEye(canvas, leftPosition, eyeRadius, leftIrisPosition, irisRadius, isLeftEyeOpen);

        PointF rightIrisPosition = rightEyePhysics.nextIrisPosition(rightPosition, eyeRadius, irisRadius);
        drawEye(canvas, rightPosition, eyeRadius, rightIrisPosition, irisRadius, isRightEyeOpen);
    }

    private void drawEye(Canvas canvas, PointF eyePosition, float eyeRadius, PointF irisPosition, float irisRadius, boolean isOpen)
    {
        if (isOpen)
        {
            canvas.drawCircle(eyePosition.x, eyePosition.y, eyeRadius, eyeWhitesPaint);
            canvas.drawCircle(irisPosition.x, irisPosition.y, irisRadius, eyeIrisPaint);
        }
        else
        {
            canvas.drawCircle(eyePosition.x, eyePosition.y, eyeRadius, eyelidPaint);
            float y = eyePosition.y;
            float start = eyePosition.x - eyeRadius;
            float end = eyePosition.x + eyeRadius;
            canvas.drawLine(start, y, end, y, eyeOutlinePaint);
        }
        canvas.drawCircle(eyePosition.x, eyePosition.y, eyeRadius, eyeOutlinePaint);
    }
}
