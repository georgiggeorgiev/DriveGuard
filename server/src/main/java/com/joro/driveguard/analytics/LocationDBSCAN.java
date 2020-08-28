package com.joro.driveguard.analytics;

import com.joro.driveguard.model.Location;
import org.apache.commons.math3.ml.clustering.Cluster;
import org.apache.commons.math3.ml.clustering.DBSCANClusterer;
import org.apache.commons.math3.ml.distance.DistanceMeasure;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public final class LocationDBSCAN
{
    private LocationDBSCAN() {}

    private static final int R = 6371; // Radius of the earth in kilometers

    private static double haversineDistance(double latitude1, double latitude2, double longitude1, double longitude2)
    {
        double latDistance = Math.toRadians(latitude2 - latitude1);
        double lonDistance = Math.toRadians(longitude2 - longitude1);

        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(latitude1)) * Math.cos(Math.toRadians(latitude2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return R * c * 1000; // Distance in meters
    }

    public static List<Cluster<LocationCluster>> locationClusters(List<Location> locations, int maxRadiusMeters, int minPoints, int maxClusters)
    {
        List<LocationCluster> clusterInput = new ArrayList<>(locations.size());
        for (Location location : locations)
        {
            clusterInput.add(new LocationCluster(location));
        }

        final DBSCANClusterer<LocationCluster> clusterer = new DBSCANClusterer<>
        (
                maxRadiusMeters,
                minPoints,
                (DistanceMeasure) (doubles, doubles1) -> haversineDistance(doubles[0], doubles1[0], doubles[1], doubles1[1])
        );

        List<Cluster<LocationCluster>> clusters = clusterer.cluster(clusterInput)
                .stream()
                .sorted(Comparator.comparingInt((Cluster<LocationCluster> cluster) -> cluster.getPoints().size()).reversed())
                .limit(maxClusters)
                .collect(Collectors.toList());

        return  clusters;
    }
}
