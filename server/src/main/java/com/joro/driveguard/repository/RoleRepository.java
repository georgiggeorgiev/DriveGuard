package com.joro.driveguard.repository;

import com.joro.driveguard.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoleRepository extends JpaRepository<Role, Integer>
{
    Role findByRole(String role);
}
