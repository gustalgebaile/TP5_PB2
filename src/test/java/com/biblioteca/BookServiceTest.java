package com.biblioteca;

import com.biblioteca.exception.BookNotFoundException;
import com.biblioteca.model.Book;
import com.biblioteca.service.BookService;
import net.jqwik.api.*;
import net.jqwik.api.lifecycle.BeforeTry;
import org.junit.jupiter.api.Assertions;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class BookServiceTest {

    BookService service;

    @BeforeTry
    void setup() {
        Book.resetSequence();
        service = new BookService();
    }

    @Provide
    Arbitrary<String> validNames() {
        return Arbitraries.strings()
                .withCharRange('a', 'z')
                .ofMinLength(3)
                .ofMaxLength(25);
    }

    @Provide
    Arbitrary<String> validAutors() {
        return Arbitraries.strings()
                .withCharRange('a', 'z')
                .ofMinLength(3)
                .ofMaxLength(25);
    }

    @Provide
    Arbitrary<String> validCategories() {
        return Arbitraries.of("Ficção", "Não-Ficção", "Romance", "Mistério",
                "Fantasia", "Terror", "Ciência Ficção", "Biografia",
                "Poesia", "Distopia", "Infantil", "Épico");
    }

    @Example
    void testGetBibliotecaReturnsBooks() {
        List<Book> books = service.getBiblioteca();
        Assertions.assertNotNull(books);
        Assertions.assertTrue(books.size() > 0);
    }

    @Example
    void testGetBibliotecaReturnsDefaultBooks() {
        List<Book> books = service.getBiblioteca();
        Assertions.assertEquals(10, books.size());
    }

    @Example
    void testGetBibliotecaAfterAddingBook() {
        int initialSize = service.getBiblioteca().size();
        Book newBook = new Book("New Book", "Author", "Romance");

        service.createBook(newBook);

        int finalSize = service.getBiblioteca().size();
        Assertions.assertEquals(initialSize + 1, finalSize);
    }

    @Example
    void testGetBookByIdWithValidId() {
        List<Book> books = service.getBiblioteca();
        Book firstBook = books.get(0);

        Book found = service.getBookById(firstBook.getId());
        Assertions.assertNotNull(found);
        Assertions.assertEquals(firstBook.getName(), found.getName());
    }

    @Example
    void testGetBookByIdWithInvalidIdThrowsException() {
        assertThrows(BookNotFoundException.class, () -> {
            service.getBookById(9999);
        });
    }

    @Example
    void testCreateBookWithValidData() {
        int initialSize = service.getBiblioteca().size();
        Book book = new Book("Test Book", "Test Author", "Romance");

        assertDoesNotThrow(() -> {
            service.createBook(book);
        });

        Assertions.assertEquals(initialSize + 1, service.getBiblioteca().size());
    }

    @Example
    void testCreateBookWithNullNameThrowsException() {
        Book book = new Book(null, "Author", "Romance");

        assertThrows(IllegalArgumentException.class, () -> {
            service.createBook(book);
        });
    }

    @Example
    void testCreateBookWithNullAutorThrowsException() {
        Book book = new Book("Name", null, "Romance");

        assertThrows(IllegalArgumentException.class, () -> {
            service.createBook(book);
        });
    }

    @Example
    void testCreateBookWithNullGeneroThrowsException() {
        Book book = new Book("Name", "Author", null);

        assertThrows(IllegalArgumentException.class, () -> {
            service.createBook(book);
        });
    }

    @Example
    void testCreateBookWithEmptyNameThrowsException() {
        Book book = new Book("", "Author", "Romance");

        assertThrows(IllegalArgumentException.class, () -> {
            service.createBook(book);
        });
    }

    @Example
    void testCreateBookWithEmptyAutorThrowsException() {
        Book book = new Book("Name", "", "Romance");

        assertThrows(IllegalArgumentException.class, () -> {
            service.createBook(book);
        });
    }

    @Example
    void testCreateBookWithEmptyGeneroThrowsException() {
        Book book = new Book("Name", "Author", "");

        assertThrows(IllegalArgumentException.class, () -> {
            service.createBook(book);
        });
    }

    @Property
    void testCreateMultipleBooks(@ForAll("validNames") String name1,
                                 @ForAll("validNames") String name2,
                                 @ForAll("validAutors") String autor1,
                                 @ForAll("validAutors") String autor2,
                                 @ForAll("validCategories") String category) {
        Book book1 = new Book(name1, autor1, category);
        Book book2 = new Book(name2, autor2, category);

        service.createBook(book1);
        service.createBook(book2);

        List<Book> books = service.getBiblioteca();
        Assertions.assertTrue(books.stream().anyMatch(b -> b.getId() == book1.getId()));
        Assertions.assertTrue(books.stream().anyMatch(b -> b.getId() == book2.getId()));
    }

    @Example
    void testUpdateBookWithValidData() {
        Book book = service.getBiblioteca().get(0);
        int id = book.getId();

        Book updated = new Book(id, "Updated Name", "Updated Author", "Terror");

        assertDoesNotThrow(() -> {
            service.updateBook(updated);
        });

        Book found = service.getBookById(id);
        Assertions.assertEquals("Updated Name", found.getName());
    }

    @Example
    void testUpdateBookWithNullNameThrowsException() {
        Book book = new Book(1, null, "Author", "Romance");

        assertThrows(IllegalArgumentException.class, () -> {
            service.updateBook(book);
        });
    }

    @Example
    void testUpdateNonExistentBookThrowsException() {
        Book book = new Book(9999, "Name", "Author", "Romance");

        assertThrows(BookNotFoundException.class, () -> {
            service.updateBook(book);
        });
    }

    @Property
    void testUpdateBookPreservesId(@ForAll("validNames") String newName,
                                   @ForAll("validAutors") String newAutor,
                                   @ForAll("validCategories") String newCategory) {
        Book original = service.getBiblioteca().get(0);
        int originalId = original.getId();

        Book updated = new Book(originalId, newName, newAutor, newCategory);
        service.updateBook(updated);

        Book found = service.getBookById(originalId);
        Assertions.assertEquals(originalId, found.getId());
    }

    @Example
    void testDeleteBookByIdWithValidId() {
        Book book = service.getBiblioteca().get(0);
        int id = book.getId();

        assertDoesNotThrow(() -> {
            service.deleteBookById(id);
        });

        assertThrows(BookNotFoundException.class, () -> {
            service.getBookById(id);
        });
    }

    @Example
    void testDeleteBookByIdWithInvalidIdThrowsException() {
        assertThrows(BookNotFoundException.class, () -> {
            service.deleteBookById(9999);
        });
    }

    @Example
    void testDeleteBookDecreasesBibliotecaSize() {
        int initialSize = service.getBiblioteca().size();
        Book book = service.getBiblioteca().get(0);

        service.deleteBookById(book.getId());

        Assertions.assertEquals(initialSize - 1, service.getBiblioteca().size());
    }

    @Example
    void testDeleteMultipleBooks() {
        int initialSize = service.getBiblioteca().size();

        Book book1 = service.getBiblioteca().get(0);
        Book book2 = service.getBiblioteca().get(1);

        service.deleteBookById(book1.getId());
        service.deleteBookById(book2.getId());

        Assertions.assertEquals(initialSize - 2, service.getBiblioteca().size());
    }

    @Example
    void testIsBookFoundWithValidId() {
        List<Book> books = service.getBiblioteca();
        Book book = books.get(0);

        boolean found = service.isBookFound(book.getId());
        Assertions.assertTrue(found);
    }

    @Example
    void testIsBookFoundAfterCreatingBook() {
        Book book = new Book("New Book", "Author", "Romance");
        service.createBook(book);

        boolean found = service.isBookFound(book.getId());
        Assertions.assertTrue(found);
    }

    @Example
    void testValidationAcceptsAllCategories() {
        String[] categories = {"Ficção", "Não-Ficção", "Romance", "Mistério",
                "Fantasia", "Terror", "Ciência Ficção", "Biografia",
                "Poesia", "Distopia", "Infantil", "Épico"};

        for (String category : categories) {
            Book book = new Book("Test", "Author", category);

            assertDoesNotThrow(() -> {
                service.createBook(book);
            });
        }
    }

    @Example
    void testValidationRejectsBooksWithInvalidData() {
        Book[] invalidBooks = {
                new Book(null, "Author", "Romance"),
                new Book("Name", null, "Romance"),
                new Book("Name", "Author", null),
                new Book("", "Author", "Romance"),
                new Book("Name", "", "Romance"),
                new Book("Name", "Author", "")
        };

        for (Book book : invalidBooks) {
            assertThrows(IllegalArgumentException.class, () -> {
                service.createBook(book);
            });
        }
    }


    @Example
    void testCompleteWorkflow() {
        Book book = new Book("Workflow Test", "Test Author", "Romance");
        service.createBook(book);

        Book found = service.getBookById(book.getId());
        Assertions.assertEquals("Workflow Test", found.getName());

        Book updated = new Book(book.getId(), "Updated Workflow", "Updated Author", "Terror");
        service.updateBook(updated);

        Book foundAfterUpdate = service.getBookById(book.getId());
        Assertions.assertEquals("Updated Workflow", foundAfterUpdate.getName());

        service.deleteBookById(book.getId());

        assertThrows(BookNotFoundException.class, () -> {
            service.getBookById(book.getId());
        });
    }

    @Example
    void testServiceMaintainsDataIntegrity() {
        List<Book> initialBooks = service.getBiblioteca();
        int initialSize = initialBooks.size();

        Book book = new Book("Integrity Test", "Author", "Romance");
        service.createBook(book);
        service.deleteBookById(book.getId());

        List<Book> finalBooks = service.getBiblioteca();
        Assertions.assertEquals(initialSize, finalBooks.size());
    }
}
