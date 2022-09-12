package com.example.finalezlearning.business.controllers;
import com.example.finalezlearning.auth.entity.User;
import com.example.finalezlearning.auth.repository.UserRepository;
import com.example.finalezlearning.business.entity.Courses;
import com.example.finalezlearning.business.entity.Professor;
import com.example.finalezlearning.business.repository.CoursesRepository;
import com.example.finalezlearning.business.repository.EducationRepository;
import com.example.finalezlearning.business.repository.ProfessorRepository;
import com.example.finalezlearning.business.services.CoursesService;
import com.example.finalezlearning.dto.CourseDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/courses")
public class CoursesController {
    private CoursesService coursesService;
    private CoursesRepository coursesRepository;
    private EducationRepository educationRepository;
    private UserRepository userRepository;
    private ProfessorRepository professorRepository;

    @Autowired
    public CoursesController(ProfessorRepository professorRepository,CoursesService coursesService,EducationRepository educationRepository,
                             UserRepository userRepository,
                             CoursesRepository coursesRepository){

        this.coursesService = coursesService;
        this.coursesRepository = coursesRepository;
        this.educationRepository = educationRepository;
        this.userRepository = userRepository;
        this.professorRepository = professorRepository;

    }

    @GetMapping("/add/{id_professor}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public String addCourses(@PathVariable Long id_professor, Model model) {
        try {
            Professor current = professorRepository.findById(id_professor).get();
            model.addAttribute("courses");
            model.addAttribute("professor", current);
            return "courses/courses-add";
        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("error", e);
            return "error";
        }
    }
    @PostMapping("/add/{id_professor}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public String saveCourses(@PathVariable Long id_professor, CourseDto course, Model model) {
        try {
            Professor current = professorRepository.findById(id_professor).get();
            course.setProfessor(current);
            coursesService.create(course);
            return "redirect:/courses";
        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("error", e);
            return "error";
        }
    }
    @GetMapping("/edit/{id_course}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public String getCourseForUpdate(@PathVariable Long id_course, Model model) {
        try {
            Courses courseActual = coursesRepository.findById(id_course).get();
            model.addAttribute("course", courseActual);
            return "courses/courses-edit";
        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("error", e);
            return "error";
        }
    }

    @PostMapping("/edit/{id_professor}/{id_course}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public String updateCourse(@PathVariable Long id_profesor, @PathVariable Long id_curso, Courses courses, Model model, RedirectAttributes attributes) {

        try {
            Professor currentProfesor = professorRepository.findById(id_profesor).get();
            courses.setProfessor(currentProfesor);

            coursesService.update(courses, id_curso);
            attributes.addAttribute("id_curso", id_curso);

            return "redirect:/courses/{id_course}";
        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("error", e);
            return "error";
        }
    }

    @GetMapping
    public String getCoursesList(Model model) {
        List<Courses> cursos = coursesService.getAll();
        model.addAttribute("courses", cursos);
        return "courses/courses";
    }

    @GetMapping("/delete/{id_course}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public String deleteCurso(@PathVariable Long id_course, Model model) {
        try {
            Courses cursoActual = coursesRepository.findById(id_course).get();
            coursesService.delete(cursoActual);

            return "redirect:/courses";
        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("error", e);
            return "error";
        }
    }

//    @GetMapping("/{id_courses}")
//    @PreAuthorize("hasRole('ROLE_USER')")
//    public String getCourseDetail(@PathVariable Long id_curso, Authentication authentication, Model model) {
//        String username = authentication.getName();
//        Boolean educationdo = false;
//        try {
//            Courses courses = coursesRepository.findById(id_curso).get();
//            User user = userRepository.findByUsername(username)
//                    .orElseThrow(() -> new Exception("USER NOT FOUND"));
//            if (null != educationRepository.findByCoursesAndUsername(courses, user)) {
//                educationdo = true;
//            }
//            model.addAttribute("course", courses);
//            model.addAttribute("education", educationdo);
//            return "course/course-detail";
//        } catch (Exception e) {
//            e.printStackTrace();
//            model.addAttribute("error", e);
//            return "error";
//        }
//    }

}
