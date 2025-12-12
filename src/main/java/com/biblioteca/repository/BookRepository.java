package com.biblioteca.repository;

import com.biblioteca.exception.BookNotFoundException;
import com.biblioteca.model.Book;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class BookRepository {

    private List<Book> repository = new ArrayList<>();

    public BookRepository() {
        Book c1 = new Book("Alice no País das Maravilhas", "Lewis Carroll", "Fantasia");
        Book c2 = new Book("O Senhor dos Anéis", "J.R.R. Tolkien", "Fantasia");
        Book c3 = new Book("Harry Potter", "J.K. Rowling", "Fantasia");
        Book c4 = new Book("Jogos Vorazes", "Suzanne Collins", "Distopia");
        Book c5 = new Book("O Diário de Anne Frank", "Anne Frank", "Biografia");
        Book c6 = new Book("Frankenstein", "Mary Shelley", "Terror");
        Book c7 = new Book("Turma da Mônica", "Mauricio de Sousa", "Infantil");
        Book c8 = new Book("Memórias Póstumas de Brás Cubas", "Machado de Assis", "Romance");
        Book c9 = new Book("A Odisseia", "Homero", "Épico");
        Book c10 = new Book("Dom Casmurro", "Machado de Assis", "Romance");

        repository.addAll(List.of(c1, c2, c3, c4, c5, c6, c7, c8, c9, c10));
    }
    public List<Book> getRepository() {
        return new ArrayList<>(repository);
    }

    public Book findBookById(int id) {
        for (Book book : repository) {
            if (id == book.getId()) {
                return book;
            }
        }
        throw new BookNotFoundException("Livro com ID " + id + " não encontrado");
    }
    public void addBook(Book book) {
        if (book == null) {
            throw new IllegalArgumentException("Livro não pode ser nulo");
        }
        repository.add(book);
    }
    public void updateBook(Book newBook) {
        if (newBook == null) {
            throw new IllegalArgumentException("Livro não pode ser nulo");}
        for (Book book : repository) {
            if (Objects.equals(book.getId(), newBook.getId())) {
                book.setAutor(newBook.getAutor());
                book.setName(newBook.getName());
                book.setCategory(newBook.getCategory());
                return;
            }
        } throw new BookNotFoundException("Livro com ID " + newBook.getId() + " não encontrado");
    }
    public void deleteBook(int id) {
        for (Book book : repository) {
            if (book.getId() == id) {
                repository.remove(book);
                return;
            }
        } throw new BookNotFoundException("Livro com ID " + id + " não encontrado");
    }
    public boolean exists(int id) {
        return repository.stream().anyMatch(b -> b.getId() == id);
    }
    public int size() {
        return repository.size();
    }
}
