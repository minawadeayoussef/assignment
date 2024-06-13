package com.boubyan.assignment.service;

import com.boubyan.assignment.dto.AuthenticationRequest;
import com.boubyan.assignment.dto.LoginRequest;
import com.boubyan.assignment.entity.User;
import com.boubyan.assignment.repository.UserRepository;
import com.boubyan.assignment.util.JwtUtil;
import com.boubyan.assignment.exception.UsernameAlreadyTakenException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
public class UserService {

    private final UserRepository userRepository;


    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    @Autowired
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    public User saveUser(User user) {
        // Encode the password before saving
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }

    public User getUserByUsername(String username) {
        return userRepository.findByUsername(username).orElse(null);
    }

    public String login(AuthenticationRequest authenticationRequest) {
        // Retrieve user from the database by username
        User user = userRepository.findByUsername(authenticationRequest.getUsername()).orElse(null);

        // Check if user exists and password matches
        if (user != null && passwordEncoder.matches(authenticationRequest.getPassword(), user.getPassword())) {
            // Authentication successful, generate and return JWT token
            UserDetails userDetails = new org.springframework.security.core.userdetails.User(
                    user.getUsername(),
                    user.getPassword(),
                    Collections.emptyList()
            );
            return jwtUtil.generateToken(userDetails);
        }

        return null; // Authentication failed
    }


    public User createUser(LoginRequest userRequest) {
        // Perform validation if necessary

            if (userRepository.existsByUsername(userRequest.getUsername())) {
                throw new UsernameAlreadyTakenException("Username is already taken");
            }
        // Create a new user entity
        User user = new User();
        user.setUsername(userRequest.getUsername());
        user.setPassword(passwordEncoder.encode(userRequest.getPassword()));
        // Set other properties as needed

        // Save the user entity
        return userRepository.save(user);
    }
}