package com.biblioteca.controller;

import com.biblioteca.model.Book;
import io.javalin.Javalin;
import com.biblioteca.service.BookService;
import com.biblioteca.view.BookView;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BibliotecaController {

    private final BookService service;

    public BibliotecaController(Javalin app) {
        this.service = new BookService();
        app.get("/biblioteca",ctx -> ctx.html(BookView.renderList(service.getBiblioteca())));
        app.get("/biblioteca/new",ctx -> ctx.html(BookView.renderForm(new HashMap<>())));
        app.post("/biblioteca",ctx ->{
            String name = ctx.formParam("name");
            String autor = ctx.formParam("autor");
            String category = ctx.formParam("category");
            service.createBook(new Book(name,autor,category));
            ctx.redirect("/biblioteca");
        });
        app.get("/biblioteca/edit/{id}",ctx -> {
            int id = ctx.pathParamAsClass("id",Integer.class).get();
            Book book = service.getBookById(id);
            if(book != null){
                Map<String, Object> model = new HashMap<>();
                model.put("id", book.getId());
                model.put("name", book.getName());
                model.put("autor", book.getAutor());
                model.put("category", book.getCategory());
                ctx.html(BookView.renderForm(model));
            }else{
                ctx.status(404).result("Livro não encontrado");
            }
        });
        app.post("/biblioteca/edit/{id}",ctx -> {
            int id =  ctx.pathParamAsClass("id",Integer.class).get();
            String name = ctx.formParam("name");
            String autor = ctx.formParam("autor");
            String category = ctx.formParam("category");
            service.updateBook(new Book(id,name,autor,category));
            ctx.redirect("/biblioteca");
        });

        app.post("/biblioteca/delete/{id}",ctx -> {
            int id = ctx.pathParamAsClass("id",Integer.class).get();
            service.deleteBookById(id);
            ctx.redirect("/biblioteca");
        });

    }

    public BibliotecaController() {
        this.service = new BookService();
    }

    public List<Book> getBiblioteca(){
        return service.getBiblioteca();
    }

    public Book getBookById(int id){
        return service.getBookById(id);
    }

    public void createBook(Book book){
        service.createBook(book);
    }

    public void updateBook(Book book){
        service.updateBook(book);
    }

    public void deleteBookById(int id) {
        service.deleteBookById(id);
    }
}
