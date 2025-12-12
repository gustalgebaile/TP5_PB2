package com.biblioteca.model;

public class Book {
    private Integer id;
    private String name;
    private String autor;
    private String category;
    private static Integer sequence = 0;

    public Book(String name, String autor, String category) {
        sequence++;
        id = sequence;
        this.name = name;
        this.autor = autor;
        this.category = category;
    }

    public Book(int id, String name, String autor, String category){
        this.id = id;
        this.name = name;
        this.autor = autor;
        this.category = category;
    }

//Setters e Getters
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

    public String getCategory() {return category; }

    public void setCategory(String category) {this.category = category; }

    public static void resetSequence() {
        sequence = 0;
    }



}
