package ui;

import com.googlecode.lanterna.screen.Screen;
import com.googlecode.lanterna.terminal.DefaultTerminalFactory;
import ui.menubar.MenuBar;

import java.io.IOException;

/**
 * Responsible for drawing the parts of the screen and receives keyboard input.
 * Screen parts:
 * 1. Field (where the chess board, games list are drawn [depending on context])
 * 2. Menu bar
 */
public class TerminalController {
    DefaultTerminalFactory defaultTerminalFactory = new DefaultTerminalFactory();
    Screen screen;

    MenuBar menuBar;

    public void eventLoop() throws IOException {
        if (screen.doResizeIfNecessary() != null) {
            screen.clear();
        };

        menuBar.draw();
        screen.refresh();
    }

    public void init() throws IOException {
        screen = defaultTerminalFactory.createScreen();
        menuBar = new MenuBar(screen);
        screen.startScreen();
    }

    public void tearDown() throws IOException {
        if (screen != null) {
            screen.stopScreen();
        }
    }
}
