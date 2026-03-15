  package ui.modals;

import com.googlecode.lanterna.TerminalPosition;
import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.graphics.TextGraphics;
import ui.TerminalController;

public abstract class LoginModal extends Modal {
    int field = 0;

    public LoginModal(TextGraphics parentTextGraphics, TerminalController terminalController) {
        super(parentTextGraphics, terminalController);
    }

    @Override
    protected TerminalSize getSize() {
        return new TerminalSize(40, 8);
    }

    @Override
    public void draw() {
        super.draw();
        textGraphics.putString(2, 0, "Login");

        textGraphics.putString(TerminalPosition.OFFSET_1x1, "Username:");

        if (field == 0) {
            textGraphics.setBackgroundColor(TextColor.ANSI.YELLOW);
            textGraphics.setForegroundColor(TextColor.ANSI.WHITE_BRIGHT);
        } else {
            textGraphics.setBackgroundColor(TextColor.ANSI.WHITE);
        }

        textGraphics.putString(TerminalPosition.OFFSET_1x1.withRelative(0, 1), "[" );
        textGraphics.fillRectangle(TerminalPosition.OFFSET_1x1.withRelative(1, 1), new TerminalSize(32, 1), '_');
        textGraphics.putString(TerminalPosition.OFFSET_1x1.withRelative(32, 1), "]" );
    }
}
