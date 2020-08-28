package com.joro.driveguard.analytics;

import com.joro.driveguard.model.Location;
import org.apache.commons.math3.ml.clustering.Clusterable;

public final class LocationCluster implements Clusterable
{
    private final double[] points;

    LocationCluster(Location location)
    {
        this.points = new double[] { location.getLatitude(), location.getLongitude() };
    }

    public double[] getPoint()
    {
        return points;
    }
}
