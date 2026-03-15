 package ui.views;

import com.googlecode.lanterna.TerminalPosition;
import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.graphics.TextGraphics;
import ui.TerminalController;

 public class PreLoginView extends View {

     public PreLoginView(TextGraphics parentTextGraphics, TerminalController terminalController) {
         super(parentTextGraphics, terminalController);
     }

     @Override
    public void draw() {
        textGraphics.setBackgroundColor(TextColor.ANSI.BLUE);
        textGraphics.setForegroundColor(TextColor.ANSI.WHITE_BRIGHT);
        textGraphics.fillRectangle(
                TerminalPosition.TOP_LEFT_CORNER,
                textGraphics.getSize(),
                ' '
        );

        textGraphics.putString(TerminalPosition.OFFSET_1x1, "240 Chess Client");
        textGraphics.putString(new TerminalPosition(2, 2), "Not Logged In");
    }
}
