package com.joro.driveguard.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserSharedPreferencesDTO
{
    private String firstName;
    private String phoneNumber;
    private String APIKey;
}
