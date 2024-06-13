package com.boubyan.assignment.controller;

import com.boubyan.assignment.dto.AuthenticationRequest;
import com.boubyan.assignment.dto.AuthenticationResponse;
import com.boubyan.assignment.dto.LoginRequest;
import com.boubyan.assignment.entity.User;
import com.boubyan.assignment.service.UserService;
import com.boubyan.assignment.util.JwtUtil;
import com.boubyan.assignment.exception.UnauthorizedException;
import com.boubyan.assignment.exception.UsernameAlreadyTakenException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

@RestController
@RequestMapping("/api/v1/auth")
public class UserController {
    private static final Logger logger = Logger.getLogger(UserController.class.getName());

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    @Autowired
    public UserController(UserService userService, PasswordEncoder passwordEncoder, JwtUtil jwtUtil) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody AuthenticationRequest authenticationRequest) {
        try {
            // Retrieve user from the database by username
            User user = userService.getUserByUsername(authenticationRequest.getUsername());

            // Check if user exists and password matches
            if (user != null && passwordEncoder.matches(authenticationRequest.getPassword(), user.getPassword())) {
                // Authentication successful, generate and return JWT token
                UserDetails userDetails = new org.springframework.security.core.userdetails.User(
                        user.getUsername(),
                        user.getPassword(),
                        Collections.emptyList()
                );
                String token = jwtUtil.generateToken(userDetails);
                Date expirationDate = jwtUtil.extractExpiration(token);
                AuthenticationResponse response = new AuthenticationResponse(token, expirationDate);

                logger.info("Login successful for user: " + user.getUsername());
                return ResponseEntity.ok(response);
            } else {
                logger.warning("Login failed: Invalid credentials for username: " + authenticationRequest.getUsername());
                throw new UnauthorizedException("Invalid credentials");
            }
        } catch (Exception e) {
            logger.severe("Login failed: " + e.getMessage());
            throw e;
        }
    }

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody LoginRequest userRegistrationRequest) {
        try {
            User newUser = userService.createUser(userRegistrationRequest);

            UserDetails userDetails = new org.springframework.security.core.userdetails.User(newUser.getUsername(), newUser.getPassword(), Collections.emptyList());
            String token = jwtUtil.generateToken(userDetails);

            // Optionally, you can return the token along with any other data
            Map<String, Object> responseBody = new HashMap<>();
            responseBody.put("token", token);
            responseBody.put("user", newUser);

            logger.info("User registered successfully: " + newUser.getUsername());
            return ResponseEntity.ok(responseBody);
        } catch (UsernameAlreadyTakenException e) {
            logger.warning("User registration failed: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Username is already taken");
        } catch (Exception e) {
            logger.severe("User registration failed: " + e.getMessage());
            throw e;
        }
    }
}