package ui;

import com.googlecode.lanterna.graphics.TextGraphics;
import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.input.KeyType;
import com.googlecode.lanterna.screen.Screen;
import com.googlecode.lanterna.terminal.DefaultTerminalFactory;
import ui.menubar.MenuBar;
import ui.views.PreLoginView;
import ui.views.View;

import java.io.IOException;
import java.util.*;

import static com.googlecode.lanterna.input.KeyType.*;

/**
 * Responsible for drawing the parts of the screen and receives keyboard input.
 * Screen parts:
 * 1. Field (where the chess board, games list are drawn [depending on context])
 * 2. Menu bar
 */
public class TerminalController {
    DefaultTerminalFactory defaultTerminalFactory = new DefaultTerminalFactory();
    Screen screen;
    TextGraphics textGraphics;

    ArrayDeque<View> viewStack = new ArrayDeque<>();
    MenuBar menuBar;

    HashMap<EventType, HashSet<Runnable>> eventHandlers = new HashMap<>();

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

    void fireEvent(EventType eventType) {
        HashSet<Runnable> callbacks = eventHandlers.get(eventType);
        if (callbacks == null) {
            return;
        }

        for (Runnable callback : callbacks) {
            callback.run();
        }
    }

    public void registerEventHandler(EventType eventType, Runnable callback) {
        HashSet<Runnable> callbacks = eventHandlers.computeIfAbsent(eventType, k -> new HashSet<>());
        callbacks.add(callback);
    }

    public void removeEventHandler(EventType eventType, Runnable callback) {
        HashSet<Runnable> callbacks = eventHandlers.computeIfAbsent(eventType, k -> new HashSet<>());
        callbacks.remove(callback);
    }

    View activeView() {
        return viewStack.peekLast();
    }

    public void pushView(View newView) {
        if (activeView() != null) {
            activeView().onUnload();
        }
        viewStack.push(newView);
        activeView().onLoad();
    }

    public <T extends View> void pushNewView(Class<T> viewClass) {
        T newView;
        try {
            newView = viewClass
                    .getConstructor(TextGraphics.class, TerminalController.class)
                    .newInstance(textGraphics, this);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        pushView(newView);
    }

    public void popView() {
        activeView().onUnload();
        viewStack.pop();
        if (activeView() != null) {
            activeView().onUnload();
        }
    }


    public void eventLoop() throws IOException {
        View view = activeView();
        if (view == null) {
            fireEvent(EventType.QUIT_PROGRAM);
            return;
        }

        if (screen.doResizeIfNecessary() != null) {
            screen.clear();
            menuBar.setTextGraphics(textGraphics);
            view.setTextGraphics(textGraphics);
        }

        view.draw();
        menuBar.draw();
        screen.refresh();

        KeyStroke keyStroke = screen.pollInput();
        if (keyStroke != null) {
            handleInput(keyStroke);
        }
    }

    protected void handleInput(KeyStroke keyStroke) {
        Set<KeyType> fnKeys = Set.of(new KeyType[]{F1, F2, F3, F4, F5, F6});

        KeyType keyType = keyStroke.getKeyType();
        EventType eventType = null;

        if (fnKeys.contains(keyType)) {
            eventType = menuBar.getEventForMenuKey(keyType);
        }

        if (eventType != null) {
            fireEvent(eventType);
        }
    }

    public void init() throws IOException {
        screen = defaultTerminalFactory.createScreen();
        textGraphics = screen.newTextGraphics();

        menuBar = new MenuBar(textGraphics);
        pushNewView(PreLoginView.class);

        screen.startScreen();
    }

    public void tearDown() throws IOException {
        if (screen != null) {
            screen.stopScreen();
        }
    }
}
