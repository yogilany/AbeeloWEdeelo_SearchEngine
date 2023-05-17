package com.example.api.service;

public class Refrence {
    String url;
    Integer frequency;
    Integer priority;
    Integer tf;

    public Refrence(String url, Integer priority, Integer tf, Integer frequency) {
        this.url = url;
        this.priority = priority;
        this.tf = tf;
        this.frequency = frequency;
    }
}
