package com.momo.certChain.services;

import com.momo.certChain.exception.ObjectNotFoundException;
import com.momo.certChain.exception.ValidationException;
import com.momo.certChain.model.data.User;
import com.momo.certChain.repositories.UserRepository;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
public class UserService {
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User createUser(User user){
        try{
            findUserByEmail(user.getUsername());
            throw new ValidationException("User already exists");
        }catch (ObjectNotFoundException objectNotFoundException){
            return userRepository.save(user);
        }
    }

    public User findUserByEmail(String username){
        return userRepository.findByUsername(username).orElseThrow(()->new ObjectNotFoundException("User"));
    }
}
