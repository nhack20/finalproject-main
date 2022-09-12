package com.example.finalezlearning.business.services;

import com.example.finalezlearning.business.entity.Courses;
import com.example.finalezlearning.business.entity.Professor;
import com.example.finalezlearning.business.repository.CoursesRepository;
import com.example.finalezlearning.dto.CourseDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CoursesService {
    private CoursesRepository coursesRepository;

    @Autowired
    public CoursesService(CoursesRepository coursesRepository) {
        this.coursesRepository = coursesRepository;
    }

    public void create(CourseDto courseDto) throws Exception{
        if (null != coursesRepository.findByName(courseDto.getNameCourses())) {
            throw new Exception("A course with the name already exists " + courseDto.getNameCourses());
        }
        String nameCourses = courseDto.getNameCourses();
        String descCourses = courseDto.getDescCourses();
        String detail = courseDto.getDetail();
        String difficulty = courseDto.getDifficulty();
        String url = courseDto.getUrl();
        String imgurl = courseDto.getImgurl();
        Professor professor = courseDto.getProfessor();

        Courses courses = new Courses(nameCourses,
                descCourses,
                detail,
                difficulty,
                url,
                imgurl,
                professor);
        coursesRepository.save(courses);
    }
    public void update(Courses courses, Long course_id) {
        Courses currentCourses = coursesRepository.findById(course_id).get();

        currentCourses.setName(courses.getName());
        currentCourses.setDescription(courses.getDescription());
        currentCourses.setDetail(courses.getDetail());
        currentCourses.setDifficulty(courses.getDifficulty());
        currentCourses.setUrl(courses.getUrl());
        currentCourses.setImgurl(courses.getImgurl());
        currentCourses.setProfessor(courses.getProfessor());
        coursesRepository.save(currentCourses);
    }

    public void delete(Courses courses){
        coursesRepository.delete(courses);
    }

    public List<Courses> getAll(){
        return coursesRepository.findAll();
    }
}
