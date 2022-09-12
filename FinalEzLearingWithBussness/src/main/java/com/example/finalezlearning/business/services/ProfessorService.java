package com.example.finalezlearning.business.services;


import com.example.finalezlearning.auth.entity.Activity;
import com.example.finalezlearning.auth.entity.User;
import com.example.finalezlearning.business.entity.Professor;
import com.example.finalezlearning.business.repository.ProfessorRepository;
import com.example.finalezlearning.dto.ProfessorDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProfessorService {
    private ProfessorRepository professorRepository;
    @Autowired
    public ProfessorService(ProfessorRepository professorRepository) {
        this.professorRepository = professorRepository;
    }

    public void create(ProfessorDto professotDto) {
        String name = professotDto.getName();
        String username = professotDto.getUsername();
        String password = professotDto.getPassword();
        String surname = professotDto.getSurname();
        String email = professotDto.getEmail();
        String description = professotDto.getDescription();
        String imgurl = professotDto.getImgurl();
        Professor professor = new Professor(name, username, password,surname, email, description, imgurl);

        professorRepository.save(professor);
    }
    public void registerProfessor(Professor professor){
        professorRepository.save(professor);
    }
    public List<Professor> getAll(){
        return professorRepository.findAll();
    }
    public void update(Professor professor){
        Professor currentProfessor = professorRepository.findById(professor.getProfessorId()).get();

        currentProfessor.setName(professor.getName());
        currentProfessor.setSurname(professor.getSurname());
        currentProfessor.setEmail(professor.getEmail());
        currentProfessor.setDescription(professor.getDescription());
        currentProfessor.setImgurl(professor.getImgurl());

        professorRepository.save(currentProfessor);
    }
    public void patch(Professor professor) {
        Professor current = professorRepository.findById(professor.getProfessorId()).get();

        current.setDetail(professor.getDetail());

        professorRepository.save(current);
    }

    public void delete(Professor professor) {
        professorRepository.delete(professor);
    }
}
