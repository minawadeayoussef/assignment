package com.boubyan.assignment.repository;

import com.boubyan.assignment.entity.Course;
import com.boubyan.assignment.entity.Registration;
import com.boubyan.assignment.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository <User,Long> {
    Optional<User> findByUsername(String username);

    boolean existsByUsername(String username);



}
