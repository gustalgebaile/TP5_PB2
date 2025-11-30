package com.biblioteca;

import io.javalin.Javalin;
import com.biblioteca.controller.BibliotecaController;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;

public class AppTest {

    @Test
    void testApplicationClassExists() {
        Assertions.assertNotNull(com.biblioteca.app.BibliotecaWebApplication.class);
    }

    @Test
    void testJavalinAppCanStart() {
        Javalin app = Javalin.create();
        Assertions.assertNotNull(app);
        app.stop();
    }

    @Test
    void testControllerCanBeInstantiated() {
        Javalin app = Javalin.create().start(7004);

        Assertions.assertDoesNotThrow(() -> {
            new BibliotecaController(app);
        });

        app.stop();
    }
}
