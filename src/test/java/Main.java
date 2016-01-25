import me.hgal.event.Event;
import me.hgal.event.EventManager;

import java.util.Random;

public class Main {

    private static EventManager manager = new EventManager();

    public static void main(String[] args) {
        Random rng = new Random();
        manager.on(SimpleEvent.class, e -> { e.getArg(); e.setArg(rng.nextInt(10000)); });
        testEvents(100000);
    }

    private static void testEvents(int num) {
        long then = System.currentTimeMillis();
        SimpleEvent e = new SimpleEvent(0);
        for (int i = 0; i < num; i++) {
            manager.emit(e);
        }
        long d = System.currentTimeMillis() - then;
        System.out.println("Fired " + num + " events in " + d + "ms");
        System.out.println(e.getArg());
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

