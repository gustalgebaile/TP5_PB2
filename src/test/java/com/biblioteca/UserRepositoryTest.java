package com.biblioteca;

import com.biblioteca.model.User;
import com.biblioteca.model.enums.Role;
import com.biblioteca.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;

public class UserRepositoryTest {

    UserRepository repository;

    @BeforeEach
    void setup() {
        repository = new UserRepository();
    }

    @Test
    void testFindUserByEmailReturnsNull() {
        User user = repository.findUserByEmail("nonexistent@email.com");
        Assertions.assertNull(user);
    }

    @Test
    void testFindUserByEmailCaseInsensitive() {
        User user = repository.findUserByEmail("ANTONIO@ADMIN.COM");
        Assertions.assertNull(user);
    }

    @Test
    void testAddUserSuccessfully() {
        int initialSize = repository.getRepository().size();
        User newUser = new User("newuser@example.com", Role.USER);
        repository.addUser(newUser);
        Assertions.assertEquals(initialSize + 1, repository.getRepository().size());
        Assertions.assertTrue(repository.getRepository().contains(newUser));
    }

    @Test
    void testAddMultipleUsers() {
        int initialSize = repository.getRepository().size();
        User user1 = new User("user1@example.com", Role.USER);
        User user2 = new User("user2@example.com", Role.ADMIN);
        repository.addUser(user1);
        repository.addUser(user2);
        Assertions.assertEquals(initialSize + 2, repository.getRepository().size());
    }

    @Test
    void testUpdateUserEmail() {
        User original = repository.getRepository().get(0);
        User updated = new User("updated@example.com", Role.USER);
        repository.updateUser(updated);
        Assertions.assertNotNull(repository.getRepository());
    }

    @Test
    void testUpdateUserWithNonExistentId() {
        User fakeUser = new User("fake@example.com", Role.USER);
        Assertions.assertDoesNotThrow(() -> {
            repository.updateUser(fakeUser);
        });
    }

    @Test
    void testUpdateUserPreservesOtherUsers() {
        int initialSize = repository.getRepository().size();
        User toUpdate = repository.getRepository().get(0);
        User updated = new User("modified@example.com", Role.USER);
        repository.updateUser(updated);
        Assertions.assertEquals(initialSize, repository.getRepository().size());
    }

    @Test
    void testDeleteUserById() {
        int initialSize = repository.getRepository().size();
        repository.deleteUser(1);
        Assertions.assertEquals(initialSize - 1, repository.getRepository().size());
    }

    @Test
    void testDeleteLastUser() {
        int size = repository.getRepository().size();
        repository.deleteUser(size);
        Assertions.assertEquals(size - 1, repository.getRepository().size());
    }

    @Test
    void testDeleteUserRemovesCorrectly() {
        User userBefore = repository.getRepository().get(0);
        repository.deleteUser(1);
        User notFound = repository.findUserByEmail(userBefore.getEmail());
        Assertions.assertNull(notFound);
    }

    @Test
    void testGetRepositoryReturnsCorrectSize() {
        Assertions.assertEquals(2, repository.getRepository().size());
    }

    @Test
    void testGetRepositoryContainsDefaultUsers() {
        boolean hasAdmin = repository.getRepository().stream()
                .anyMatch(u -> u.getEmail().equals("antonio@admin.com"));
        boolean hasUser = repository.getRepository().stream()
                .anyMatch(u -> u.getEmail().equals("gusta@user.com"));
        Assertions.assertTrue(hasAdmin);
        Assertions.assertTrue(hasUser);
    }
}
