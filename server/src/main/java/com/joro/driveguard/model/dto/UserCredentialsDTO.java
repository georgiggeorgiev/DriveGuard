package com.joro.driveguard.model.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class UserCredentialsDTO
{
    private String phoneNumber;
    private String password;
}
