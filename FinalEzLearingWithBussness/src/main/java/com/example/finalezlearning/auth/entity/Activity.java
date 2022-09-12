package com.example.finalezlearning.auth.entity;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;

/*

Вся активность пользователя (активация аккаунта, другие действия по необходимости)

*/


@DynamicUpdate
@Entity
@Table(name="ACTIVITY")
public class Activity { // название таблицы будет браться автоматически по названию класса с маленькой буквы: activity

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Type(type = "org.hibernate.type.NumericBooleanType") // для автоматической конвертации числа в true/false
    @Column
    private boolean activated; // становится true только после подтверждения активации пользователем (обратно false уже стать не может по логике)

    @NotBlank
    @Column(updatable = false)
    private String uuid; // создается только один раз, нужен для активации пользователя

    @JsonIgnore // чтобы не было бесконечного обратного зацикливания (JSON не сможет сформироваться) - ссылку на Activity для JSON имеем только из User
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User user; // привязка к пользователю

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public boolean isActivated() {
        return activated;
    }

    public void setActivated(boolean activated) {
        this.activated = activated;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}


