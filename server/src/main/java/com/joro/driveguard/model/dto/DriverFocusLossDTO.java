package com.joro.driveguard.model.dto;

import com.joro.driveguard.model.DriverFocusLoss;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class DriverFocusLossDTO
{
    private final LocalDateTime localDateTime;
    private final double latitude;
    private final double longitude;
    private final String userPhoneNumber;
    private final String userFirstName;

    DriverFocusLossDTO(DriverFocusLoss driverFocusLoss)
    {
        localDateTime = driverFocusLoss.getLocalDateTime();
        latitude = driverFocusLoss.getLocation().getLatitude();
        longitude = driverFocusLoss.getLocation().getLongitude();
        userPhoneNumber = driverFocusLoss.getUser().getPhoneNumber();
        userFirstName = driverFocusLoss.getUser().getFirstName();
    }
}
