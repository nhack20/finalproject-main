package com.example.finalezlearning.dto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProfessorDto {
    private String name;
    private String username;
    private String password;
    private String surname;
    private String email;
    private String description;
    private String imgurl;
}

