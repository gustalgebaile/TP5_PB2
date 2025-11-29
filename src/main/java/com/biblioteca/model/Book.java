package com.biblioteca.model;

public class Book {
    private Integer id;
    private String name;
    private String autor;
    private static Integer sequence = 0;

    public Book(String name, String autor) {
        sequence++;
        id = sequence;
        this.name = name;
        this.autor = autor;
    }


    public Book(int id, String name, String autor){
        this.id = id;
        this.name = name;
        this.autor = autor;
    }


    public Integer getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAutor() {
        return autor;
    }

    public void setAutor(String autor) {this.autor = autor;}

    public static void resetSequence() {
        sequence = 0;
    }



}
