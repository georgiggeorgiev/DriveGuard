package com.joro.driveguard.vision;

import android.graphics.PointF;
import android.os.SystemClock;

public class EyePhysics
{
    private final long TIME_PERIOD_MS = 1000;

    private final float FRICTION = 2.2f;
    private final float GRAVITY = 0.5f;

    private final float BOUNCE_MULTIPLIER = 0.8f;

    private final float ZERO_TOLERANCE = 0.001f;

    private long lastUpdateTimeMs = SystemClock.elapsedRealtime();

    private PointF eyePosition;
    private float eyeRadius;

    private PointF irisPosition;
    private float irisRadius;

    private float velocityX = 0.0f;
    private float velocityY = 0.0f;

    private int consecutiveBounces = 0;

    PointF nextIrisPosition(PointF eyePosition, float eyeRadius, float irisRadius)
    {
        this.eyePosition = eyePosition;
        this.eyeRadius = eyeRadius;

        if (irisPosition == null)
        {
            irisPosition = eyePosition;
        }

        this.irisRadius = irisRadius;

        long nowMs = SystemClock.elapsedRealtime();
        long elapsedTimeMs = nowMs - lastUpdateTimeMs;
        float simulationRate = (float) elapsedTimeMs / TIME_PERIOD_MS;
        lastUpdateTimeMs = nowMs;

        if (!isStopped())
        {
            velocityY += GRAVITY * simulationRate;
        }

        velocityX = applyFriction(velocityX, simulationRate);
        velocityY = applyFriction(velocityY, simulationRate);

        float x = irisPosition.x + (velocityX * this.irisRadius * simulationRate);
        float y = irisPosition.y + (velocityY * this.irisRadius * simulationRate);
        irisPosition = new PointF(x, y);

        makeIrisInBounds(simulationRate);

        return irisPosition;
    }

    private float applyFriction(float velocity, float simulationRate)
    {
        if (isZero(velocity))
        {
            velocity = 0.0f;
        } else if (velocity > 0)
        {
            velocity = Math.max(0.0f, velocity - (FRICTION * simulationRate));
        } else
        {
            velocity = Math.min(0.0f, velocity + (FRICTION * simulationRate));
        }
        return velocity;
    }

    private void makeIrisInBounds(float simulationRate)
    {
        float irisOffsetX = irisPosition.x - eyePosition.x;
        float irisOffsetY = irisPosition.y - eyePosition.y;

        float maxDistance = eyeRadius - irisRadius;
        float distance = (float) Math.sqrt(Math.pow(irisOffsetX, 2) + Math.pow(irisOffsetY, 2));
        if (distance <= maxDistance)
        {
            // In bounds
            consecutiveBounces = 0;
            return;
        }

        consecutiveBounces++;

        float ratio = maxDistance / distance;
        float x = eyePosition.x + (ratio * irisOffsetX);
        float y = eyePosition.y + (ratio * irisOffsetY);

        float dx = x - irisPosition.x;
        velocityX = applyBounce(velocityX, dx, simulationRate) / consecutiveBounces;

        float dy = y - irisPosition.y;
        velocityY = applyBounce(velocityY, dy, simulationRate) / consecutiveBounces;

        irisPosition = new PointF(x, y);
    }

    private float applyBounce(float velocity, float distOutOfBounds, float simulationRate)
    {
        if (isZero(distOutOfBounds))
        {
            return velocity;
        }

        velocity *= -1;

        float bounce = BOUNCE_MULTIPLIER * Math.abs(distOutOfBounds / irisRadius);
        if (velocity > 0)
        {
            velocity += bounce * simulationRate;
        }
        else
        {
            velocity -= bounce * simulationRate;
        }

        return velocity;
    }

    private boolean isStopped()
    {
        if (eyePosition.y >= irisPosition.y)
        {
            return false;
        }

        float irisOffsetY = irisPosition.y - eyePosition.y;
        float maxDistance = eyeRadius - irisRadius;
        if (irisOffsetY < maxDistance)
        {
            return false;
        }

        return (isZero(velocityX) && isZero(velocityY));
    }

    private boolean isZero(float num)
    {
        // Floating point zero tolerance
        return ((num < ZERO_TOLERANCE) && (num > -1 * ZERO_TOLERANCE));
    }
}
