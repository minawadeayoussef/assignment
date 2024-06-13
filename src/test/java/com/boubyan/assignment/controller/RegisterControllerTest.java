package com.boubyan.assignment.controller;

import com.boubyan.assignment.controller.RegisterController;
import com.boubyan.assignment.dto.CourseRegistrationRequest;
import com.boubyan.assignment.entity.User;
import com.boubyan.assignment.service.CourseService;
import com.boubyan.assignment.service.ScheduleService;
import com.boubyan.assignment.service.UserService;
import com.boubyan.assignment.util.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class RegisterControllerTest {

    @Mock
    private ScheduleService scheduleService;

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private UserService userService;

    @Mock
    private CourseService courseService;

    @InjectMocks
    private RegisterController registerController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    void testCancelCourseRegistration() {
        // Prepare test data
        CourseRegistrationRequest request = new CourseRegistrationRequest();
        // Set request properties
        // ...

        // Define mock behavior
        doNothing().when(courseService).cancelRegistration(request);

        // Call the controller method
        ResponseEntity<String> response = registerController.cancelCourseRegistration(request);

        // Verify the response
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Cancelled successfully", response.getBody());
        // Verify that the service method was called once
        verify(courseService, times(1)).cancelRegistration(request);
    }

    @Test
    void testGetCourseSchedule() {
        // Prepare test data
        String authorizationHeader = "Bearer <JWT_TOKEN>";

        // Define mock behavior
        String userId = "user123";
        when(jwtUtil.extractUserId(authorizationHeader)).thenReturn(userId);
        User user = new User();
        // Set user properties
        // ...
        when(userService.getUserByUsername(userId)).thenReturn(user);

        byte[] scheduleData = new byte[0]; // Mock schedule data
        when(scheduleService.generateCourseSchedulePDF(user.getId())).thenReturn(ResponseEntity.ok(scheduleData));

        // Call the controller method
        ResponseEntity<byte[]> response = registerController.getCourseSchedule(authorizationHeader);

        // Verify the response
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(scheduleData, response.getBody());
        // Verify that the service methods were called once
        verify(jwtUtil, times(1)).extractUserId(authorizationHeader);
        verify(userService, times(1)).getUserByUsername(userId);
        verify(scheduleService, times(1)).generateCourseSchedulePDF(user.getId());
    }

    @Test
    void testRegisterCourse() {
        // Prepare test data
        CourseRegistrationRequest request = new CourseRegistrationRequest();
        // Set request properties
        // ...

        // Define mock behavior
        doNothing().when(courseService).registerCourse(request);

        // Call the controller method
        ResponseEntity<String> response = registerController.registerCourse(request);

        // Verify the response
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Registered successfully", response.getBody());
        // Verify that the service method was called once
        verify(courseService, times(1)).registerCourse(request);
    }
}
