package com.boubyan.assignment.service;

import com.boubyan.assignment.constants.RegistrationStatus;
import com.boubyan.assignment.dto.CourseRegistrationRequest;
import com.boubyan.assignment.entity.Course;
import com.boubyan.assignment.entity.Registration;
import com.boubyan.assignment.entity.User;
import com.boubyan.assignment.repository.CourseRepository;
import com.boubyan.assignment.repository.RegistrationRepository;
import com.boubyan.assignment.repository.UserRepository;
import com.boubyan.assignment.exception.CourseNotFoundException;
import com.boubyan.assignment.exception.RegistrationNotFoundException;
import com.boubyan.assignment.exception.UserNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.transaction.Transactional;
import java.util.List;

@Service
public class CourseService {

    private final CourseRepository courseRepository;
    private final EntityManager entityManager;
    private final UserRepository userRepository;
    private final RegistrationRepository registrationRepository;

    @Autowired
    public CourseService(CourseRepository courseRepository, EntityManager entityManager, UserRepository userRepository, RegistrationRepository registrationRepository) {
        this.courseRepository = courseRepository;
        this.entityManager = entityManager;
        this.userRepository = userRepository;
        this.registrationRepository = registrationRepository;
    }
    public List<Course> getCoursesByIds(List<Long> courseIds) {
        return courseRepository.findAllById(courseIds);
    }
    @Cacheable("courses")
    public List<Course> getAllCourses() {
        return courseRepository.findAll();
    }

    @CacheEvict(value = "courses", allEntries = true)
    @Transactional
    public void createCourse(Course request) {
        courseRepository.save(request);
    }

    @CacheEvict(value = "courses", allEntries = true)
    @Transactional
    public void cancelRegistration(CourseRegistrationRequest request) {
        try {
            Registration registration = entityManager.createQuery(
                            "SELECT r FROM Registration r WHERE r.course.id = :courseId AND r.user.id = :userId", Registration.class)
                    .setParameter("courseId", request.getCourseId())
                    .setParameter("userId", request.getUserId())
                    .getSingleResult();

            registration.setStatus(RegistrationStatus.CANCELLED);
            entityManager.merge(registration);
        } catch (NoResultException ex) {
            throw new RegistrationNotFoundException("Registration not found");
        }
    }

    public void registerCourse(CourseRegistrationRequest request) {
        // Retrieve user and course entities from repositories
        User user = userRepository.findById(request.getUserId()).orElseThrow(() -> new UserNotFoundException("User not found"));
        Course course = courseRepository.findById(request.getCourseId()).orElseThrow(() -> new CourseNotFoundException("Course not found"));

        // Create a new registration entity
        Registration registration = new Registration();
        registration.setUser(user);
        registration.setCourse(course);
        registration.setStatus(RegistrationStatus.ACTIVE); // Assuming ACTIVE is the default status

        // Save the registration entity
        registrationRepository.save(registration);
    }

}
