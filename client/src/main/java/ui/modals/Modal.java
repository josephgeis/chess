 package ui.modals;

import com.googlecode.lanterna.TerminalPosition;
import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.graphics.TextGraphics;
import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.input.KeyType;

import ui.views.View;

public abstract class Modal extends View {

    TextGraphics shadowTextGraphics;

    protected abstract TerminalSize getSize();

    protected Runnable onDismiss;

    public Modal(TextGraphics parentTextGraphics, Runnable onDismiss) {
        super(parentTextGraphics);
        this.onDismiss = onDismiss;
    }

    protected void defaultColor() {
        textGraphics.setBackgroundColor(TextColor.ANSI.WHITE);
        textGraphics.setForegroundColor(TextColor.ANSI.BLACK);
    }

    @Override
    public void draw() {
        shadowTextGraphics.setBackgroundColor(TextColor.ANSI.BLACK);
        shadowTextGraphics.fillRectangle(TerminalPosition.TOP_LEFT_CORNER, getSize(), ' ');

        defaultColor();

        var topRight = TerminalPosition.TOP_LEFT_CORNER.withRelativeColumn(getSize().getColumns() - 1);
        var bottomLeft = TerminalPosition.TOP_LEFT_CORNER.withRelativeRow(getSize().getRows() - 1);
        TerminalPosition bottomRight = TerminalPosition.TOP_LEFT_CORNER.withRelative(getSize().getColumns() - 1, getSize().getRows() - 1);

        textGraphics.fillRectangle(TerminalPosition.TOP_LEFT_CORNER, getSize(), ' ');
        textGraphics.drawLine(TerminalPosition.TOP_LEFT_CORNER, topRight, '─');
        textGraphics.drawLine(bottomLeft, bottomRight, '─');
        textGraphics.drawLine(TerminalPosition.TOP_LEFT_CORNER, bottomLeft, '│');
        textGraphics.drawLine(topRight, bottomRight, '│');
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

        TerminalPosition modalPosition = new TerminalPosition(colOffset, rowOffset);
        this.textGraphics = parentTextGraphics.newTextGraphics(
                modalPosition, getSize()
        );
        this.shadowTextGraphics = parentTextGraphics.newTextGraphics(
                modalPosition.withRelative(2, 1), getSize()
        );
    }

    @Override
    public void onKeyStroke(KeyStroke keyStroke) {
        if (keyStroke.getKeyType() == KeyType.Enter) {
            onDismiss.run();
        }
    }
}
