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

    // ?????????? ???? ?????????????? - ???? ???????????? ???????????????? (??????????????) ?????? ?? jwt (???????????????????????? ???????????????? ???????????? ???????????????????? ?????? ????????. ??????????)
    @PostMapping("/logout")
    @PreAuthorize("hasAuthority('USER')")
    public ResponseEntity logout() { // body ?????????????????????? (???????????? ???? ???????????????? ???? ??????????????) - ?????? ???????????? ???????????????????????? ???????????????????? ?? ??????????

        // ?????????????? ???????????? ?????? logout - ?????? ?????????????? ??????

        // ?????????????? ?????? ?? ???????????????? ???????????? ????????????????, ?????? ?????????? ?????????????? ???????????? ?????????? ?????? ??????????????????????????
        HttpCookie cookie = cookieUtils.deleteJwtCookie();

        HttpHeaders responseHeaders = new HttpHeaders(); // ???????????? ?????? ???????????????????? ???????????????????? ?? response
        responseHeaders.add(HttpHeaders.SET_COOKIE, cookie.toString()); // ?????????????????? ?????? ?? ?????????????????? (header)

        // ?? ???????? ?????????????? ???????????? ???? ????????????????????, ???????????????????? ???????????? ?????? ?????????????? ?????????????? (?????????????? ?????????????????????????? ???????????? ??????)
        return ResponseEntity.ok().headers(responseHeaders).build();

    }

    // ?????????????????????? ???????????? ????????????????????????
    @PutMapping("/register")
    public ResponseEntity register(@Valid @RequestBody User user) throws RoleNotFoundException { // ?????????? ???????????????? user ????????????????????????, ?????????? ???????????????? ?????? ???????????? ???????????????????????? ?????? ??????????????????????

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

        userService.register(user, activity); // ?????????????????? ???????????????????????? ?? ????

        return ResponseEntity.ok().build(); // ???????????? ???????????????????? ???????????? 200-???? (?????? ??????????-???????? ????????????) - ???????????? ?????????????????????? ?????????????????????? ??????????????
    }

    // ?????????????????? ???????????????????????? (?????????? ?????? ???????????????????????????? ?? ???????????????? ???????????? ?? ??????????????????????)
    @PostMapping("/activate-account")
    public ResponseEntity<Boolean> activateUser(@RequestBody String uuid) { // true - ?????????????? ??????????????????????

        // ?????????????????? UUID ????????????????????????, ???????????????? ?????????? ????????????????????????
        Activity activity = userService.findActivityByUuid(uuid)
                .orElseThrow(() -> new UsernameNotFoundException("Activity Not Found with uuid: " + uuid));

        // ???????? ???????????????????????? ?????? ?????? ?????????? ??????????????????????
        if (activity.isActivated())
            throw new UserAlreadyActivatedException("User already activated");

        // ???????????????????? ??????-???? ?????????????????????? ?????????????? (?? ?????????? ???????????? ???????????? ???????? 1)
        int updatedCount = userService.activate(uuid); // ???????????????????? ????????????????????????

        return ResponseEntity.ok(updatedCount == 1); // 1 - ???????????? ???????????? ???????????????????? ??????????????, 0 - ??????-???? ?????????? ???? ??????
    }

    // ???????????????????????? ???? ???????????? ????????????????????????
    @PostMapping("/login")
    public ResponseEntity<User> login(@Valid @RequestBody User user) {

        // ?????????????????? ??????????-????????????
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(user.getUsername(), user.getPassword()));

        // ?????????????????? Spring-?????????????????? ???????? ???? ??????????????????????
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // UserDetailsImpl - ???????? ????????????, ?????????????? ???????????????? ?? Spring ???????????????????? ?? ???????????????? ???????????? ????????????????????????
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

        // ?????????????????????? ???????????????????????? ?????? ??????
        if(!userDetails.isActivated()) {
            throw new DisabledException("User disabled"); // ?????????????? ???????????????? ???????????? ?????? ???????????????????????? ???? ??????????????
        }

        String jwt = jwtUtils.createAccessToken(userDetails.getUser());

        userDetails.getUser().setPassword(null); //???????????? ?????????? ???????????? ???????? ?????? ?????? ????????????????????????????

        HttpCookie cookie = cookieUtils.createJwtCookie(jwt); //server-side cookie

        HttpHeaders responseHeaders = new HttpHeaders(); // ???????????? ?????? ???????????????????? ?????????????????? ?? response
        responseHeaders.add(HttpHeaders.SET_COOKIE, cookie.toString()); // ?????????????????? ?????? ?? header

//        return ResponseEntity.ok().body(userDetails.getUser());

        // ???????????????????? ?????????????? ???????????? ???????????????????????? (?? jwt-???????? ?? ?????????????????? Set-Cookie)
        return ResponseEntity.ok().headers(responseHeaders).body(userDetails.getUser());


    }

    // ???????????????????? ???????????? (?????????? ???????????? ???????? ?????????? ???????????? ?? ???????????????? ?????? ???? backend)
    @PostMapping("/update-password")
    @PreAuthorize("hasAuthority('USER')")
    public ResponseEntity<Boolean> updatePassword(@RequestBody String password) { // password - ?????????? ????????????

         /*
            ???? ?????????? ???????? ???????????? ???????? ?????????????????? ???????????????????????????? ?????????????????????? ???????????????????????? ?? AuthTokenFilter ???? ???????????? ???????? jwt.
            ?????? ???????? ???????????????????? ?????????? ???? ????????????????????, ??.??. ???? ???????????? ???????????????? ????????????????????????, ?????? ???????????????? ?????????? ?????????????????? ????????????????
         */
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication(); // ???????????????? ???????????????? ???????????? ????????????????????????????
        UserDetailsImpl user = (UserDetailsImpl) authentication.getPrincipal(); // ???????????????? ???????????????????????? ???? Spring ????????????????????

        // ??????-???? ?????????????????????? ?????????????? (?? ?????????? ???????????? ???????????? ???????? 1, ??.??. ?????????????????? ???????????? ???????????? ????????????????????????)
        int updatedCount = userService.updatePassword(encoder.encode(password), user.getUsername());

        return ResponseEntity.ok(updatedCount == 1); // 1 - ???????????? ???????????? ???????????????????? ??????????????, 0 - ??????-???? ?????????? ???? ??????
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<JsonException> handleException(Exception ex) {
        return new ResponseEntity(new JsonException(ex.getClass().getSimpleName()), HttpStatus.BAD_REQUEST);
    }


}
