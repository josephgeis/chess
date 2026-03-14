package ui;

import com.googlecode.lanterna.TerminalPosition;
import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.graphics.TextGraphics;
import com.googlecode.lanterna.screen.Screen;

public class MenuBar {
    Screen screen;

    public MenuBar(Screen screen) {
        this.screen = screen;
    }

    TerminalPosition getBottomRowStart(TerminalSize terminalSize) {
        return new TerminalPosition(0, terminalSize.getRows() - 1);
    }

    TerminalSize getMenuBarSize(TerminalSize terminalSize) {
        return new TerminalSize(terminalSize.getColumns(), 1);
    }

    void draw() {
        TerminalSize terminalSize = screen.getTerminalSize();

        TextGraphics textGraphics = screen.newTextGraphics();

        textGraphics.setBackgroundColor(TextColor.ANSI.CYAN_BRIGHT);
        textGraphics.fillRectangle(getBottomRowStart(terminalSize), getMenuBarSize(terminalSize), ' ');
    }
}
