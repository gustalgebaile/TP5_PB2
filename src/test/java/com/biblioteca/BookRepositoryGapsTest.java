package com.biblioteca;

import com.biblioteca.exception.BookNotFoundException;
import com.biblioteca.model.Book;
import com.biblioteca.repository.BookRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;

public class BookRepositoryGapsTest {

    BookRepository repository;

    @BeforeEach
    void setup() {
        Book.resetSequence();
        repository = new BookRepository();
    }

    @Test
    void testAddBookThrowsExceptionWhenNull() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            repository.addBook(null);
        });
    }

    @Test
    void testAddValidBook() {
        int initialSize = repository.size();
        Book book = new Book("Valid Book", "Valid Author", "Romance");
        repository.addBook(book);
        Assertions.assertEquals(initialSize + 1, repository.size());
    }

    @Test
    void testFindByIdThrowsExceptionWhenNotFound() {
        Assertions.assertThrows(BookNotFoundException.class, () -> {
            repository.findBookById(9999);
        });
    }

    @Test
    void testFindByIdExceptionMessage() {
        try {
            repository.findBookById(9999);
            Assertions.fail("Should throw exception");
        } catch (BookNotFoundException e) {
            Assertions.assertTrue(e.getMessage().contains("9999"));
        }
    }

    @Test
    void testFindByIdThrowsForNegativeId() {
        Assertions.assertThrows(BookNotFoundException.class, () -> {
            repository.findBookById(-5);
        });
    }

    // Cobertura: update() com validação null
    @Test
    void testUpdateBookThrowsExceptionWhenNull() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            repository.updateBook(null);
        });
    }

    @Test
    void testUpdateNonExistentBookThrowsException() {
        Book fakeBook = new Book(9999, "Fake", "Author", "Romance");
        Assertions.assertThrows(BookNotFoundException.class, () -> {
            repository.updateBook(fakeBook);
        });
    }

    @Test
    void testUpdateValidBook() {
        Book book = repository.getRepository().get(0);
        Book updated = new Book(book.getId(), "Updated Name", "Updated Author", "Terror");
        Assertions.assertDoesNotThrow(() -> {
            repository.updateBook(updated);
        });
        Book found = repository.findBookById(book.getId());
        Assertions.assertEquals("Updated Name", found.getName());
    }

    @Test
    void testDeleteThrowsExceptionWhenNotFound() {
        Assertions.assertThrows(BookNotFoundException.class, () -> {
            repository.deleteBook(9999);
        });
    }

    @Test
    void testDeleteValidBook() {
        Book book = repository.getRepository().get(0);
        int id = book.getId();
        int initialSize = repository.size();
        repository.deleteBook(id);
        Assertions.assertEquals(initialSize - 1, repository.size());
        Assertions.assertThrows(BookNotFoundException.class, () -> {
            repository.findBookById(id);
        });
    }

    @Test
    void testDeleteThrowsExceptionForNegativeId() {
        Assertions.assertThrows(BookNotFoundException.class, () -> {
            repository.deleteBook(-1);
        });
    }

    @Test
    void testExistsReturnsTrueForValidId() {
        Book book = repository.getRepository().get(0);
        Assertions.assertTrue(repository.exists(book.getId()));
    }

    @Test
    void testExistsReturnsFalseForInvalidId() {
        Assertions.assertFalse(repository.exists(9999));
    }

    @Test
    void testSizeReturnsCorrectValue() {
        Assertions.assertEquals(10, repository.size());
    }

    @Test
    void testSizeIncreasesAfterAdd() {
        int initialSize = repository.size();
        Book book = new Book("Test", "Author", "Romance");
        repository.addBook(book);
        Assertions.assertEquals(initialSize + 1, repository.size());
    }

    @Test
    void testGetAllReturnsNewList() {
        var list1 = repository.getRepository();
        var list2 = repository.getRepository();
        Assertions.assertNotSame(list1, list2);
        Assertions.assertEquals(list1.size(), list2.size());
    }

    @Test
    void testGetAllDoesNotReturnNull() {
        Assertions.assertNotNull(repository.getRepository());
    }
}
