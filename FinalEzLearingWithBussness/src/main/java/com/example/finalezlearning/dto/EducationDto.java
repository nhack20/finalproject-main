package com.example.finalezlearning.dto;
import com.example.finalezlearning.auth.entity.User;
import com.example.finalezlearning.business.entity.Courses;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class EducationDto {
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date fecha;
    private User user;
    private Courses courses;
}

