package com.example.finalezlearning.business.controllers;

import com.example.finalezlearning.auth.entity.Activity;
import com.example.finalezlearning.auth.entity.Role;
import com.example.finalezlearning.auth.entity.User;
import com.example.finalezlearning.auth.exception.UserOrEmailExistsException;
import com.example.finalezlearning.business.entity.Courses;
import com.example.finalezlearning.business.entity.Professor;
import com.example.finalezlearning.business.repository.CoursesRepository;
import com.example.finalezlearning.business.repository.ProfessorRepository;
import com.example.finalezlearning.business.services.ProfessorService;
import com.example.finalezlearning.dto.ProfessorDto;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.management.relation.RoleNotFoundException;
import javax.validation.Valid;
import java.util.List;
import java.util.UUID;

import static com.example.finalezlearning.auth.services.UserService.DEFAULT_ROLE;

@Controller
@RequestMapping("/professors")
public class ProfessorController {
    private ProfessorRepository professorRepository;
    private ProfessorService professorService;
    private CoursesRepository coursesRepository;

    public ProfessorController(ProfessorRepository professorRepository, ProfessorService professorService, CoursesRepository coursesRepository) {
        this.professorRepository = professorRepository;
        this.professorService = professorService;
        this.coursesRepository = coursesRepository;
    }
//    @GetMapping("/add")
//    @PreAuthorize("hasRole('ROLE_USER')")
//    public String addProfessor(Model model) {
//        model.addAttribute("professor", new ProfessorDto());
//        return "professors/professor-add";
//    }
@GetMapping("/add")
public ResponseEntity register(@Valid @RequestBody Professor professor) throws RoleNotFoundException { // здесь параметр user используется, чтобы передать все данные пользователя для регистрации

//    if (professorService.userExists(user.getUsername(), user.getEmail())) {
//        throw new UserOrEmailExistsException("User or email already exists");
//    }
//
//    Role userRole = professorService.findByName(DEFAULT_ROLE)
//            .orElseThrow(() -> new RoleNotFoundException("Default Role USER not found."));
//    user.getRoles().add(userRole);
//
//    user.setPassword(encoder.encode(user.getPassword())); // hash the password
//
//    Activity activity = new Activity();
//    activity.setUser(user);
//    activity.setUuid(UUID.randomUUID().toString());

    professorService.registerProfessor(professor); // сохранить пользователя в БД
    return ResponseEntity.ok().build(); // просто отправляем статус 200-ОК (без каких-либо данных) - значит регистрация выполнилась успешно
}

    @PostMapping("/save")
    @PreAuthorize("hasRole('ROLE_USER')")
    public String saveProfessor(ProfessorDto professor) {
        professorService.create(professor);
        return "redirect:/professors";
    }

    @GetMapping("/edit/{id_professor}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public String getProfessorForUpdate(@PathVariable Long id_professor,
                                        Model model) {
        try {
            Professor professorActual = professorRepository.findById(id_professor).get();
            model.addAttribute("professor", professorActual);
            return "professors/professor-edit";
        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("error", e);
            return "error";
        }
    }

    @PostMapping("/update/{id_professor}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public String updateProfessor(@PathVariable Long id_professor,
                                  Professor professor, RedirectAttributes attributes, Model model){

        try {
            Professor currentProfessor = professorRepository.findById(id_professor).get();
            currentProfessor.setName(professor.getName());
            currentProfessor.setSurname(professor.getSurname());
            currentProfessor.setEmail(professor.getEmail());
            currentProfessor.setDescription(professor.getDescription());
            currentProfessor.setImgurl(professor.getImgurl());

            professorService.update(professor);
            attributes.addAttribute("id_professor", id_professor);

            return "redirect:/professors/{id_professor}";
        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("error", e);
            return "error";
        }
    }

    @PostMapping("/patch/{id_professor}")
    public String patchProfessor(@PathVariable Long id_professor, Professor professor, RedirectAttributes attributes, Model model) {

        try {
            Professor current = professorRepository.findById(id_professor).get();
            current.setDetail(professor.getDetail());
            professorService.patch(current);

            attributes.addAttribute("id_professor", id_professor);
            return "redirect:/professors/{id_professor}";
        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("error", e);
            return "error";
        }
    }

    @GetMapping
    @PreAuthorize("hasRole('ROLE_USER')")
    public String getProfessorsList(Model model) {
        List<Professor> profesores = professorService.getAll();
        model.addAttribute("professors", profesores);
        return "professors/professors";
    }

    @GetMapping("/delete/{id_professor}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public String deleteProfessor(@PathVariable Long id_professor, Model model) {
        try {
            Professor profesorActual = professorRepository.findById(id_professor).get();
            professorService.delete(profesorActual);

            return "redirect:/professors";
        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("error", e);
            return "error";
        }
    }

    @GetMapping("/{id_professor}")
    @PreAuthorize("hasRole('ROLE_USER')")
    public String getProfesorDetail(@PathVariable Long id_professor, Model model) {
        try {
            Professor professor = professorRepository.findById(id_professor).get();
            model.addAttribute("professor", professor);
            List<Courses> courses = coursesRepository.findAllByProfessor(professor);
            model.addAttribute("courses", courses);
            return "professors/professor-detail";
        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("error", e);
            return "error";
        }
    }
}
