package com.boubyan.assignment.controller;


import com.boubyan.assignment.dto.CourseRegistrationRequest;
import com.boubyan.assignment.entity.Course;
import com.boubyan.assignment.entity.User;
import com.boubyan.assignment.service.CourseService;
import com.boubyan.assignment.service.ScheduleService;
import com.boubyan.assignment.service.UserService;
import com.boubyan.assignment.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;
import java.util.logging.Logger;

@RestController
@RequestMapping("/api/v1/courses")
public class CourseController {
    private static final Logger logger = Logger.getLogger(CourseController.class.getName());

    private final CourseService courseService;

    public CourseController(CourseService courseService) {
        this.courseService = courseService;
    }

    @GetMapping
    public List<Course> getAllCourses() {
        try {
            List<Course> courses = courseService.getAllCourses();
            logger.info("Retrieved all courses successfully");
            return courses;
        } catch (Exception e) {
            logger.severe("Failed to retrieve all courses: " + e.getMessage());
            return null;
        }
    }

    @PostMapping
    public ResponseEntity<String> createCourse(@RequestBody Course request) {
        try {
            courseService.createCourse(request);
            logger.info("Course created successfully");
            return ResponseEntity.ok("Course created successfully");
        } catch (Exception e) {
            logger.severe("Failed to create course: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
}