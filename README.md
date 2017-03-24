# EventEmitter [![Build Status](https://travis-ci.org/harrygallagher4/EventManager.svg?branch=master)](https://travis-ci.org/harrygallagher4/EventManager)

## About

This project is a Java event emitter based on Java 8's [`FunctionalInterface`][]. The emitter maps `Event` classes to [`Consumer`][]s. If you haven't been introduced to these Java 8 features, you should probably read up on them. I'm not going to explain them here. 

The event emitter is *really* simple. In fact, it's more documentation than it is code. It basically stores `Consumer`s in a `HashMap` keyed by their respective `Event` class. When registering a listener, it is required that the `Consumer` is able to accept an instance of the `Event` it is being registered to.

Sadly, I couldn't quite abuse Java generics to make this system *perfect*, so there is some casting going on. It's all safe though, since an `Event` will never be registered to a `Consumer` that is unable to accept it.

## Usage

Super easy. 

First, instantiate an `EventEmitter`
~~~java
import me.hgal.event.EventEmitter;

EventEmitter emitter = new EventEmitter();
~~~

Next, register a listener to an event. A listener is any method that accepts only one parameter: an instance of the event. Lambdas will do the trick.
~~~java
import SimpleEvent; //events implement me.hgal.event.Event
// Pretend SimpleEvent accepts one argument, and toString() returns it

emitter.on(SimpleEvent.class, e -> System.out.println(e.toString()));
~~~

Or if you prefer not to use lambdas...
~~~java
class MyListener {

    public MyListener(EventEmitter emitter) {
        emitter.on(SimpleEvent.class, this::myMethod);
    }

    void myMethod(SimpleEvent e) {
        System.out.println(e.toString());
    }

}
~~~

Now you can fire an event
~~~java
emitter.emit(new SimpleEvent(1));
// > 1
~~~

You can even store an event and apply side effects
~~~java
emitter.on(SimpleEvent.class, e -> { e.setVal(0) });

Event e = emitter.emit(new SimpleEvent(1));
System.out.println(e.toString())
// > 0
~~~

[`FunctionalInterface`]: https://docs.oracle.com/javase/8/docs/api/java/lang/FunctionalInterface.html
[`Consumer`]: https://docs.oracle.com/javase/8/docs/api/java/util/function/Consumer.html
