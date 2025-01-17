package com.joro.driveguard.controller;

import com.joro.driveguard.model.dto.DriverFocusLossDTO;
import com.joro.driveguard.model.dto.DriverFocusLossValidationDTO;
import com.joro.driveguard.model.dto.UserRequestValidationDTO;
import com.joro.driveguard.service.DriverFocusLossService;

import com.joro.driveguard.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@Controller
public class EmployeeTrackerController
{
    @Autowired
    private SimpMessagingTemplate smt;

    @Autowired
    private DriverFocusLossService driverFocusLossService;

    @Autowired
    private UserService userService;

    private void broadcastUpdate(DriverFocusLossDTO dto)
    {
        smt.convertAndSend("/topic/tracker", dto);
    }

    @PostMapping("/tracker")
    public ResponseEntity tracker(@RequestBody DriverFocusLossValidationDTO dto)
    {
        if (!userService.isValidUserRequestValidationDTO(new UserRequestValidationDTO(dto.getUserPhoneNumber(), dto.getAPIKey())))
        {
            return new ResponseEntity(HttpStatus.BAD_REQUEST);
        }
        if (driverFocusLossService.saveDriverFocusLossFromDTO(dto) != null)
        {
            broadcastUpdate(dto);
            return new ResponseEntity(HttpStatus.ACCEPTED);
        }
        else
        {
            return new ResponseEntity(HttpStatus.BAD_REQUEST);
        }
    }
}
