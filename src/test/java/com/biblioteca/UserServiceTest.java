package com.biblioteca;

import com.biblioteca.exception.UserNotFoundException;
import com.biblioteca.model.User;
import com.biblioteca.model.enums.Role;
import com.biblioteca.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;

public class UserServiceTest {

    UserService service;

    @BeforeEach
    void setup() {
        service = new UserService();
    }

    @Test
    void testGetUserByEmailThrowsExceptionWhenNotFound() {
        UserNotFoundException exception = Assertions.assertThrows(
                UserNotFoundException.class,
                () -> service.getUserByEmail("notfound@example.com")
        );
        Assertions.assertTrue(exception.getMessage().contains("notfound@example.com"));
    }

    @Test
    void testGetUserByEmailExceptionMessage() {
        try {
            service.getUserByEmail("invalid@email.com");
            Assertions.fail("Should throw exception");
        } catch (UserNotFoundException e) {
            Assertions.assertTrue(e.getMessage().contains("Email"));
            Assertions.assertTrue(e.getMessage().contains("invalid@email.com"));
        }
    }

    @Test
    void testGetUserByEmailWithMultipleInvalidEmails() {
        String[] invalidEmails = {
                "fake1@example.com",
                "fake2@example.com",
                "fake3@example.com"
        };
        for (String email : invalidEmails) {
            Assertions.assertThrows(
                    UserNotFoundException.class,
                    () -> service.getUserByEmail(email)
            );
        }
    }

    @Test
    void testIsUserFoundThrowsExceptionWhenNotFound() {
        UserNotFoundException exception = Assertions.assertThrows(
                UserNotFoundException.class,
                () -> service.isUserFound("notfound@example.com")
        );
        Assertions.assertTrue(exception.getMessage().contains("notfound@example.com"));
    }

    @Test
    void testIsUserFoundExceptionMessage() {
        try {
            service.isUserFound("invalid@email.com");
            Assertions.fail("Should throw exception");
        } catch (UserNotFoundException e) {
            Assertions.assertTrue(e.getMessage().contains("Email"));
        }
    }

    @Test
    void testIsUserFoundThrowsForEmptyEmail() {
        Assertions.assertThrows(
                UserNotFoundException.class,
                () -> service.isUserFound("")
        );
    }

    @Test
    void testGetUserByEmailReturnsAdminUser() {
        User user = service.getUserByEmail("antonio@admin.com");
        Assertions.assertNotNull(user);
        Assertions.assertEquals(Role.ADMIN, user.getRole());
    }

    @Test
    void testGetUserByEmailReturnsRegularUser() {
        User user = service.getUserByEmail("gusta@user.com");
        Assertions.assertNotNull(user);
        Assertions.assertEquals(Role.USER, user.getRole());
    }

    // Cobertura: isUserFound retorna true
    @Test
    void testIsUserFoundReturnsTrue() {
        boolean found = service.isUserFound("antonio@admin.com");
        Assertions.assertTrue(found);
    }

    @Test
    void testIsUserFoundReturnsTrueForBothUsers() {
        Assertions.assertTrue(service.isUserFound("antonio@admin.com"));
        Assertions.assertTrue(service.isUserFound("gusta@user.com"));
    }

    // Cobertura: getUsers
    @Test
    void testGetUsersReturnsAll() {
        var users = service.getUsers();
        Assertions.assertTrue(users.size() >= 2);
    }
}
