package com.joro.driveguard.service;

import com.joro.driveguard.model.Role;
import com.joro.driveguard.model.User;
import com.joro.driveguard.model.dto.UserCredentialsDTO;
import com.joro.driveguard.model.dto.UserRequestValidationDTO;
import com.joro.driveguard.repository.RoleRepository;
import com.joro.driveguard.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;

@Service
public class UserService
{
    private UserRepository userRepository;
    private RoleRepository roleRepository;
    private BCryptPasswordEncoder passwordEncoder;

    @Autowired
    public UserService(UserRepository userRepository, RoleRepository roleRepository, BCryptPasswordEncoder bCryptPasswordEncoder)
    {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = bCryptPasswordEncoder;
    }

    public User findUserByPhoneNumber(String phoneNumber)
    {
        return userRepository.findByPhoneNumber(phoneNumber);
    }

    public User saveUser(User user)
    {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setActive(true);
        user.setRoles(new HashSet<Role>(roleRepository.findAll())); // TODO
        return userRepository.save(user);
    }

    public User isValidUserCredentialsDTO(UserCredentialsDTO dto)
    {
        User user = findUserByPhoneNumber(dto.getPhoneNumber());
        if (user == null || !passwordEncoder.matches(dto.getPassword(), user.getPassword()))
        {
            return null;
        }
        return user;
    }

    public boolean isValidUserRequestValidationDTO(UserRequestValidationDTO dto)
    {
        if (dto.getAPIKey() == null)
        {
            return false;
        }
        User user = findUserByPhoneNumber(dto.getPhoneNumber());
        if (user != null)
        {
            return dto.getAPIKey().equals(user.getAPIKey());
        }
        return true;
    }
}
