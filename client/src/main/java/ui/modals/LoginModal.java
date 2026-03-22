package ui.modals;

import com.googlecode.lanterna.TerminalPosition;
import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.graphics.TextGraphics;
import com.googlecode.lanterna.input.KeyStroke;

public abstract class LoginModal extends Modal {
    int field = 0;
    boolean submitted = false;

    String[] fields = {"", "", null, null};

    public LoginModal(TextGraphics parentTextGraphics, Runnable onDismiss) {
        super(parentTextGraphics, onDismiss);
    }

    @Override
    protected TerminalSize getSize() {
        return new TerminalSize(37, 9);
    }

    public String getUsername() {
        return fields[0];
    }

    public String getPassword() {
        return fields[1];
    }

    @Override
    public void onKeyStroke(KeyStroke keyStroke) {
        if (submitted) {
            return;
        }

        switch (keyStroke.getKeyType()) {
            case Character -> {
                if (fields[field] != null) {
                    fields[field] = (fields[field] + keyStroke.getCharacter())
                            .substring(0, Integer.min(fields[field].length() + 1, 32));
                }
            }
            case Backspace -> {
                if (fields[field] != null) {
                    fields[field] = fields[field].substring(0, Integer.max(fields[field].length() - 1, 0));
                }
            }
            case ArrowDown, ArrowRight, Tab -> field = (field + 1) % fields.length;
            case ArrowUp, ArrowLeft, ReverseTab -> field = (field + fields.length - 1) % fields.length;
            case Enter -> {
                if (field == 2) {
                    submitted = true;
                    onSubmit();
                } else if (field == 3) {
                    onDismiss.run();
                }
            }
        }
    }

    @Override
    public void draw() {
        super.draw();
        textGraphics.putString(2, 0, "Login");

        TerminalPosition fieldStartPosition = TerminalPosition.OFFSET_1x1.withRelativeColumn(1);
        defaultColor();
        textGraphics.putString(fieldStartPosition, "Username:");
        highlightSelectedField(0);
        textGraphics.putString(fieldStartPosition.withRelativeRow(1), "[");
        textGraphics.putString(fieldStartPosition.withRelative(1, 1), "%-32s".formatted(getUsername().replace(' ', '␣')));
        textGraphics.putString(fieldStartPosition.withRelative(32, 1), "]");

        fieldStartPosition = fieldStartPosition.withRelativeRow(3);
        defaultColor();
        textGraphics.putString(fieldStartPosition, "Password:");
        highlightSelectedField(1);
        textGraphics.putString(fieldStartPosition.withRelativeRow(1), "[");
        textGraphics.putString(fieldStartPosition.withRelative(1, 1), "%-32s".formatted("*".repeat(getPassword().length())));
        textGraphics.putString(fieldStartPosition.withRelative(32, 1), "]");

        fieldStartPosition = fieldStartPosition.withRelativeRow(3);
        defaultColor();
        highlightSelectedField(2);
        textGraphics.putString(fieldStartPosition, "<Submit>");

        fieldStartPosition = fieldStartPosition.withRelativeColumn(9);
        defaultColor();
        highlightSelectedField(3);
        textGraphics.putString(fieldStartPosition, "<Cancel>");
    }

    private void highlightSelectedField(int field) {
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

    protected abstract void onSubmit();

    protected abstract void onLoginSuccess();

    protected abstract void onLoginFailure(Throwable throwable);
}
