package me.hgal.event;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BinaryOperator;
import java.util.function.Consumer;
import java.util.stream.Stream;

/**
 * Manages events and their respective listeners.
 *
 * This class utilizes Java 8 {@code Consumer}s. When a listener is registered,
 * all other listeners are composed by {@code Consumer.andThen} so no iteration
 * is necessary when an event is emitted.
 *
 * @see Consumer
 * @see Consumer#andThen
 */
public class EventManager {

    private Map<Class<? extends Event>, Consumer<? extends Event>> functions = new HashMap<>();

    /**
     * Stores listeners as seperate {@code Consumer}s in case a listener is to be cleared.
     */
    private Map<Class<? extends Event>, List<Consumer<? extends Event>>> allListeners = new HashMap<>();

    /**
     * Binds a listener to an Event.
     *
     * This method looks up the current {@code Consumer} for a provided
     * {@code Event} class, then calls {@code Consumer.andThen} to chain the
     * {@code Consumer}s together and updates {@code functions} with the new
     * value. It also updates the {@code allListeners} list to contain the
     * added listener.
     *
     * @param listener      {@code Consumer} of the event
     * @param eventClass    {@code Event} class to listen for
     * @param <C>           Type of {@code Event} to listen for
     *
     * @see #functions
     * @see Event
     * @see Consumer
     * @see Consumer#andThen
     */
    public <C extends Event> void on(Class<C> eventClass, Consumer<C> listener) {
        List<Consumer<? extends Event>> consumers;
        Consumer<C> combined;

        if (this.functions.containsKey(eventClass)) {
            consumers = this.allListeners.get(eventClass);
            consumers.add(listener);
            combined = this.reduce(eventClass, null, consumers.stream());
        } else {
            consumers = new ArrayList<>();
            consumers.add(listener);
            combined = listener;
        }

        this.allListeners.put(eventClass, consumers);
        this.functions.put(eventClass, combined);
    }

    /**
     * Reduces a stream of {@code Consumer}s to a single {@code Consumer} of
     * the desired type.
     *
     * This method uses {@code Stream.reduce} to combine a given stream of
     * {@code Consumer}s into one consumer. In our use context, all of the
     * casting is safe, since the {@code Stream} will always contain
     * {@code Consumer}s of the same type, designated by the eventClass
     * parameter. {@code eventClass} is never actually used, but it is
     * necessary to provide the type {@code C}, because {@code identity} is
     * always null. {@code identity} is used to make the casting at the end
     * work, because {@code Stream.reduce}, when given an identity, returns
     * the type of the identity.
     *
     * @param eventClass    {@code Class} of the event to be consumed. This
     *                      is only necessary to ensure return type
     * @param identity      {@code Consumer} of the desired type. Again only
     *                      necessary to ensure return type
     * @param consumers     {@code Stream} of {@code Consumer}s to be reduced
     * @param <C>           Type of event to be consumed
     * @return              the reduced stream of type {@code Consumer<C>}
     *
     * @see Consumer
     * @see Stream
     * @see Stream#reduce
     */
    @SuppressWarnings("unchecked")
    private <C extends Event> Consumer<C> reduce(Class<C> eventClass, Consumer<C> identity, Stream<Consumer<? extends Event>> consumers) {
        BinaryOperator<Consumer<? extends Event>> op = (c1, c2) -> {
            if (c1 == null) {
                return (Consumer<C>) c2;
            } else {
                Consumer<C> from = (Consumer<C>) c1;
                Consumer<C> to = (Consumer<C>) c2;
                return from.andThen(to);
            }
        };

        return (Consumer<C>) consumers.reduce(identity, op);
    }


    /**
     * Removes a given listener.
     *
     * This method checks {@code this.allListeners} for the provided listener
     * and removes it. It then reduces the updated {@code List} of listeners
     * and updates {@code this.functions} accordingly.
     *
     * @param eventClass    Event class the provided listener is listening on
     * @param listener      The listener to be cleared
     * @param <C>           Type of {@code Event} {@code listener} is listening
     *                      on
     * @return              {@code true} if {@code listener} was removed
     */
    public <C extends Event> boolean removeListener(Class<C> eventClass, Consumer<C> listener) {
        List<Consumer<? extends Event>> listeners = this.allListeners.get(eventClass);
        if (listeners.remove(listener)) {
            Consumer<C> combined = this.reduce(eventClass, null, listeners.stream());
            this.functions.put(eventClass, combined);
            return true;
        }

        return false;
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

    public int getListenerCount(Class<? extends Event> eventClass) {
        List<Consumer<? extends Event>> listeners = this.allListeners.get(eventClass);
        if (listeners == null)
            return 0;
        return listeners.size();
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
