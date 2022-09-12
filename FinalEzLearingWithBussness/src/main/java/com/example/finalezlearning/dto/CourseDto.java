package com.example.finalezlearning.dto;

import com.example.finalezlearning.business.entity.Professor;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class CourseDto {
    private String nameCourses;
    private String descCourses;
    private String difficulty;
    private String detail;
    private String url;
    private String imgurl;
    private Professor professor;

}

