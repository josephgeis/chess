package ui.views;

import com.googlecode.lanterna.TerminalPosition;
import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.graphics.TextGraphics;
import ui.Drawable;
import ui.menubar.MenuBar;

public class View implements Drawable {
    protected TextGraphics textGraphics;

    public View(TextGraphics parentTextGraphics) {
        setTextGraphics(parentTextGraphics);
    }

    @Override
    public void setTextGraphics(TextGraphics parentTextGraphics) {
        TerminalSize terminalSize = parentTextGraphics.getSize();

        this.textGraphics = parentTextGraphics.newTextGraphics(
                TerminalPosition.TOP_LEFT_CORNER,
                terminalSize.withRelativeRows(-1));
    }

    @Override
    public void draw() {

    }
}
