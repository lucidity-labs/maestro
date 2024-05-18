package org.example.mymarketingapp;

public class MyActivity {

    private final SomeClass someClass;

    public MyActivity(SomeClass someClass) {
        this.someClass = someClass;
    }

    public void doSomething() {
        System.out.println(this + " is doing something with " + someClass);
    }
}
