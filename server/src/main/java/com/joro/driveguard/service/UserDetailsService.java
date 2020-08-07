package com.joro.driveguard.service;

import com.joro.driveguard.model.Role;
import com.joro.driveguard.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class UserDetailsService implements org.springframework.security.core.userdetails.UserDetailsService
{
    @Autowired
    private UserService userService;

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String phoneNumber)
    {
        User user = userService.findUserByPhoneNumber(phoneNumber);
        List<GrantedAuthority> authorities = getUserAuthority(user.getRoles());
        return buildUserForAuthentication(user, authorities);
    }

    private List<GrantedAuthority> getUserAuthority(Set<Role> userRoles)
    {
        Set<GrantedAuthority> roles = new HashSet<>();
        for (Role role : userRoles)
        {
            roles.add(new SimpleGrantedAuthority(role.getRole()));
        }
        return new ArrayList<>(roles);
    }

    private UserDetails buildUserForAuthentication(User user, List<GrantedAuthority> authorities)
    {
        return new org.springframework.security.core.userdetails.User(user.getPhoneNumber(), user.getPassword(),
                user.getActive(), true, true, true, authorities);
    }
}
