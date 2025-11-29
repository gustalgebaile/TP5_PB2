package com.biblioteca.model;

import com.biblioteca.model.enums.Role;

public class User {

    private int id;
    private String email;
    private Role role;
    private static Integer sequence = 0;

    public User(String email, Role role) {
        sequence++;
        id = sequence;
        this.email = email;
        this.role = role;
    }

    public int getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Role getRole() {
        return role;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", email='" + email + '\'' +
                ", role=" + role +
                '}';
    }
}
