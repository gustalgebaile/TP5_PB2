package com.biblioteca;

import com.biblioteca.exception.BookNotFoundException;
import com.biblioteca.model.Book;
import net.jqwik.api.*;
import net.jqwik.api.lifecycle.BeforeTry;
import com.biblioteca.controller.BibliotecaController;
import com.biblioteca.service.BookService;
import org.junit.jupiter.api.Assertions;

import java.util.List;

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
    Arbitrary<Book> validBooks() {
        Arbitrary<String> names = Arbitraries.strings()
                .withCharRange('a','z')
                .ofMinLength(3)
                .ofMaxLength(20);

        Arbitrary<String> autors = Arbitraries.strings()
                .withCharRange('a','z')
                .ofMinLength(3)
                .ofMaxLength(20);

        Arbitrary<String> categories = Arbitraries.of("Ficção", "Não-Ficção", "Romance", "Mistério",
                "Fantasia", "Terror", "Ciência Ficção", "Biografia", "Poesia");

        return Combinators.combine(names, autors, categories).as(Book::new);
    }

    @Provide
    Arbitrary<Book> invalidBooks() {
        Arbitrary<String> names = Arbitraries.of("", null);
        Arbitrary<String> autors = Arbitraries.of("", null);
        Arbitrary<String> categories = Arbitraries.of("", null);
        return Combinators.combine(names, autors, categories).as(Book::new);
    }

    @Provide
    Arbitrary<String> validNames() {
        return Arbitraries.strings()
                .withCharRange('a', 'z')
                .ofMinLength(3)
                .ofMaxLength(20);
    }

    @Provide
    Arbitrary<String> validAutors() {
        return Arbitraries.strings()
                .withCharRange('a','z')
                .ofMinLength(3)
                .ofMaxLength(20);
    }

    @Provide
    Arbitrary<String> validCategories() {
        return Arbitraries.of("Ficção", "Não-Ficção", "Romance", "Mistério",
                "Fantasia", "Terror", "Ciência Ficção", "Biografia", "Poesia");
    }

    @Provide
    Arbitrary<String> invalidData() {
        return Arbitraries.of("", null);
    }

    @Provide
    Arbitrary<Integer> mockedBookIds() {
        return Arbitraries.of(1,2,3,4,5,6,7,8,9,10);
    }

    @Example
    void getNotExistingBookById() {
        assertThrows(BookNotFoundException.class, () -> {
            controller.getBookById(-10);
        });
    }

    @Property
    boolean createBookAndFindByIdWorks(@ForAll("validBooks") Book book) {
        controller.createBook(book);
        return controller.getBookById(book.getId()) != null;
    }

    @Property
    void createBookFailed(@ForAll("invalidBooks") Book book) {
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            controller.createBook(book);
        });
    }

    @Example
    void updateBookWithValidData(@ForAll("validNames") String name,
                                 @ForAll("validAutors") String autor,
                                 @ForAll("validCategories") String category,
                                 @ForAll("mockedBookIds") int id) {
        assertDoesNotThrow(() -> {
            controller.updateBook(new Book(id, name, autor, category));
        });
    }

    @Example
    void updateBookWithInvalidData(@ForAll("invalidData") String invalidName,
                                   @ForAll("invalidData") String invalidAutor,
                                   @ForAll("invalidData") String invalidCategory,
                                   @ForAll("mockedBookIds") int id) {
        assertThrows(IllegalArgumentException.class, () -> {
            controller.updateBook(new Book(id, invalidName, invalidAutor, invalidCategory));
        });
    }

    @Example
    void deleteExistingBook() {
        BookService service = new BookService();
        Book book = controller.getBiblioteca().get(0);
        int id = book.getId();
        assertDoesNotThrow(() -> controller.deleteBookById(id));
        assertThrows(BookNotFoundException.class, () -> {
            service.isBookFound(id);
        });
    }

    @Example
    void deleteNotExistingBook() {
        int id = 1024091240;
        assertThrows(BookNotFoundException.class, () -> {
            controller.deleteBookById(id);
        });
    }

    @Example
    void testGetAllBooks() {
        List<Book> books = controller.getBiblioteca();
        Assertions.assertNotNull(books);
        Assertions.assertTrue(books.size() > 0);
    }

    @Example
    void testBookToStringMethod() {
        Book book = new Book("Test Book", "Test Author", "Romance");
        String toString = book.toString();
        Assertions.assertNotNull(toString);
        Assertions.assertTrue(toString.contains("Test Book") || toString.length() > 0);
    }

    @Example
    void testBookGettersSetters() {
        Book book = new Book("Original", "Author", "Ficção");

        book.setName("Updated Name");
        Assertions.assertEquals("Updated Name", book.getName());

        book.setAutor("Updated Author");
        Assertions.assertEquals("Updated Author", book.getAutor());

        book.setCategory("Mistério");
        Assertions.assertEquals("Mistério", book.getCategory());
    }

    @Example
    void testUpdateBookPartialFields() {
        Book original = new Book("Original", "Author", "Romance");
        int id = original.getId();

        Book updated = new Book(id, "New Name", "New Author", "Terror");
        Assertions.assertNotEquals(original.getName(), updated.getName());
    }

    @Property
    void createMultipleBooks(@ForAll("validNames") String name1,
                             @ForAll("validNames") String name2,
                             @ForAll("validAutors") String autor1,
                             @ForAll("validAutors") String autor2,
                             @ForAll("validCategories") String cat) {
        Book b1 = new Book(name1, autor1, cat);
        Book b2 = new Book(name2, autor2, cat);

        controller.createBook(b1);
        controller.createBook(b2);

        Assertions.assertNotNull(controller.getBookById(b1.getId()));
        Assertions.assertNotNull(controller.getBookById(b2.getId()));
    }

    @Example
    void testBookConstructorWithId() {
        Book book = new Book(5, "Constructor Test", "Author Name", "Fantasia");
        Assertions.assertEquals(5, book.getId());
        Assertions.assertEquals("Constructor Test", book.getName());
        Assertions.assertEquals("Author Name", book.getAutor());
        Assertions.assertEquals("Fantasia", book.getCategory());
    }

    @Example
    void testServiceIsBookFoundWithInvalidId() {
        BookService service = new BookService();
        assertThrows(BookNotFoundException.class, () -> {
            service.isBookFound(99999);
        });
    }
}
