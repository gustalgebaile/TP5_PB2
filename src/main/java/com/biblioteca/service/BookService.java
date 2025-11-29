package com.biblioteca.service;

import com.biblioteca.exception.BookNotFoundException;
import com.biblioteca.model.Book;
import com.biblioteca.repository.BookRepository;

import java.util.List;

public class BookService {

    private final BookRepository repository;

    public BookService() {
        this.repository = new BookRepository();
    }

    public List<Book> getBiblioteca(){
        return repository.getRepository();
    }

    public Book getBookById(int id) {
        if(isBookFound(id)){
            return repository.findBookById(id);
        }
        throw new BookNotFoundException("Livro com ID: " + id + " não encontrado");
    }

    public void createBook(Book book) {
        if(isBookDataValid(book)){
            repository.addBook(book);
        }else{
            throw new IllegalArgumentException("Dados de criação não devem ser nulos");
        }
    }

    public void updateBook(Book book) {
        if(isBookFound(book.getId())){
            if(isBookDataValid(book)){
                repository.updateBook(book);
            }else{
                throw new IllegalArgumentException("Dados de alteração não devem ser nulos");
            }
        }
    }

    public void deleteBookById(int id) {
        if(isBookFound(id)){
            repository.deleteBook(id);
        }
        else{
            throw new BookNotFoundException("Livro com ID: " + id + " não encontrado");
        }
    }

    public boolean isBookFound(int id) {
        Book book = repository.findBookById(id);
        if (book != null) {
            return true;
        }
        throw new BookNotFoundException("Livro com ID: " + id + " não encontrado");
    }

    private boolean isBookDataValid(Book book){
        return book.getName() != "" && book.getAutor() != "" && book.getName() != null && book.getAutor() != null ;
    }


}
