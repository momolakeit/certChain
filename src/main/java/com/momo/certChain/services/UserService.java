package com.momo.certChain.services;

import com.momo.certChain.exception.ObjectNotFoundException;
import com.momo.certChain.exception.ValidationException;
import com.momo.certChain.mapping.EmployeeMapper;
import com.momo.certChain.mapping.InstitutionMapper;
import com.momo.certChain.mapping.StudentMapper;
import com.momo.certChain.model.data.Employee;
import com.momo.certChain.model.data.Institution;
import com.momo.certChain.model.data.Student;
import com.momo.certChain.model.data.User;
import com.momo.certChain.model.dto.UserDTO;
import com.momo.certChain.repositories.UserRepository;
import com.momo.certChain.services.request.HeaderCatcherService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

@Service
@Transactional
public class UserService {
    private final UserRepository userRepository;

    private final HeaderCatcherService headerCatcherService;

    public UserService(UserRepository userRepository, HeaderCatcherService headerCatcherService) {
        this.userRepository = userRepository;
        this.headerCatcherService = headerCatcherService;
    }

    public User createUser(User user) {
        try {
            findUserByEmail(user.getUsername());
            throw new ValidationException("User already exists");
        } catch (ObjectNotFoundException objectNotFoundException) {
            return userRepository.save(user);
        }
    }

    public User getUser(String id) {
        return userRepository.findById(id).orElseThrow(() -> new ObjectNotFoundException("User"));
    }

    public User getLoggedUser(){
        return getUser(headerCatcherService.getUserId());
    }

    public UserDTO toDto(User user){
        if(user instanceof Student){
            return StudentMapper.instance.toDTO((Student) user);
        }
        else if(user instanceof Institution){
            return InstitutionMapper.instance.toDTO((Institution) user);
        }
        else if(user instanceof Employee){
            return EmployeeMapper.instance.toDTO((Employee) user);
        }
        throw new IllegalArgumentException("Mauvais User convertit en DTO");
    }

    public User findUserByEmail(String username) {
        return userRepository.findByUsername(username).orElseThrow(() -> new ObjectNotFoundException("User"));
    }
}
