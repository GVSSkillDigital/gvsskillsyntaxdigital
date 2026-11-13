package com.gvdigital.training.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "courses")
public class Course {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String code;
    private String title;
    private String category;
    private String duration;
    private String mode;
    private String fee;
    @Column(length = 1000)
    private String description;

    public Course(String code, String title, String category, String duration, String mode, String fee, String description) {
        this.code = code;
        this.title = title;
        this.category = category;
        this.duration = duration;
        this.mode = mode;
        this.fee = fee;
        this.description = description;
    }

}
