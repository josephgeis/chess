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
public class TerminalController implements EventObserver {
    ViewPresenter viewPresenter;
    EventPublisher eventPublisher = EventPublisher.getInstance();
    DefaultTerminalFactory defaultTerminalFactory = new DefaultTerminalFactory();
    Screen screen;
    TextGraphics textGraphics;

    MenuBar menuBar;

    public void pushView(View newView) {
        viewPresenter.pushView(newView);
    }

    public void popView() {
        viewPresenter.popView();
    }

    public void eventLoop() throws IOException {
        View view = viewPresenter.activeView();
        if (view == null) {
            eventPublisher.fireEvent(EventPublisher.EventType.QUIT_PROGRAM);
            return;
        }

        if (screen.doResizeIfNecessary() != null) {
            screen.clear();
            menuBar.setTextGraphics(textGraphics);
            view.setTextGraphics(textGraphics);
        }

        view.draw();
        menuBar.setMenuItems(view.getMenuItems());
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
        EventPublisher.EventType eventType = null;

        if (fnKeys.contains(keyType)) {
            eventType = menuBar.getEventForMenuKey(keyType);
            if (eventType != null) {
                eventPublisher.fireEvent(eventType);
            }
        } else if (keyStroke.getKeyType() == EOF) {
            eventPublisher.fireEvent(EventPublisher.EventType.QUIT_PROGRAM);
        } else {
            viewPresenter.activeView().onKeyStroke(keyStroke);
        }
    }

    public void init() throws IOException {
        screen = defaultTerminalFactory.createScreen();
        textGraphics = screen.newTextGraphics();
        viewPresenter = new ViewPresenter(this, textGraphics);

        menuBar = new MenuBar(textGraphics);
        viewPresenter.pushNewView(PreLoginView.class);

        screen.startScreen();
    }

    public void tearDown() throws IOException {
        if (screen != null) {
            screen.stopScreen();
        }
    }
}
