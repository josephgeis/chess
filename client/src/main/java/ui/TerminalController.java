package ui;

import client.ClientState;
import com.googlecode.lanterna.TerminalPosition;
import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.graphics.TextGraphics;
import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.input.KeyType;
import com.googlecode.lanterna.screen.Screen;
import com.googlecode.lanterna.terminal.DefaultTerminalFactory;
import ui.menubar.MenuBar;
import ui.views.View;

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
    TextGraphics textGraphics;

    View view;
    MenuBar menuBar;

    public void eventLoop() throws IOException {
        if (screen.doResizeIfNecessary() != null) {
            screen.clear();
            menuBar.setTextGraphics(textGraphics);
        };

        KeyStroke keyStroke = screen.pollInput();
        if (keyStroke != null) {
            handleInput(keyStroke);
        }

        view.draw();
        menuBar.draw();
        screen.refresh();
    }

    protected void handleInput(KeyStroke keyStroke) {

    }

    public void init() throws IOException {
        screen = defaultTerminalFactory.createScreen();
        textGraphics = screen.newTextGraphics();
        TerminalSize terminalSize = screen.getTerminalSize();

        menuBar = MenuBar.fromTextGraphics(textGraphics);
        view = new View(textGraphics) {
            @Override
            public void draw() {
                this.textGraphics.setBackgroundColor(TextColor.ANSI.RED);
                this.textGraphics.fillRectangle(TerminalPosition.TOP_LEFT_CORNER, this.textGraphics.getSize(), ' ');
            }
        };

        screen.startScreen();
    }

    public void tearDown() throws IOException {
        if (screen != null) {
            screen.stopScreen();
        }
    }
}
