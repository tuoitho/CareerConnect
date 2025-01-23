package com.careerconnect.service.impl;

import com.careerconnect.entity.Role;
import com.careerconnect.repository.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RoleService {

    @Autowired
    private RoleRepository roleRepository;

//    public Role mapRole(String userType) {
//        return roleRepository.findByRoleName(userType)
//                .orElseGet(() -> {
//                    Role role = new Role();
//                    role.setRoleName(userType);
//                    return roleRepository.save(role);
//                });
//    }
}