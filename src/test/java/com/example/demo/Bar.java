package com.example.demo;

public class Bar {
    String id;
    Integer value;

    public Bar(String id) {
        this.id = id;
    }

    public Bar(Integer value) {
        this.value = value;
    }

    public Bar(String id, Integer value) {
        this.id = id;
        this.value = value;
    }
}
