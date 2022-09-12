package com.example.finalezlearning.auth.entity;
import lombok.*;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
@Entity
@Data
@Table(name = "user") //, schema = "public", catalog = "finalproject2"
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column
    private String username; // имя пользователя (аккаунта)
    // обратная ссылка - указываем поле "user" из Activity, которое ссылается на User
    // Activity имеет внешний ключ на User
    @OneToOne(mappedBy = "user", fetch = FetchType.LAZY)
    public Activity activity; // действия пользователя (активация и любые другие)
    //@Email // встроенный валидатор на правильное написание email
    @Column
    private String email;

    @Column
    private String surname;
    @Column
    private String password; // пароль желательно занулять сразу после аутентификации (в контроллере), чтобы он нигде больше не "светился"

    private String imgurl;

    private String detail;
    @ManyToMany(fetch = FetchType.EAGER) // таблица role ссылается на user через промежуточную таблицу user_role
    @JoinTable(	name = "USER_ROLE",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id"))

    private Set<Role> roles = new HashSet<>();
    // для сравнения объектов User между собой (если email равны - значит объекты тоже равны)
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return email.equals(user.email); // сравнение объектов по email
    }
    // обязательно нужно реализовывать, если реализован equals
    @Override
    public int hashCode() {
        return Objects.hash(email);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }
    public String getImgurl() {
        return imgurl;
    }

    public void setImgurl(String imgurl) {
        this.imgurl = imgurl;
    }
    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

    public Activity getActivity() {
        return activity;
    }

    public void setActivity(Activity activity) {
        this.activity = activity;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Set<Role> getRoles() {
        return roles;
    }

    public void setRoles(Set<Role> roles) {
        this.roles = roles;
    }
}