package com.example.demo;

public class Foo {
    Integer id;
    String value;

    public Foo(Integer id) {
        this.id = id;
    }

    public Foo(String value) {
        this.value = value;
    }

    public Foo(Integer id, String value) {
        this.id = id;
        this.value = value;
    }
}
