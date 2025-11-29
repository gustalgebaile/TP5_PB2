package com.biblioteca.app;

import io.javalin.Javalin;
import com.biblioteca.controller.BibliotecaController;
import com.biblioteca.controller.UserController;

public class BibliotecaWebApplication
{
    public static void main( String[] args ) {
        Javalin app = Javalin.create().start(7000);

        new BibliotecaController(app);
        new UserController(app);
    }
}
