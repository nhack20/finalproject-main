package com.example.finalezlearning.auth.repository;
import com.example.finalezlearning.auth.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
public interface UserRepository extends CrudRepository<User, Long> {
    // для проверки email username
    @Query("select case when count(u)>0 then true else false end from User u where lower(u.email) = lower(:email)")
    boolean existsByEmail(@Param("email") String email);

    @Query("select case when count(u)> 0 then true else false end from User u where lower(u.username) = lower(:username)")
    boolean existsByUsername(@Param("username") String username);

    // используем обертку Optional - контейнер, который хранит значение или null - позволяет избежать ошибки NullPointerException
    Optional<User> findByUsername(String username); // поиск по username

    // используем обертку Optional - контейнер, который хранит значение или null - позволяет избежать ошибки NullPointerException
    Optional<User> findByEmail(String email); // поиск по email

    @Modifying // если запрос изменяет данные - желательно добавлять эту аннотацию
    @Transactional // если запрос изменяет данные - желательно добавлять эту аннотацию
    @Query("UPDATE User u SET u.password = :password WHERE u.username=:username") // обновление записи с нужным UUID
    int updatePassword(@Param("password") String password, @Param("username") String username); // возвращает int (сколько записей обновил) - в данном случае всегда должен возвращать 1
}

