package com.joro.driveguard.repository;

import com.joro.driveguard.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Integer>
{
    User findByPhoneNumber(String phoneNumber);
}
