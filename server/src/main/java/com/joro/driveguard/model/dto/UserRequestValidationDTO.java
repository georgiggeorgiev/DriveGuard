package com.joro.driveguard.model.dto;

import lombok.AllArgsConstructor;
import lombok.Value;

@Value
@AllArgsConstructor
public class UserRequestValidationDTO
{
    String phoneNumber;
    String APIKey;
}
