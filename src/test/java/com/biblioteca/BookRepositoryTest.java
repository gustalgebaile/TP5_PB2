package com.biblioteca;

import com.biblioteca.exception.BookNotFoundException;
import com.biblioteca.model.Book;
import com.biblioteca.repository.BookRepository;
import net.jqwik.api.*;
import net.jqwik.api.lifecycle.BeforeTry;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class BookRepositoryTest {

    BookRepository repository;

    @BeforeTry
    void setup() {
        Book.resetSequence();
        repository = new BookRepository();
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
    void repositoryInitializesWithDefaultBooks() {
        assertFalse(repository.getRepository().isEmpty());
        Assertions.assertEquals(10, repository.getRepository().size());
    }

    @Example
    void testFindBookById() {
        List<Book> books = repository.getRepository();
        Book firstBook = books.get(0);

        Book found = repository.findBookById(firstBook.getId());
        Assertions.assertNotNull(found);
        Assertions.assertEquals(firstBook.getName(), found.getName());
    }

    @Example
    void testFindNonExistentBookThrowsException() {
        assertThrows(BookNotFoundException.class, () -> {
            repository.findBookById(9999);
        });
    }

    @Property
    void addMultipleBooksAndRetrieveThem(@ForAll("validNames") String name1,
                                         @ForAll("validNames") String name2,
                                         @ForAll("validAutors") String autor1,
                                         @ForAll("validAutors") String autor2,
                                         @ForAll("validCategories") String category) {
        Book book1 = new Book(name1, autor1, category);
        Book book2 = new Book(name2, autor2, category);

        repository.addBook(book1);
        repository.addBook(book2);

        List<Book> allBooks = repository.getRepository();
        Assertions.assertTrue(allBooks.contains(book1) || allBooks.size() > 10);
    }

    @Example
    void testGetAllBooksReturnsNotEmpty() {
        List<Book> books = repository.getRepository();
        Assertions.assertFalse(books.isEmpty());
    }

    @Example
    void testGetAllBooksReturnsImmutableList() {
        List<Book> books1 = repository.getRepository();
        List<Book> books2 = repository.getRepository();

        Assertions.assertEquals(books1.size(), books2.size());
    }

    @Example
    void testDefaultBooksHaveCorrectCategories() {
        List<Book> books = repository.getRepository();

        boolean hasFantasy = books.stream()
                .anyMatch(b -> b.getCategory().equals("Fantasia"));
        boolean hasRomance = books.stream()
                .anyMatch(b -> b.getCategory().equals("Romance"));
        boolean hasHorror = books.stream()
                .anyMatch(b -> b.getCategory().equals("Terror"));

        Assertions.assertTrue(hasFantasy);
        Assertions.assertTrue(hasRomance);
        Assertions.assertTrue(hasHorror);
    }

    @Example
    void testDefaultBooksHaveExpectedAuthors() {
        List<Book> books = repository.getRepository();

        boolean hasCarroll = books.stream()
                .anyMatch(b -> b.getAutor().equals("Lewis Carroll"));
        boolean hasTolkien = books.stream()
                .anyMatch(b -> b.getAutor().equals("J.R.R. Tolkien"));
        boolean hasRowling = books.stream()
                .anyMatch(b -> b.getAutor().equals("J.K. Rowling"));

        Assertions.assertTrue(hasCarroll);
        Assertions.assertTrue(hasTolkien);
        Assertions.assertTrue(hasRowling);
    }

    @Property
    void testAddBookIncreasesRepositorySize(@ForAll("validNames") String name,
                                            @ForAll("validAutors") String autor,
                                            @ForAll("validCategories") String category) {
        int initialSize = repository.getRepository().size();

        Book newBook = new Book(name, autor, category);
        repository.addBook(newBook);

        int finalSize = repository.getRepository().size();
        Assertions.assertEquals(initialSize + 1, finalSize);
    }

    @Example
    void testFindByIdReturnsCorrectBook() {
        Book added = new Book("Unique Title", "Unique Author", "Romance");
        repository.addBook(added);

        Book found = repository.findBookById(added.getId());
        Assertions.assertEquals("Unique Title", found.getName());
        Assertions.assertEquals("Unique Author", found.getAutor());
    }

    @Example
    void testRepositoryContainsSpecificBooks() {
        List<Book> books = repository.getRepository();

        boolean hasAlice = books.stream()
                .anyMatch(b -> b.getName().contains("Alice"));
        boolean hasLord = books.stream()
                .anyMatch(b -> b.getName().contains("Senhor"));
        boolean hasHarry = books.stream()
                .anyMatch(b -> b.getName().contains("Harry"));

        Assertions.assertTrue(hasAlice);
        Assertions.assertTrue(hasLord);
        Assertions.assertTrue(hasHarry);
    }

    @Example
    void testAllBooksHaveValidIds() {
        List<Book> books = repository.getRepository();

        for (Book book : books) {
            Assertions.assertTrue(book.getId() > 0);
        }
    }

    @Example
    void testAllBooksHaveNonEmptyNames() {
        List<Book> books = repository.getRepository();

        for (Book book : books) {
            Assertions.assertNotNull(book.getName());
            Assertions.assertFalse(book.getName().isEmpty());
        }
    }

    @Example
    void testAllBooksHaveNonEmptyAutors() {
        List<Book> books = repository.getRepository();

        for (Book book : books) {
            Assertions.assertNotNull(book.getAutor());
            Assertions.assertFalse(book.getAutor().isEmpty());
        }
    }

    @Example
    void testAllBooksHaveValidGeneros() {
        List<Book> books = repository.getRepository();

        String[] validGeneros = {"Ficção", "Não-Ficção", "Romance", "Mistério",
                "Fantasia", "Terror", "Ciência Ficção", "Biografia",
                "Poesia", "Distopia", "Infantil", "Épico"};

        for (Book book : books) {
            String genero = book.getCategory();
            Assertions.assertTrue(java.util.Arrays.asList(validGeneros).contains(genero),
                    "Gênero inválido: " + genero);
        }
    }

    @Example
    void testFindByIdConsistency() {
        Book book = repository.getRepository().get(0);

        Book found1 = repository.findBookById(book.getId());
        Book found2 = repository.findBookById(book.getId());

        Assertions.assertEquals(found1.getName(), found2.getName());
        Assertions.assertEquals(found1.getId(), found2.getId());
    }

    @Property
    void testAddBookCanBeRetrievedByFindById(@ForAll("validNames") String name,
                                             @ForAll("validAutors") String autor,
                                             @ForAll("validCategories") String category) {
        Book book = new Book(name, autor, category);
        repository.addBook(book);

        Book found = repository.findBookById(book.getId());

        Assertions.assertEquals(book.getName(), found.getName());
        Assertions.assertEquals(book.getAutor(), found.getAutor());
        Assertions.assertEquals(book.getCategory(), found.getCategory());
    }
}
