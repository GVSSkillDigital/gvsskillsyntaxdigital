package com.gvdigital.training.controller;

import com.gvdigital.training.model.Course;
import com.gvdigital.training.model.StudentRegistration;
import com.gvdigital.training.service.TrainingService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "http://localhost:4200")
public class TrainingController {
    private final TrainingService trainingService;

    public TrainingController(TrainingService trainingService) {
        this.trainingService = trainingService;
    }

    @GetMapping("/courses")
    public ResponseEntity<List<Course>> getAllCourses() {
        return ResponseEntity.ok(trainingService.getAllCourses());
    }

    @PostMapping("/register")
    public ResponseEntity<StudentRegistration> registerStudent(@RequestBody StudentRegistration registration) {
        return ResponseEntity.ok(trainingService.registerStudent(registration));
    }
}
