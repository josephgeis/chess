package ui;

import com.googlecode.lanterna.TerminalPosition;
import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.graphics.TextGraphics;

public abstract class Drawable {
    protected TextGraphics textGraphics;

    public Drawable(TextGraphics parentTextGraphics) {
        setTextGraphics(parentTextGraphics);
    }

    public void setTextGraphics(TextGraphics parentTextGraphics) {
        this.textGraphics = parentTextGraphics;
    }

    abstract public void draw();
}
