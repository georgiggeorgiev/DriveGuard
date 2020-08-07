package com.joro.driveguard.model.dto;

import com.joro.driveguard.model.DriverFocusLoss;

public final class DtoFactory
{
    private DtoFactory() {}

    public static DriverFocusLossDTO fromDriverFocusLoss(DriverFocusLoss driverFocusLoss)
    {
        return new DriverFocusLossDTO(driverFocusLoss);
    }
}
