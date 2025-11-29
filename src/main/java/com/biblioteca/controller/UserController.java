package com.biblioteca.controller;

import io.javalin.Javalin;
import com.biblioteca.model.User;
import com.biblioteca.model.enums.Role;
import com.biblioteca.service.UserService;

import java.util.List;

public class UserController {

    private final UserService service;
    private static final String SECRET_KEY = "123456";


    public UserController(Javalin app) {
        this.service = new UserService();

        app.get("/hello", ctx -> {
            String email = ctx.queryParam("user");
            User user = service.getUserByEmail(email);

            if (user.getRole() == Role.ADMIN) {
                ctx.result("Bem vindo admin " + user.getEmail() + "! Chave:" + SECRET_KEY);
            } else {
                ctx.result("Bem vindo " + user.getEmail() + "!");
            }
        });

        app.get("/getUser", ctx -> {
            String id = ctx.queryParam("id");
            String query = "SELECT * FROM users WHERE id = " + id;
            System.out.println("Executando query (demo): " + query);

            try{
                int userId = Integer.parseInt(id);
                List<User> userList = service.getUsers();
                System.out.println(userList);
                if(userId > 0 && userId <= userList.size()){
                    ctx.result(userList.stream().filter(u -> u.getId() == userId).findFirst().get().getEmail());
                }else{
                    ctx.result("Usuário não encontrado");
                }
            }catch(Exception e){
                ctx.result("Erro no input");
            }
        });


        app.get("/run", ctx-> {
            String cmd=ctx.queryParam("cmd");
            try{
                Runtime.getRuntime().exec(cmd);
            }catch (Exception e){

            }
            ctx.result("Comando recebido: " + cmd);
        });



    }

}
