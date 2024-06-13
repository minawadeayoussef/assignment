package com.boubyan.assignment.controller;

import com.boubyan.assignment.dto.AuthenticationRequest;
import com.boubyan.assignment.dto.AuthenticationResponse;
import com.boubyan.assignment.dto.LoginRequest;
import com.boubyan.assignment.entity.User;
import com.boubyan.assignment.service.UserService;
import com.boubyan.assignment.util.JwtUtil;
import com.boubyan.assignment.exception.UnauthorizedException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Collections;
import java.util.Date;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class UserControllerTest {

    @Mock
    private UserService userService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtUtil jwtUtil;

    @InjectMocks
    private UserController userController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    void testLogin_Success() {
        // Prepare test data
        AuthenticationRequest authenticationRequest = new AuthenticationRequest("username", "password");
        User user = new User();
        user.setUsername("username");
        user.setPassword("encodedPassword"); // Password already encoded
        UserDetails userDetails = new org.springframework.security.core.userdetails.User(
                user.getUsername(),
                user.getPassword(),
                Collections.emptyList()
        );
        String token = "generatedToken";
        Date expirationDate = new Date();

        // Define mock behavior
        when(userService.getUserByUsername(authenticationRequest.getUsername())).thenReturn(user);
        when(passwordEncoder.matches(authenticationRequest.getPassword(), user.getPassword())).thenReturn(true);
        when(jwtUtil.generateToken(userDetails)).thenReturn(token);
        when(jwtUtil.extractExpiration(token)).thenReturn(expirationDate);

        // Call the controller method
        ResponseEntity<?> response = userController.login(authenticationRequest);

        // Verify the response
        assertEquals(HttpStatus.OK, response.getStatusCode());
        AuthenticationResponse responseBody = (AuthenticationResponse) response.getBody();
        assertEquals(token, responseBody.getToken());
        assertEquals(expirationDate, responseBody.getExpirationDate());
    }

    @Test
    void testLogin_Failure() {
        // Prepare test data
        AuthenticationRequest authenticationRequest = new AuthenticationRequest("username", "password");

        // Define mock behavior
        when(userService.getUserByUsername(authenticationRequest.getUsername())).thenReturn(null);

        // Call the controller method and verify UnauthorizedException is thrown
        try {
            userController.login(authenticationRequest);
        } catch (UnauthorizedException e) {
            assertEquals("Invalid credentials", e.getMessage());
        }
    }

    @Test
    void testRegisterUser_Success() {
        // Prepare test data
        LoginRequest userRegistrationRequest = new LoginRequest("username", "password");
        User newUser = new User();
        newUser.setUsername("username");
        newUser.setPassword("encodedPassword"); // Password already encoded
        UserDetails userDetails = new org.springframework.security.core.userdetails.User(
                newUser.getUsername(),
                newUser.getPassword(),
                Collections.emptyList()
        );
        String token = "generatedToken";

        // Define mock behavior
        when(userService.createUser(userRegistrationRequest)).thenReturn(newUser);
        when(jwtUtil.generateToken(userDetails)).thenReturn(token);

        // Call the controller method
        ResponseEntity<?> response = userController.registerUser(userRegistrationRequest);

        // Verify the response
        assertEquals(HttpStatus.OK, response.getStatusCode());
        Map<String, Object> responseBody = (Map<String, Object>) response.getBody();
        assertEquals(token, responseBody.get("token"));
        assertEquals(newUser, responseBody.get("user"));
    }

}

