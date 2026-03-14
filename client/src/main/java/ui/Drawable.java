package ui;

import com.googlecode.lanterna.graphics.TextGraphics;

public interface Drawable {
    void setTextGraphics(TextGraphics parentTextGraphics);

    void draw();
}
