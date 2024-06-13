package com.boubyan.assignment.service;

import com.boubyan.assignment.entity.Course;
import com.boubyan.assignment.entity.Registration;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Paragraph;

import com.itextpdf.text.pdf.PdfWriter;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ScheduleService {

    private final CourseService courseService;
    private final RegistrationService registrationService;

    public ScheduleService(CourseService courseService, UserService userService, RegistrationService registrationService) {
        this.courseService = courseService;
        this.registrationService = registrationService;
    }
    public ResponseEntity<byte[]> generateCourseSchedulePDF(Long userId) {
        try {
            List<Registration> registrations = registrationService.getRegistrationsByUserId(userId);

            // Extract course IDs from registrations
            List<Long> courseIds = registrations.stream()
                    .map(registration -> registration.getCourse().getId())
                    .collect(Collectors.toList());

            // Get courses associated with the registrations
            List<Course> courses = courseService.getCoursesByIds(courseIds);

            // Format schedule content
            String scheduleContent = formatScheduleContent(courses);

            // Generate PDF from content
            byte[] pdfBytes = generatePdfFromContent(scheduleContent);

            // Return ResponseEntity with PDF bytes
            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_PDF)
                    .body(pdfBytes);
        } catch (IOException | DocumentException e) {
            e.printStackTrace(); // Handle error appropriately
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    private String formatScheduleContent(List<Course> courses) {
        StringBuilder scheduleContent = new StringBuilder();
        scheduleContent.append("Course Schedule:\n\n");
        for (Course course : courses) {
            scheduleContent.append("Course Name: ").append(course.getName()).append("\r\n");
            scheduleContent.append("Instructor: ").append(course.getInstructor()).append("\r\n");
            scheduleContent.append("Time: ").append(course.getSchedule()).append("\r\n\r\n");
        }
        return scheduleContent.toString();
    }

    private byte[] generatePdfFromContent(String content) throws IOException, DocumentException {
        try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream()) {
            com.itextpdf.text.Document document = new com.itextpdf.text.Document();
            PdfWriter.getInstance(document, byteArrayOutputStream);
            document.open();

            Paragraph paragraph = new Paragraph(content);
            document.add(paragraph);

            document.close();
            return byteArrayOutputStream.toByteArray();
        }
    }

}