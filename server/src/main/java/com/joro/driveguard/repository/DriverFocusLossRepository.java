package com.joro.driveguard.repository;

import com.joro.driveguard.model.DriverFocusLoss;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface DriverFocusLossRepository extends JpaRepository<DriverFocusLoss, Integer>
{
    @Query("SELECT HOUR(dfl.localDateTime) as hour, COUNT(*) as count FROM DriverFocusLoss dfl GROUP BY HOUR(dfl.localDateTime)")
    List<Object[]> driverFocusLossByHour();
}
