package com.joro.driveguard.controller;

import com.joro.driveguard.analytics.LocationCluster;
import com.joro.driveguard.service.DataAnalyticsService;
import org.apache.commons.math3.ml.clustering.Cluster;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.Map;

@Controller
public class DataAnalyticsController
{
    @Autowired
    private DataAnalyticsService dataAnalyticsService;

    @ResponseBody
    @GetMapping("/locationClusters")
    public List<Cluster<LocationCluster>> locationClusters()
    {
        return dataAnalyticsService.locationClusters(2000, 2, 50);
    }

    @ResponseBody
    @GetMapping("/driverFocusLossByHour")
    public Map<Integer, Long> driverFocusLossByHour()
    {
        return dataAnalyticsService.driverFocusLossByHour();
    }
}
