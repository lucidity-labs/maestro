package org.example.example.activity;

public class MyActivityImpl implements MyActivity {

    private final Foo foo;

    public MyActivityImpl(Foo foo) {
        this.foo = foo;
    }

    public String doSomething() {
        System.out.println(this + " is interacting with " + foo);
        return "foo is: " + foo.toString();
    }
}
