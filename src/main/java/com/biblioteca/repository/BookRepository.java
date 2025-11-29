package com.biblioteca.repository;

import com.biblioteca.model.Book;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class BookRepository {

    private List<Book> repository = new ArrayList<>();

    public BookRepository() {
        Book c1 = new Book("Alice no País das Maravilhas", "Lewis Carroll");
        Book c2 = new Book("O Senhor dos Anéis", "J.R.R. Tolkien");
        Book c3 = new Book("Harry Potter", "J.K. Rowling");
        Book c4 = new Book("Jogos Vorazes", "Suzanne Collins");
        Book c5 = new Book("O Diário de Anne Frank", "Anne Frank");
        Book c6 = new Book("Frankenstein", "Mary Shelley");
        Book c7 = new Book("Turma da Mônica", "Mauricio de Sousa");
        Book c8 = new Book("Memórias Póstumas de Brás Cubas", "Machado de Assis");
        Book c9 = new Book("A Odisseia", "Homero");
        Book c10 = new Book("Dom Casmurro", "Machado de Assis");

        repository.addAll(List.of(c1,c2,c3,c4,c5,c6,c7,c8,c9,c10));
    }

    public List<Book> getRepository() {
        return repository;
    }

    public Book findBookById(int id) {
        for(Book book : repository){
            if(id == book.getId()){
                return book;
            }
        }
       return null;
    }

    public void addBook(Book book) {
        repository.add(book);
    }

    public void updateBook(Book newBook) {
        for(Book book : repository){
            if(Objects.equals(book.getId(), newBook.getId())){
                book.setAutor(newBook.getAutor());
                book.setName(newBook.getName());
            }
        }
    }

    public void deleteBook(int id) {
        repository.remove(id - 1);
    }
}
