package com.biblioteca.repository;

import com.biblioteca.model.User;
import com.biblioteca.model.enums.Role;

import java.util.ArrayList;
import java.util.List;

public class UserRepository {

    private List<User> repository = new ArrayList<>();

    public UserRepository() {
        User u1 = new User("antonio@admin.com", Role.ADMIN);
        User u2 = new User("gusta@user.com", Role.USER);
        repository.addAll(List.of(u1,u2));
    }

    public List<User> getRepository() {
        return repository;
    }

    public User findUserByEmail(String email) {
        for(User user : repository){
            if(email.equals(user.getEmail())){
                return user;
            }
        }
        return null;
    }

    public void addUser(User user) {
        repository.add(user);
    }

    public void updateUser(User newUser) {
        for(User user : repository){
            if(user.getId() == newUser.getId()){
                user.setEmail(newUser.getEmail());
            }
        }
    }

    public void deleteUser(int id) {
        repository.remove(id - 1);
    }
}
