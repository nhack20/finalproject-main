package com.example.finalezlearning.business.repository;

import com.example.finalezlearning.business.entity.Courses;
import com.example.finalezlearning.business.entity.Professor;
import org.springframework.data.jpa.repository.JpaRepository;


import java.util.List;

public interface CoursesRepository extends JpaRepository<Courses,Long> {
    Courses findByName(String name);
    List<Courses> findAllByProfessor(Professor professor);
}
