import me.hgal.event.Event;
import me.hgal.event.EventManager;
import org.junit.Test;

import static org.junit.Assert.*;

public class EventTester {

    @Test
    public void emitsEvents() {
        EventManager manager = new EventManager();
        manager.on(SimpleEvent.class, e -> e.setArg(0));

        SimpleEvent e = manager.emit(new SimpleEvent(1));

        assertEquals(e.getArg(), 0);
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
