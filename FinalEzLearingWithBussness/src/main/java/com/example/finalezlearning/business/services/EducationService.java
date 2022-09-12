package com.example.finalezlearning.business.services;

import com.example.finalezlearning.auth.entity.User;
import com.example.finalezlearning.auth.repository.UserRepository;
import com.example.finalezlearning.business.entity.Courses;
import com.example.finalezlearning.business.entity.Education;
import com.example.finalezlearning.business.repository.CoursesRepository;
import com.example.finalezlearning.business.repository.EducationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Optional;

@Service
public class EducationService {
    private EducationRepository educationRepository;
    private CoursesRepository coursesRepository;
    private UserRepository userRepository;

    @Autowired
    public EducationService(EducationRepository educationRepository,CoursesRepository coursesRepository,UserRepository userRepository){
        this.coursesRepository = coursesRepository;
        this.educationRepository = educationRepository;
        this.userRepository = userRepository;
    }

//    public void createEducation(Long id_course, String username) throws Exception {
//        Courses courses = coursesRepository.findById(id_course).get();
//        User user = userRepository.findByUsername(username)// change myself
//                .orElseThrow(() -> new Exception("USER NOT FOUND"));
//
//        if (null != educationRepository.findByCoursesAndUsername(courses, user)) {
//            throw new Exception("You are already enrolled in this course");//Вы уже зарегистрированы на этот курс
//        }
//        LocalDate date = LocalDate.now(); //возвращает объект, который представляет текущую дату
//        Education education = new Education(date, user, courses);
//        educationRepository.save(education);
//    }
}

