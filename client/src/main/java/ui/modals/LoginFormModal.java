package ui.modals;

import com.googlecode.lanterna.TerminalPosition;
import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.graphics.TextGraphics;
import com.googlecode.lanterna.input.KeyStroke;

public abstract class LoginFormModal extends FormModal {

    public LoginFormModal(TextGraphics parentTextGraphics, Runnable onDismiss) {
        super(parentTextGraphics, new String[]{"", "", null, null}, onDismiss);
    }

    @Override
    protected TerminalSize getSize() {
        return new TerminalSize(38, 9);
    }

    public String getUsername() {
        return fields[0];
    }

    public String getPassword() {
        return fields[1];
    }

    @Override
    public void handleKeyStroke(KeyStroke keyStroke) {
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
        drawField(0, fieldStartPosition, "Username", getUsername());

        fieldStartPosition = fieldStartPosition.withRelativeRow(3);
        drawField(1, fieldStartPosition, "Password", getPassword(), FieldFormat.PASSWORD);

        fieldStartPosition = fieldStartPosition.withRelativeRow(3);
        defaultColor();
        highlightSelectedField(2);
        textGraphics.putString(fieldStartPosition, "<Submit>");

        fieldStartPosition = fieldStartPosition.withRelativeColumn(9);
        defaultColor();
        highlightSelectedField(3);
        textGraphics.putString(fieldStartPosition, "<Cancel>");
    }

    protected abstract void onSubmit();
}
