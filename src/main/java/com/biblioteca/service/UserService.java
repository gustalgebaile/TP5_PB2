package com.biblioteca.service;

import com.biblioteca.exception.UserNotFoundException;
import com.biblioteca.model.User;
import com.biblioteca.repository.UserRepository;

import java.util.List;

public class UserService {

    private final UserRepository repository;

    public UserService() {
        this.repository = new UserRepository();
    }

    public List<User> getUsers(){
        return repository.getRepository();
    }


    public User getUserByEmail(String email) {
        if(isUserFound(email)){
            return repository.findUserByEmail(email);
        }
        throw new UserNotFoundException("Usuário com Email: " + email + " não encontrado");
    }

    public boolean isUserFound(String email) {
        User user = repository.findUserByEmail(email);
        if (user != null) {
            return true;
        }
        throw new UserNotFoundException("Usuário com Email: " + email + " não encontrado");
    }
}
