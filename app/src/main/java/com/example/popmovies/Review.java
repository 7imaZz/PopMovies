package com.example.popmovies;

public class Review {

    private String name;
    private String content;

    public Review(String name, String content) {
        this.name = name;
        this.content = content;
    }

    public String getName() {
        return name;
    }

    public String getContent() {
        return content;
    }
}
