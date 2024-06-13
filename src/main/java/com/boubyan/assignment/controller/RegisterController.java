package com.boubyan.assignment.controller;


import com.boubyan.assignment.dto.CourseRegistrationRequest;
import com.boubyan.assignment.entity.User;
import com.boubyan.assignment.service.CourseService;
import com.boubyan.assignment.service.ScheduleService;
import com.boubyan.assignment.service.UserService;
import com.boubyan.assignment.util.JwtUtil;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.*;

import java.util.logging.Logger;

@RestController
@RequestMapping("/api/v1/register")
public class RegisterController {
    private static final Logger logger = Logger.getLogger(RegisterController.class.getName());

    private final ScheduleService scheduleService;
    private final JwtUtil jwtUtil;
    private final UserService userService;
    private final CourseService courseService;

    public RegisterController(ScheduleService scheduleService, JwtUtil jwtUtil, UserService userService, CourseService courseService) {
        this.scheduleService = scheduleService;
        this.jwtUtil = jwtUtil;
        this.courseService = courseService;
        this.userService = userService;
    }

    @PostMapping("/cancel")
    public ResponseEntity<String> cancelCourseRegistration(@RequestBody CourseRegistrationRequest request) {
        try {
            courseService.cancelRegistration(request);
            logger.info("Cancellation successful");
            return ResponseEntity.ok("Cancelled successfully");
        } catch (Exception e) {
            logger.severe("Cancellation failed: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @GetMapping("/schedule")
    public ResponseEntity<byte[]> getCourseSchedule(@RequestHeader("Authorization") String authorizationHeader) {
        try {
            String userName = jwtUtil.extractUserId(authorizationHeader); // Extract user ID from JWT token
            User user =  userService.getUserByUsername(userName);
            logger.info("Schedule retrieved successfully for user: " + user.getUsername());
            return scheduleService.generateCourseSchedulePDF(user.getId());
        } catch (Exception e) {
            logger.severe("Failed to retrieve schedule: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @PostMapping
    public ResponseEntity<String> registerCourse(@RequestBody CourseRegistrationRequest request) {
        try {
            courseService.registerCourse(request);
            logger.info("Registration successful");
            return ResponseEntity.ok("Registered successfully");
        } catch (Exception e) {
            logger.severe("Registration failed: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
}