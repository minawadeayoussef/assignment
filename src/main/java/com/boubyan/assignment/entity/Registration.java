package com.boubyan.assignment.entity;

import com.boubyan.assignment.constants.RegistrationStatus;
import lombok.Data;

import javax.persistence.*;

@Entity
@Data
public class Registration
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    private User user;
    @ManyToOne
    private Course course;
    private String status = RegistrationStatus.ACTIVE;


}
