package com.example.finalezlearning.auth.controllers;

import com.example.finalezlearning.auth.entity.Activity;
import com.example.finalezlearning.auth.entity.Role;
import com.example.finalezlearning.auth.entity.User;
import com.example.finalezlearning.auth.exception.UserAlreadyActivatedException;
import com.example.finalezlearning.auth.objects.JsonException;
import com.example.finalezlearning.auth.services.UserDetailsImpl;
import com.example.finalezlearning.auth.services.UserService;
import com.example.finalezlearning.auth.exception.UserOrEmailExistsException;
import com.example.finalezlearning.auth.util.CookieUtils;
import com.example.finalezlearning.auth.util.JwtUtils;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpCookie;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import javax.management.relation.RoleNotFoundException;
import javax.validation.Valid;
import java.util.UUID;

import static com.example.finalezlearning.auth.services.UserService.DEFAULT_ROLE;

@RestController
@RequestMapping("/auth")
@Log
@Setter
@Getter
public class AuthController {

    private UserService userService;
    private PasswordEncoder encoder;
    private AuthenticationManager authenticationManager;

    private JwtUtils jwtUtils;
    private CookieUtils cookieUtils;


    @Autowired
    public AuthController(UserService userService,
                          PasswordEncoder encoder,
                          AuthenticationManager authenticationManager,
                          JwtUtils jwtUtils,
                          CookieUtils cookieUtils) {

        this.userService = userService;
        this.encoder = encoder;
        this.authenticationManager = authenticationManager;
        this.jwtUtils = jwtUtils;
        this.cookieUtils = cookieUtils;

    }

    @GetMapping("/test")
//    @CrossOrigin(origins = "*")
    public String test() {
        return "OK";
    }

    @PostMapping("/test-no-auth")
    public String testNoAuth() {
        return "OK-no-auth";
    }

    @PostMapping("/test-with-auth")
    @PreAuthorize("hasAuthority('USER')")
    public String testWithAuth() {
        return "OK-with-auth";
    }

    // выход из системы - мы должны занулить (удалить) кук с jwt (пользователю придется заново логиниться при след. входе)
    @PostMapping("/logout")
    @PreAuthorize("hasAuthority('USER')")
    public ResponseEntity logout() { // body отсутствует (ничего не передаем от клиента) - все данные пользователя передаются с куком

        // главная задача при logout - это удалить кук

        // создаем кук с истекшим сроком действия, тем самым браузер удалит такой кук автоматически
        HttpCookie cookie = cookieUtils.deleteJwtCookie();

        HttpHeaders responseHeaders = new HttpHeaders(); // объект для добавления заголовков в response
        responseHeaders.add(HttpHeaders.SET_COOKIE, cookie.toString()); // добавляем кук в заголовок (header)

        // в теле запроса ничего не отправляем, отправляем только кук обратно клиенту (браузер автоматически удалит его)
        return ResponseEntity.ok().headers(responseHeaders).build();

    }

    // регистрация нового пользователя
    @PutMapping("/register")
    public ResponseEntity register(@Valid @RequestBody User user) throws RoleNotFoundException { // здесь параметр user используется, чтобы передать все данные пользователя для регистрации

        if (userService.userExists(user.getUsername(), user.getEmail())) {
            throw new UserOrEmailExistsException("User or email already exists");
        }

        Role userRole = userService.findByName(DEFAULT_ROLE)
                .orElseThrow(() -> new RoleNotFoundException("Default Role USER not found."));
        user.getRoles().add(userRole);

        user.setPassword(encoder.encode(user.getPassword())); // hash the password

        Activity activity = new Activity();
        activity.setUser(user);
        activity.setUuid(UUID.randomUUID().toString());

        userService.register(user, activity); // сохранить пользователя в БД

        return ResponseEntity.ok().build(); // просто отправляем статус 200-ОК (без каких-либо данных) - значит регистрация выполнилась успешно
    }

    // активация пользователя (чтобы мог авторизоваться и работать дальше с приложением)
    @PostMapping("/activate-account")
    public ResponseEntity<Boolean> activateUser(@RequestBody String uuid) { // true - успешно активирован

        // проверяем UUID пользователя, которого хотим активировать
        Activity activity = userService.findActivityByUuid(uuid)
                .orElseThrow(() -> new UsernameNotFoundException("Activity Not Found with uuid: " + uuid));

        // если пользователь уже был ранее активирован
        if (activity.isActivated())
            throw new UserAlreadyActivatedException("User already activated");

        // возвращает кол-во обновленных записей (в нашем случае должна быть 1)
        int updatedCount = userService.activate(uuid); // активируем пользователя

        return ResponseEntity.ok(updatedCount == 1); // 1 - значит запись обновилась успешно, 0 - что-то пошло не так
    }

    // залогиниться по паролю пользователя
    @PostMapping("/login")
    public ResponseEntity<User> login(@Valid @RequestBody User user) {

        // проверяем логин-пароль
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(user.getUsername(), user.getPassword()));

        // добавляем Spring-контейнер инфу об авторизации
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // UserDetailsImpl - спец объект, который хранится в Spring контейнере и содержит данные пользователя
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

        // активирован пользователь или нет
        if(!userDetails.isActivated()) {
            throw new DisabledException("User disabled"); // клиенту отправим ошибку что пользователь не активен
        }

        String jwt = jwtUtils.createAccessToken(userDetails.getUser());

        userDetails.getUser().setPassword(null); //пароль нужен только один раз для аутентификации

        HttpCookie cookie = cookieUtils.createJwtCookie(jwt); //server-side cookie

        HttpHeaders responseHeaders = new HttpHeaders(); // объект для добавления заголовка в response
        responseHeaders.add(HttpHeaders.SET_COOKIE, cookie.toString()); // добавляем кук в header

//        return ResponseEntity.ok().body(userDetails.getUser());

        // отправляем клиенту данные пользователя (и jwt-куки в заголовке Set-Cookie)
        return ResponseEntity.ok().headers(responseHeaders).body(userDetails.getUser());


    }

    // обновление пароля (когда клиент ввел новый пароль и отправил его на backend)
    @PostMapping("/update-password")
    @PreAuthorize("hasAuthority('USER')")
    public ResponseEntity<Boolean> updatePassword(@RequestBody String password) { // password - новый пароль

         /*
            До этого шага должна была произойти автоматическая авторизация пользователя в AuthTokenFilter на основе кука jwt.
            Без этой информации метод не выполнится, т.к. не сможем получить пользователя, для которого хотим выполнить операцию
         */
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication(); // пытаемся получить объект аутентификации
        UserDetailsImpl user = (UserDetailsImpl) authentication.getPrincipal(); // получаем пользователя из Spring контейнера

        // кол-во обновленных записей (в нашем случае должно быть 1, т.к. обновляем пароль одного пользователя)
        int updatedCount = userService.updatePassword(encoder.encode(password), user.getUsername());

        return ResponseEntity.ok(updatedCount == 1); // 1 - значит запись обновилась успешно, 0 - что-то пошло не так
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<JsonException> handleException(Exception ex) {
        return new ResponseEntity(new JsonException(ex.getClass().getSimpleName()), HttpStatus.BAD_REQUEST);
    }


}
