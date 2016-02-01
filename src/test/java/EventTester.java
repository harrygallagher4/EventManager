import me.hgal.event.Event;
import me.hgal.event.EventEmitter;
import org.junit.Test;

import java.util.function.Consumer;

import static org.junit.Assert.*;

public class EventTester {

    @Test
    public void emitsEvents() {
        EventEmitter manager = new EventEmitter();
        final boolean[] received = {false};
        manager.on(SimpleEvent.class, e -> received[0] = true);

        manager.emit(new SimpleEvent(0));

        assertTrue("Event wasn't received", received[0]);
    }

    @Test
    public void removesListeners() {
        EventEmitter manager = new EventEmitter();

        boolean[] received = {false, false};

        Consumer<SimpleEvent> firstListener = (e) -> received[0] = true;
        Consumer<SimpleEvent> secondListener = (e) -> received[1] = true;

        manager.on(SimpleEvent.class, firstListener);
        manager.on(SimpleEvent.class, secondListener);

        boolean removed = manager.removeListener(SimpleEvent.class, secondListener);

        manager.emit(new SimpleEvent(0));

        assertTrue("Listener was not removed", removed);
        assertTrue("Event didn't reach first listener", received[0]);
        assertTrue("Event reached second listener", !received[1]);
    }

    @Test
    public void emitsToMultiple() {
        EventEmitter manager = new EventEmitter();

        final boolean[] received = {false, false};

        Consumer<SimpleEvent> firstListener = (e) -> received[0] = true;
        Consumer<SimpleEvent> secondListener = (e) -> received[1] = true;

        manager.on(SimpleEvent.class, firstListener);
        manager.on(SimpleEvent.class, secondListener);

        manager.emit(new SimpleEvent(0));

        assertTrue("First listener didn't receive event", received[0]);
        assertTrue("Second listener didn't receive event", received[1]);
    }

    private static class SimpleEvent implements Event {

        private Object arg;

        private SimpleEvent(Object arg) {
            this.arg = arg;
        }

        public Object getArg() {
            return arg;
        }

        public void setArg(Object arg) {
            this.arg = arg;
        }
    }

}
