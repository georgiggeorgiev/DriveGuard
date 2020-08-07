package com.joro.driveguard.controller;

import com.joro.driveguard.model.DriverFocusLoss;
import com.joro.driveguard.model.Location;
import com.joro.driveguard.model.dto.DtoFactory;
import com.joro.driveguard.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import java.time.LocalDateTime;

@Controller
public class EmployeeTrackerController
{
    @Autowired
    private SimpMessagingTemplate smt;

    @Autowired
    private UserService userService;

    private void broadcastUpdate(String message)
    {
        // TODO Testing
        DriverFocusLoss driverFocusLoss = DriverFocusLoss.builder()
                .id(1)
                .localDateTime(LocalDateTime.now())
                .location(Location.builder().id(1).latitude(10.0).longitude(20.0).build())
                .user(userService.findUserByPhoneNumber("0882595206"))
                .build();

        smt.convertAndSend("/topic/tracker", DtoFactory.fromDriverFocusLoss(driverFocusLoss));
    }

    @GetMapping("/tracker")
    private ResponseEntity apiTest()
    {
        // TODO curl -v localhost:8080/tracker
        broadcastUpdate("TEST");
        return new ResponseEntity(HttpStatus.ACCEPTED);
    }
}
