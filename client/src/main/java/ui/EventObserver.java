package ui;

import java.util.HashSet;

public interface EventObserver {
    EventPublisher eventPublisher = EventPublisher.getInstance();
    HashSet<EventPublisher.EventHandler> eventHandlers = new HashSet<>();

    default void registerEventHandler(EventPublisher.EventType eventType, Runnable callback) {
        EventPublisher.EventHandler handler = eventPublisher.registerEventHandler(eventType, callback);
        eventHandlers.add(handler);
    }

    default void removeEventHandler(EventPublisher.EventHandler handler) {
        eventHandlers.remove(handler);
        eventPublisher.removeEventHandler(handler);
    }

    default void cancelEventHandlers() {
        for (EventPublisher.EventHandler handler : (HashSet< EventPublisher.EventHandler>) eventHandlers.clone()) {
            removeEventHandler(handler);
        }
    }
}
