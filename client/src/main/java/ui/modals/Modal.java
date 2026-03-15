 package ui.modals;

import com.googlecode.lanterna.TerminalPosition;
import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.graphics.TextGraphics;
import ui.TerminalController;
import ui.views.View;

public abstract class Modal extends View {

    protected abstract TerminalSize getSize();

    public Modal(TextGraphics parentTextGraphics, TerminalController terminalController) {
        super(parentTextGraphics, terminalController);
    }

    @Override
    public void draw() {
        textGraphics.setBackgroundColor(TextColor.ANSI.WHITE);
        textGraphics.setForegroundColor(TextColor.ANSI.BLACK);

        var topRight = TerminalPosition.TOP_LEFT_CORNER.withRelativeColumn(getSize().getColumns() - 1);
        var bottomLeft = TerminalPosition.TOP_LEFT_CORNER.withRelativeRow(getSize().getRows() - 1);
        TerminalPosition bottomRight = TerminalPosition.TOP_LEFT_CORNER.withRelative(getSize().getColumns() - 1, getSize().getRows() - 1);

        textGraphics.fillRectangle(TerminalPosition.TOP_LEFT_CORNER, getSize(), ' ');
        textGraphics.drawLine(TerminalPosition.TOP_LEFT_CORNER, topRight, '\u2500');
        textGraphics.drawLine(bottomLeft, bottomRight, '\u2500');
        textGraphics.drawLine(TerminalPosition.TOP_LEFT_CORNER, bottomLeft, '\u2502');
        textGraphics.drawLine(topRight, bottomRight, '\u2502');
        textGraphics.putString(TerminalPosition.TOP_LEFT_CORNER, "┌");
        textGraphics.putString(topRight, "┐");
        textGraphics.putString(bottomLeft, "└");
        textGraphics.putString(bottomRight, "┘");
    }

    @Override
    public void setTextGraphics(TextGraphics parentTextGraphics) {
        TerminalSize parentSize = parentTextGraphics.getSize();
        int colOffset = (parentSize.getColumns() - getSize().getColumns()) / 2;
        int rowOffset = (parentSize.getRows() - getSize().getRows()) / 2;

        this.textGraphics = parentTextGraphics.newTextGraphics(
                new TerminalPosition(colOffset, rowOffset), getSize());
    }

    public abstract void onClose();
}
