package com.joro.driveguard.service;

import com.joro.driveguard.analytics.LocationDBSCAN;
import com.joro.driveguard.analytics.LocationCluster;
import com.joro.driveguard.model.Location;
import com.joro.driveguard.repository.DriverFocusLossRepository;
import com.joro.driveguard.repository.LocationRepository;
import org.apache.commons.math3.ml.clustering.Cluster;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class DataAnalyticsService
{
    private DriverFocusLossRepository driverFocusLossRepository;
    private LocationRepository locationRepository;

    @Autowired
    public DataAnalyticsService(DriverFocusLossRepository driverFocusLossRepository, LocationRepository locationRepository)
    {
        this.driverFocusLossRepository = driverFocusLossRepository;
        this.locationRepository = locationRepository;
    }

    public List<Cluster<LocationCluster>> locationClusters(int maxRadiusMeters, int minPoints, int maxClusters)
    {
        List<Location> locations = locationRepository.findAll();
        return LocationDBSCAN.locationClusters(locations, maxRadiusMeters, minPoints, maxClusters);
    }

    public Map<Integer, Long> driverFocusLossByHour()
    {
        Map<Integer, Long> result = new HashMap<>(24);
        final List<Object[]> queryResult = driverFocusLossRepository.driverFocusLossByHour();

        for (Object[] objects : queryResult)
        {
            result.put((int) objects[0], (long) objects[1]);
        }

        // Add missing hours
        for (int i = 0; i < 24L; i++)
        {
            if (!result.containsKey(i))
            {
                result.put(i, 0L);
            }
        }

        return result;
    }
}
