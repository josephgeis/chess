package ui;

import java.util.HashMap;
import java.util.HashSet;

public class EventPublisher {
    private static final EventPublisher INSTANCE = new EventPublisher();

    public record EventHandler(Runnable callback, EventType type) {}

    public static EventPublisher getInstance() {
        return INSTANCE;
    }

    HashMap<EventType, HashSet<EventHandler>> eventHandlers = new HashMap<>();

    private EventPublisher() { }

    public void fireEvent(EventType eventType) {
        HashSet<EventHandler> handlers = eventHandlers.get(eventType);
        if (handlers == null) {
            return;
        }

        for (EventHandler handler : handlers) {
            handler.callback().run();
        }
    }

    public EventHandler registerEventHandler(EventType eventType, Runnable callback) {
        HashSet<EventHandler> handlers = eventHandlers.computeIfAbsent(eventType, k -> new HashSet<>());
        EventHandler handler = new EventHandler(callback, eventType);
        handlers.add(handler);
        return handler;
    }

    public void removeEventHandler(EventHandler handler) {
        HashSet<EventHandler> handlers = eventHandlers.computeIfAbsent(handler.type(), k -> new HashSet<>());
        handlers.remove(handler);
    }

    public enum EventType {
        LOG_IN,
        DO_LOG_IN,
        REGISTER,
        QUIT_PROGRAM,
        NEW_GAME,
        JOIN_GAME,
        LIST_GAME,
        SPECTATE_GAME,
        LOG_OUT,
        SHOW_HELP
    }
}