package com.joro.driveguard.service;

import com.joro.driveguard.model.DriverFocusLoss;
import com.joro.driveguard.model.Location;
import com.joro.driveguard.model.User;
import com.joro.driveguard.model.dto.DriverFocusLossDTO;
import com.joro.driveguard.repository.DriverFocusLossRepository;
import com.joro.driveguard.repository.LocationRepository;
import com.joro.driveguard.repository.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DriverFocusLossService
{
    private DriverFocusLossRepository driverFocusLossRepository;
    private UserRepository userRepository;
    private LocationRepository locationRepository;

    @Autowired
    public DriverFocusLossService(DriverFocusLossRepository driverFocusLossRepository, UserRepository userRepository, LocationRepository locationRepository)
    {
        this.driverFocusLossRepository = driverFocusLossRepository;
        this.userRepository = userRepository;
        this.locationRepository = locationRepository;
    }

    public DriverFocusLoss saveDriverFocusLoss(DriverFocusLoss driverFocusLoss)
    {
        return driverFocusLossRepository.save(driverFocusLoss);
    }

    public Location saveLocation(Location location)
    {
        return locationRepository.save(location);
    }

    public DriverFocusLoss saveDriverFocusLossFromDTO(DriverFocusLossDTO dto)
    {
        User user = userRepository.findByPhoneNumber(dto.getUserPhoneNumber());
        if (user == null)
        {
            return null;
        }

        Location location = Location.builder()
                .latitude(dto.getLatitude())
                .longitude(dto.getLongitude())
                .build();

        DriverFocusLoss driverFocusLoss = DriverFocusLoss.builder()
                .localDateTime(dto.getLocalDateTime())
                .location(location)
                .user(user)
                .build();

        saveLocation(location);
        return saveDriverFocusLoss(driverFocusLoss);
    }
}
