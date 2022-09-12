package com.example.finalezlearning.business.entity;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "courses") //, schema = "public", catalog = "finalproject"
public class Courses {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "course_id")
    private Long courseId;
    @Basic
    @Column(name = "description")
    private String description;
    @Basic
    @Column(name = "detail")
    private String detail;
    @Basic
    @Column(name = "difficulty")
    private String difficulty;
    @Basic
    @Column(name = "imgurl")
    private String imgurl;
    @Basic
    @Column(name = "name")
    private String name;
    @Basic
    @Column(name = "url")
    private String url;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "professor_id"/*, referencedColumnName = "course_id"*/)
     private Professor professor;

    public Courses(String description, String detail, String difficulty, String imgurl, String name, String url, Professor professor) {
        //this.courseId = courseId;
        this.description = description;
        this.detail = detail;
        this.difficulty = difficulty;
        this.imgurl = imgurl;
        this.name = name;
        this.url = url;
    }

    public Long getCourseId() {
        return courseId;
    }

    public void setCourseId(Long courseId) {
        this.courseId = courseId;
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

    public String getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(String difficulty) {
        this.difficulty = difficulty;
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

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }



    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Courses that = (Courses) o;
        return Objects.equals(courseId, that.courseId) && Objects.equals(description, that.description) && Objects.equals(detail, that.detail) && Objects.equals(difficulty, that.difficulty) && Objects.equals(imgurl, that.imgurl) && Objects.equals(name, that.name) && Objects.equals(url, that.url);
    }

    @Override
    public int hashCode() {
        return Objects.hash(courseId, description, detail, difficulty, imgurl, name, url);
    }

}

