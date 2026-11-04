package com.gvdigital.training.model;

import javax.persistence.*;

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

    public Course() {}

    public Course(String code, String title, String category, String duration, String mode, String fee, String description) {
        this.code = code;
        this.title = title;
        this.category = category;
        this.duration = duration;
        this.mode = mode;
        this.fee = fee;
        this.description = description;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    public String getDuration() { return duration; }
    public void setDuration(String duration) { this.duration = duration; }
    public String getMode() { return mode; }
    public void setMode(String mode) { this.mode = mode; }
    public String getFee() { return fee; }
    public void setFee(String fee) { this.fee = fee; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
}
