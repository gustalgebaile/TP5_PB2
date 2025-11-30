package com.biblioteca;

import com.biblioteca.exception.BookNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;

public class ExceptionTest {

    @Test
    void testBookNotFoundExceptionWithMessage() {
        BookNotFoundException ex = new BookNotFoundException("Livro não encontrado");
        Assertions.assertEquals("Livro não encontrado", ex.getMessage());
    }

    @Test
    void testBookNotFoundExceptionIsRuntimeException() {
        BookNotFoundException ex = new BookNotFoundException("Test");
        Assertions.assertTrue(ex instanceof RuntimeException);
    }

    @Test
    void testBookNotFoundExceptionCanBeThrown() {
        try {
            throw new BookNotFoundException("Test");
        } catch (BookNotFoundException e) {
            Assertions.assertNotNull(e.getMessage());
        }
    }
}
