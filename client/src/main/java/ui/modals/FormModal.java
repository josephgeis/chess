package ui.modals;

import com.googlecode.lanterna.TerminalPosition;
import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.graphics.TextGraphics;
import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.input.KeyType;

public abstract class FormModal extends Modal {
    int field = 0;
    boolean submitted = false;
    String[] fields;

    FormModal(TextGraphics parentTextGraphics, String[] fields, Runnable onDismiss) {
        super(parentTextGraphics, onDismiss);
        this.fields = fields;
    }

    public enum FieldFormat {
        NORMAL,
        PASSWORD,
        PASSWORD_PEEK
    }

    @Override
    final public void onKeyStroke(KeyStroke keyStroke) {
        if (!submitted) {
            handleKeyStroke(keyStroke);
        }
    }

    protected void handleKeyStroke(KeyStroke keyStroke) {
        if (keyStroke.getKeyType() == KeyType.Escape) {
            onDismiss.run();
        }
    }

    protected void drawField(int index, TerminalPosition fieldStartPosition, String name, String value) {
        drawField(index, fieldStartPosition, name, value, FieldFormat.NORMAL);
    }

    protected void drawField(int index, TerminalPosition fieldStartPosition, String name, String value, FieldFormat format) {
        defaultColor();
        textGraphics.putString(fieldStartPosition, "%s:".formatted(name));
        highlightSelectedField(index);
        textGraphics.putString(fieldStartPosition.withRelativeRow(1), "[");
        textGraphics.putString(fieldStartPosition.withRelative(1, 1), "%-32s".formatted(
                switch (format) {
                    case NORMAL -> value.replace(' ', '␣');
                    case PASSWORD -> "*".repeat(value.length());
                    case PASSWORD_PEEK -> !value.isEmpty() ? "*".repeat(value.length() - 1) + value.charAt(value.length() - 1) : "";
                }));
        textGraphics.putString(fieldStartPosition.withRelative(33, 1), "]");
    }

    protected void highlightSelectedField(int field) {
        if (this.field == field) {
            if (!submitted) {
                textGraphics.setBackgroundColor(TextColor.ANSI.YELLOW);
            } else {
                textGraphics.setBackgroundColor(TextColor.ANSI.BLACK_BRIGHT);
            }
            textGraphics.setForegroundColor(TextColor.ANSI.WHITE_BRIGHT);
        } else {
            textGraphics.setBackgroundColor(TextColor.ANSI.WHITE);
            textGraphics.setForegroundColor(TextColor.ANSI.BLACK);
        }
    }

    @Override
    public void onLoad() {
        super.onLoad();
        submitted = false;
        field = 0;
    }
}
