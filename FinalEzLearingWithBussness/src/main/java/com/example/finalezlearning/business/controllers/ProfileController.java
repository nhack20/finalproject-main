package com.example.finalezlearning.business.controllers;
import com.example.finalezlearning.auth.entity.User;
import com.example.finalezlearning.auth.repository.UserRepository;
import com.example.finalezlearning.auth.services.UserService;
import com.example.finalezlearning.business.entity.Education;
import com.example.finalezlearning.business.repository.EducationRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;

@Controller
@PreAuthorize("hasRole('ROLE_USER')")
@AllArgsConstructor
public class ProfileController {

    private final UserRepository userRepository;
    private final EducationRepository educationRepository;
    private final UserService userService;

//    @GetMapping("/profile")
//    public String getUserProfile(Authentication authentication, Model model) {
//        try {
//            String currentUsername = authentication.getName();
//            User user = userRepository.findByUsername(currentUsername)
//                    .orElseThrow(()-> new Exception("USER NOT FOUND"));
//            List<Education> educations = educationRepository.findAllByUsername(user);
//            int nameCourses = educations.size();
//            model.addAttribute("user", user);
//            model.addAttribute("educations", educations);
//            model.addAttribute("nameCourses", nameCourses);
//            return "user/profile";
//        } catch (Exception e) {
//            e.printStackTrace();
//            model.addAttribute("error", e);
//            return "error";
//        }
//    }

    @GetMapping("/user/edit/{userID}")
    public String getForEdit(@PathVariable Long userID, Authentication authentication, Model model) {

        try {
            String currentUsername = authentication.getName();
            User current = userRepository.findById(userID).get();
            if (currentUsername.equals(current.getUsername())) {
                model.addAttribute(current);
                return "user/user-edit";
            } else {
                throw new Exception("Authentication Error");
            }
        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("error", e);
            return "error";
        }
    }

    @PostMapping("/user/edit/{userID}")
    public String updateUser(@PathVariable Long userID, Authentication authentication, User user, Model model) {

        try {
            User current = userRepository.findById(userID).get();
            String currentUsername = authentication.getName();
            if (currentUsername.equals(current.getUsername())) {
                current.setUsername(user.getUsername());
                current.setSurname(user.getSurname());
                current.setEmail(user.getEmail());
                current.setImgurl(user.getImgurl());
                userService.update(current);
                return "redirect:/profile";
            } else {
                throw new Exception("Authentication Error");
            }

        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("error", e);
            return "error";
        }
    }

    @PostMapping("/user/patch/{id_user}")
    public String patchUser(@PathVariable Long id_user, Authentication authentication, User user, Model model) {

        try {
            User current = userRepository.findById(id_user).get();
            String currentUsername = authentication.getName();
            if (currentUsername.equals(current.getUsername())) {
                current.setDetail(user.getDetail());
                userService.patch(current);
                return "redirect:/profile";
            } else {
                throw new Exception("Authentication Error");
            }
        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("error", e);
            return "error";
        }
    }
}

