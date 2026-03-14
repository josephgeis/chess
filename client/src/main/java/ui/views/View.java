package ui.views;

import com.googlecode.lanterna.graphics.TextGraphics;
import ui.Drawable;

public abstract class View extends Drawable {

    public View(TextGraphics parentTextGraphics) {
        super(parentTextGraphics);
    }


    @Override
    abstract public void draw();
}
