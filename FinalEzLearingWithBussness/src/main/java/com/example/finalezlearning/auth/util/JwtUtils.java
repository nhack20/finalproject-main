package com.example.finalezlearning.auth.util;

import com.example.finalezlearning.auth.entity.User;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.*;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

@Component
@Log
public class JwtUtils {

    @Value("${jwt.secret}")
    private String jwtSecret; // секретный ключ для создания jwt (хранится только на сервере, нельзя никуда передавать)


    @Value("${jwt.access_token-expiration}") // 86400000 мс = 1 сутки
    private int accessTokenExpiration; // длительность токена для автоматического логина (все запросы будут автоматически проходить аутентификацию, если в них присутствует JWT)
    // название взяли по аналогии с протоколом OAuth2, но не путайте - это просто название нашего JWT, здесь мы не применяем OAuth2


    // генерация JWT по данным пользователя
    public String createAccessToken(User user) {

        Date currentDate = new Date(); // для отсчета времени от текущего момента - для задания expiration

        Map claims = new HashMap<String, String[]>();
        claims.put("user", user);
        claims.put(Claims.SUBJECT, user.getId());

        return Jwts.builder()

                // задаем claims
                // Какие именно данные (claims) добавлять в JWT (решаете сами)
//                .setSubject((user.getId().toString())) // subject - это одно из стандартных полей jwt (сохраняем неизменяемое id пользователя)
                .setClaims(claims)
                .setIssuedAt(currentDate) // время отсчета - текущий момент
                .setExpiration(new Date(currentDate.getTime() + accessTokenExpiration)) // срок действия access_token

                .signWith(SignatureAlgorithm.HS512, jwtSecret) // используем алгоритм кодирования HS512 (часто используемый в соотношении скорость-качество) - хешируем все данные секретным ключом-строкой
                .compact(); // кодируем в формат Base64 (это не шифрование, а просто представление данных в виде удобной строки)
    }

    // проверить целостность данных (не истек ли срок jwt и пр.)
    public boolean validate(String jwt) {
        try {
            Jwts.
                    parser(). // проверка формата на корректность
                    setSigningKey(jwtSecret). // указываем каким ключом будет проверять подпись
                    parseClaimsJws(jwt); // проверка подписи "секретом"
            return true; // проверка прошла успешно
        } catch (MalformedJwtException e) {
            log.log(Level.SEVERE, "Invalid JWT token: ", jwt);
        } catch (SignatureException e) {
            log.log(Level.SEVERE, "JWT token signature is not valid: ", jwt);
        } catch (UnsupportedJwtException e) {
            log.log(Level.SEVERE, "JWT token is unsupported: ", jwt);
        } catch (IllegalArgumentException e) {
            log.log(Level.SEVERE, "JWT claims string is empty: ", jwt);
        }

        return false; // валидация не прошла успешно (значит данные payload были изменены - подпись была наложена не на этот payload)

        /*
        Сервер проверяет своим ключом JWT.
        Если подпись не прошла проверку (failed) - значит эти данные были подписаны на нашим secret (или сами данные после подписи были изменены), а значит к данным нет доверия.
        Сервер может доверять только тем данным, которые подписаны его secret ключом. Этот ключ хранится только на сервере, а значит никто кроме сервера не мог им воспользоваться и подписать данные.
        */
    }

    public User getUser(String jwt) {
        Map map = (Map)Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(jwt).getBody().get("user");

        ObjectMapper mapper = new ObjectMapper();
        User user = mapper.convertValue(map, User.class);

        return user;
    }




}



