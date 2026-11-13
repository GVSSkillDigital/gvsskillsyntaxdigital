package com.gvdigital.training.service;

import com.gvdigital.training.model.Course;
import com.gvdigital.training.model.StudentRegistration;
import com.gvdigital.training.repository.CourseRepository;
import com.gvdigital.training.repository.StudentRegistrationRepository;
import org.springframework.stereotype.Service;
import jakarta.annotation.PostConstruct;
import java.util.List;

@Service
public class TrainingService {
    private final CourseRepository courseRepository;
    private final StudentRegistrationRepository registrationRepository;

    public TrainingService(CourseRepository courseRepository, StudentRegistrationRepository registrationRepository) {
        this.courseRepository = courseRepository;
        this.registrationRepository = registrationRepository;
    }

    @PostConstruct
    public void initData() {
        if (courseRepository.count() == 0) {
            courseRepository.save(new Course("JAVA-01", "Enterprise Java 8 & Spring Boot", "Backend", "16 Weeks", "Online / Classroom", "$750", "Master Java 8 Streams, Lambda, Spring Security, and Microservices."));
            courseRepository.save(new Course("DOTNET-01", ".NET Core & C# Microservices", "Backend", "14 Weeks", "Online / Classroom", "$700", "Build enterprise REST APIs and Entity Framework Core."));
            courseRepository.save(new Course("AZURE-01", "DevOps & Azure Cloud Architect", "Cloud & DevOps", "18 Weeks", "Online", "$850", "Master CI/CD pipelines, Docker, Kubernetes, and Azure."));
            courseRepository.save(new Course("ANGULAR-01", "Modern Angular & TypeScript", "Frontend", "16 Weeks", "Online / Classroom", "$720", "Comprehensive frontend course covering components, services, and RxJS."));
            courseRepository.save(new Course("PYTHON-01", "Python Data Engineering & SQL", "Data & DB", "12 Weeks", "Online / Classroom", "$650", "Learn Python, Advanced SQL, and ETL pipeline automation."));
            courseRepository.save(new Course("QA-01", "Full-Stack Software Testing", "Quality Assurance", "12 Weeks", "Online / Classroom", "$580", "Manual test strategies and Selenium automation with Java."));
        }
    }

    public List<Course> getAllCourses() {
        return courseRepository.findAll();
    }

    public StudentRegistration registerStudent(StudentRegistration registration) {
        return registrationRepository.save(registration);
    }
}
