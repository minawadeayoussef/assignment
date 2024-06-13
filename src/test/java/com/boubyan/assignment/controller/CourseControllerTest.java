package com.boubyan.assignment.controller;

import com.boubyan.assignment.entity.Course;
import com.boubyan.assignment.service.CourseService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CourseControllerTest {

    @Mock
    private CourseService courseService;

    @InjectMocks
    private CourseController courseController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    void testGetAllCourses() {
        // Prepare test data
        List<Course> courses = new ArrayList<>();
        // Add some courses to the list
        // ...

        // Define mock behavior
        when(courseService.getAllCourses()).thenReturn(courses);

        // Call the controller method
        List<Course> result = courseController.getAllCourses();

        // Verify the result
        assertEquals(courses, result);
        // Verify that the service method was called once
        verify(courseService, times(1)).getAllCourses();
    }

    @Test
    void testCreateCourse() {
        // Prepare test data
        Course course = new Course();
        // Set course properties
        // ...

        // Define mock behavior
        // You may define behavior for the service method if needed

        // Call the controller method
        ResponseEntity<String> response = courseController.createCourse(course);

        // Verify the response
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Registered successfully", response.getBody());
        // Verify that the service method was called once
        verify(courseService, times(1)).createCourse(course);
    }
}