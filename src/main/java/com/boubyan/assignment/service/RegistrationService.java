package com.boubyan.assignment.service;

import com.boubyan.assignment.entity.Registration;
import com.boubyan.assignment.repository.RegistrationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RegistrationService {

    private final RegistrationRepository registrationRepository;

    @Autowired
    public RegistrationService(RegistrationRepository registrationRepository) {
        this.registrationRepository = registrationRepository;
    }

    public List<Registration> getRegistrationsByUserId(Long userId) {
        return registrationRepository.findByUserId(userId);
    }

}