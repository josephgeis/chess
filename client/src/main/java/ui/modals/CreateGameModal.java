package ui.modals;

import com.googlecode.lanterna.TerminalPosition;
import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.graphics.TextGraphics;
import com.googlecode.lanterna.input.KeyStroke;

public abstract class CreateGameModal extends FormModal {

    public CreateGameModal(TextGraphics parentTextGraphics, Runnable onDismiss) {
        super(parentTextGraphics, new String[]{"", null, null}, onDismiss);
    }

    @Override
    protected TerminalSize getSize() {
        return new TerminalSize(38, 6);
    }

    public String getGameName() {
        return fields[0];
    }

    @Override
    public void handleKeyStroke(KeyStroke keyStroke) {
        super.handleKeyStroke(keyStroke);

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
                if (field == 1) {
                    submitted = true;
                    onSubmit();
                } else if (field == 2) {
                    onDismiss.run();
                }
            }
        }
    }

    @Override
    public void draw() {
        super.draw();
        textGraphics.putString(2, 0, "Create Game");

        TerminalPosition fieldStartPosition = TerminalPosition.OFFSET_1x1.withRelativeColumn(1);
        drawField(0, fieldStartPosition, "Name", getGameName());

        fieldStartPosition = fieldStartPosition.withRelativeRow(3);
        defaultColor();
        highlightSelectedField(1);
        textGraphics.putString(fieldStartPosition, "<Submit>");

        fieldStartPosition = fieldStartPosition.withRelativeColumn(9);
        defaultColor();
        highlightSelectedField(2);
        textGraphics.putString(fieldStartPosition, "<Cancel>");
    }

    protected abstract void onSubmit();
}
