package com.biblioteca;

import com.biblioteca.exception.BookNotFoundException;
import com.biblioteca.model.Book;
import net.jqwik.api.*;
import net.jqwik.api.lifecycle.BeforeTry;
import com.biblioteca.controller.BibliotecaController;
import com.biblioteca.service.BookService;
import org.junit.jupiter.api.Assertions;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class BibliotecaControllerTest {

    BibliotecaController controller;

    @BeforeTry
    void setup() {
        Book.resetSequence();
        controller = new BibliotecaController();
    }
    @Provide
    Arbitrary<Book> validBooks(){
        Arbitrary<String> names = Arbitraries.strings()
                .withCharRange('a','z')
                .ofMinLength(3)
                .ofMaxLength(20);

        Arbitrary<String> emails = Arbitraries.strings()
                .withCharRange('a','z')
                .ofMinLength(3)
                .map(s -> s + "aa");

        return Combinators.combine(names,emails).as(Book::new);
    }

    @Provide
    Arbitrary<Book> invalidBooks() {
        Arbitrary<String> names = Arbitraries.of("", null);
        Arbitrary<String> emails = Arbitraries.of("", null);
        return Combinators.combine(names, emails).as(Book::new);
    }

    @Provide
    Arbitrary<String> validNames() {
        return Arbitraries.strings()
                .withCharRange('a', 'z')
                .ofMinLength(3)
                .ofMaxLength(20);
    };

    @Provide
    Arbitrary<String> validAutors() {
        return Arbitraries.strings()
                .withCharRange('a','z')
                .ofMinLength(3)
                .map(s -> s + "aa");
    };

    @Provide
    Arbitrary<String> invalidData() {
        return Arbitraries.of("", null);
    };

    @Provide
    Arbitrary<String> invalidAutors() {
        return Arbitraries.strings()
                .withCharRange('a','z')
                .ofMinLength(3)
                .map(s -> s + "aa");
    };
    @Provide Arbitrary<Integer> mockedBookIds() { return Arbitraries.of(1,2,3,4,5,6,7,8,9,10); }

    @Example
    void getNotExistingBookById(){
        assertThrows(BookNotFoundException.class,() -> {
            controller.getBookById(-10);
        });
    }

    @Property
    boolean createBookAndFindByIdWorks(@ForAll("validBooks") Book book){
        controller.createBook(book);

        return controller.getBookById(book.getId()) != null;
    }

    @Property
    void createBookFailed(@ForAll("invalidBooks") Book book){
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            controller.createBook(book);
        });
    }

    @Example
    void updateBookWithValidData(@ForAll("validNames") String name,@ForAll("validAutors") String autor,@ForAll("mockedBookIds") int id){
        assertDoesNotThrow(() -> {
            controller.updateBook(new Book(id,name,autor));
        });
    }

    @Example
    void updateBookWithInvalidData(@ForAll("invalidData") String data,@ForAll("mockedBookIds") int id){
        assertThrows(IllegalArgumentException.class,() -> {
            controller.updateBook(new Book(id,data,data));
        });

    }

    @Example
    void deleteExistingBook() {
        BookService service = new BookService();
        Book book = controller.getBiblioteca().get(0);
        int id = book.getId();
        assertDoesNotThrow(() -> controller.deleteBookById(id));
        assertThrows(BookNotFoundException.class,() -> {
            service.isBookFound(id);
        });
    }
    @Example
    void deleteNotExistingBook() {
        int id = 1024091240;
        assertThrows(BookNotFoundException.class,() -> {
            controller.deleteBookById(id);
        });
    }




}
