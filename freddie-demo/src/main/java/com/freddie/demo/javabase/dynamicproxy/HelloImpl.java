package com.freddie.demo.javabase.dynamicproxy;

public class HelloImpl implements Hello {
    @Override
    public void sayHello() {
        System.out.println("Hello World");
    }
}
