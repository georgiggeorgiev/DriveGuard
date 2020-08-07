package com.joro.driveguard.model.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class DriverFocusLossDTO
{
    private LocalDateTime localDateTime;
    private double latitude;
    private double longitude;
    private String userPhoneNumber;
    private String userFirstName;
}
