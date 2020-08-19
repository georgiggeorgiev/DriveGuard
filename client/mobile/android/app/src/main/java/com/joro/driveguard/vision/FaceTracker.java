package com.joro.driveguard.vision;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.PointF;

import com.google.android.gms.vision.Tracker;
import com.google.android.gms.vision.face.Face;
import com.google.android.gms.vision.face.FaceDetector;
import com.google.android.gms.vision.face.Landmark;
import com.joro.driveguard.WarningController;

import java.util.HashMap;
import java.util.Map;

public class FaceTracker extends Tracker<Face>
{
    private static final float EYE_CLOSED_THRESHOLD = 0.5f;

    private GraphicsOverlay overlay;
    private EyeGraphics eyeGraphics;
    private WarningController warningController;

    // Record the previously seen proportions of the landmark locations relative to the bounding box
    // of the face.  These proportions can be used to approximate where the landmarks are within the
    // face bounding box if the eye landmark is missing in a future update.
    @SuppressLint("UseSparseArrays")
    private Map<Integer, PointF> previousProportionsMap = new HashMap<>();

    // Similarly, keep track of the previous eye open state so that it can be reused for
    // intermediate frames which lack eye landmarks and corresponding eye state.
    private boolean wasLeftOpen = true;
    private boolean wasRightOpen = true;

    public FaceTracker(Context context, GraphicsOverlay overlay)
    {
        this.overlay = overlay;
        warningController = new WarningController(context);
    }

    @Override
    public void onNewItem(int id, Face face)
    {
        // Reset
        eyeGraphics = new EyeGraphics(overlay);
    }

    @Override
    public void onUpdate(FaceDetector.Detections<Face> detectionResults, Face face)
    {
        overlay.add(eyeGraphics);

        updatePreviousProportions(face);

        PointF leftPosition = getLandmarkPosition(face, Landmark.LEFT_EYE);
        PointF rightPosition = getLandmarkPosition(face, Landmark.RIGHT_EYE);

        float leftOpenScore = face.getIsLeftEyeOpenProbability();
        boolean isLeftOpen;
        if (leftOpenScore == Face.UNCOMPUTED_PROBABILITY)
        {
            isLeftOpen = wasLeftOpen;
        }
        else
        {
            isLeftOpen = (leftOpenScore > EYE_CLOSED_THRESHOLD);
            wasLeftOpen = isLeftOpen;
        }

        float rightOpenScore = face.getIsRightEyeOpenProbability();
        boolean isRightOpen;
        if (rightOpenScore == Face.UNCOMPUTED_PROBABILITY)
        {
            isRightOpen = wasRightOpen;
        }
        else
        {
            isRightOpen = (rightOpenScore > EYE_CLOSED_THRESHOLD);
            wasRightOpen = isRightOpen;
        }

        eyeGraphics.updateEyes(leftPosition, isLeftOpen, rightPosition, isRightOpen);
        warningController.update(!(isLeftOpen && isRightOpen));
    }

    @Override
    public void onMissing(FaceDetector.Detections<Face> detectionResults)
    {
        // Hide on missing face from frame
        overlay.remove(eyeGraphics);
    }

    @Override
    public void onDone()
    {
        // Hide on permanently missing face
        overlay.remove(eyeGraphics);
    }

    private void updatePreviousProportions(Face face)
    {
        for (Landmark landmark : face.getLandmarks())
        {
            PointF position = landmark.getPosition();
            float xProp = (position.x - face.getPosition().x) / face.getWidth();
            float yProp = (position.y - face.getPosition().y) / face.getHeight();
            previousProportionsMap.put(landmark.getType(), new PointF(xProp, yProp));
        }
    }

    private PointF getLandmarkPosition(Face face, int landmarkId)
    {
        for (Landmark landmark : face.getLandmarks())
        {
            if (landmark.getType() == landmarkId)
            {
                return landmark.getPosition();
            }
        }

        PointF prop = previousProportionsMap.get(landmarkId);
        if (prop == null)
        {
            return null;
        }

        float x = face.getPosition().x + (prop.x * face.getWidth());
        float y = face.getPosition().y + (prop.y * face.getHeight());
        return new PointF(x, y);
    }
}
