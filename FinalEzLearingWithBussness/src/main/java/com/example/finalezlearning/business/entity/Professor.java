package com.example.finalezlearning.business.entity;

import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Objects;

@Entity
@NoArgsConstructor
@Table(name = "professor") //, catalog = "finalproject3" //, schema = "public"
public class Professor {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "professor_id")
    private Long professorId;
    @Basic
    @Column(name = "surname")
    private String surname;
    @Basic
    @Column(name = "email")
    private String email;
    @Basic
    @Column(name = "description")
    private String description;
    @Basic
    @Column(name = "detail")
    private String detail;
    @Basic
    @Column(name = "imgurl")
    private String imgurl;
    @Basic
    @Column(name = "name")
    private String name;

    @Basic
    @Column(name = "username")
    private String username;

    @Basic
    @Column(name = "password")
    private String password;

    public Professor(String username, String password, String name, String surname, String email, String description, String detail) {
        this.professorId = professorId;
        this.surname = surname;
        this.email = email;
        this.description = description;
        this.detail = detail;
        this.imgurl = imgurl;
        this.name = name;
        this.username = username;
        this.password = password;
    }


    public Long getProfessorId() {
        return professorId;
    }

    public void setProfessorId(Long professorId) {
        this.professorId = professorId;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

    public String getImgurl() {
        return imgurl;
    }

    public void setImgurl(String imgurl) {
        this.imgurl = imgurl;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Professor that = (Professor) o;
        return Objects.equals(professorId, that.professorId) && Objects.equals(surname, that.surname)
                && Objects.equals(email, that.email) && Objects.equals(description, that.description)
                && Objects.equals(detail, that.detail) && Objects.equals(imgurl, that.imgurl)
                && Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(professorId, surname, email, description, detail, imgurl, name);
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
