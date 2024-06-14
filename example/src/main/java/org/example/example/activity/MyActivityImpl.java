package org.example.example.activity;

public class MyActivityImpl implements MyActivity {

    private final Foo foo;

    public MyActivityImpl(Foo foo) {
        this.foo = foo;
    }

    public void doSomething() {
        System.out.println(this + " is interacting with " + foo);
    }
}
