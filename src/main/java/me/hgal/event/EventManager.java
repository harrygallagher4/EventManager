package me.hgal.event;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

/**
 * Manages events and their respective listeners.
 *
 * This class utilizes Java 8 {@code Consumer}s. When a listener is registered,
 * all other listeners are composed by {@code Consumer.andThen} so no iteration
 * is necessary when an event is emitted. Unfortunately, this means listeners
 * can't be unregistered.
 *
 * TODO: implement unregister functionality
 * @see Consumer
 * @see Consumer#andThen(Consumer)
 */
public class EventManager {

    private Map<Class<? extends Event>, Consumer<? extends Event>> functions = new HashMap<>();

    /**
     * Binds a listener to an Event.
     *
     * This method looks up the current {@code Consumer} for a provided
     * {@code Event} class, then calls {@code Consumer.andThen} to chain the
     * {@code Consumer}s together and updates {@code functions} with the new
     * value.
     *
     * @param f     Consumer of the event
     * @param <C>   Type of event to listen for
     *
     * @see #functions
     * @see Event
     * @see Consumer
     * @see Consumer#andThen(Consumer)
     */
    public <C extends Event> void on(Class<C> eventClass, Consumer<C> f) {
        Consumer<C> consumer;

        if (this.functions.containsKey(eventClass)) {
            consumer = this.getConsumer(eventClass);
            this.functions.put(eventClass, consumer.andThen(f));
        } else {
            this.functions.put(eventClass, f);
        }
    }

    /**
     * Emits an {@code Event}
     *
     * @param event instance of the {@code Event}to be emitted
     * @param <T>   type of {@code Event} to be emitted
     * @return      {@code event} after side effects
     *
     * @see Event
     */
    public <T extends Event> T emit(T event) {
        if (this.functions.containsKey(event.getClass())) {
            this.getConsumer(event.getClass()).accept(event);
        }

        return event;
    }

    /**
     * Returns a {@code Consumer} that accepts an instance of the provided
     * class.
     *
     * This is really just a utility that gets a {@code Consumer} from
     * {@code functions} and casts it to a Consumer of the proper type. It's a
     * safe cast, since {@code on} will only register a {@code Consumer} to the
     * class that it consumes.
     *
     * @param c     class to be consumed
     * @param <T>   type to be consumed
     * @return      {@code Consumer} that accepts the provided type
     *
     * @see #functions
     * @see #on
     * @see Consumer
     */
    @SuppressWarnings({"unchecked", "SuspiciousMethodCalls"})
    private <T> Consumer<T> getConsumer(Class<? extends T> c) {
        return (Consumer<T>) this.functions.get(c);
    }

}
