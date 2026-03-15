  package ui.modals;

import com.googlecode.lanterna.TerminalPosition;
import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.graphics.TextGraphics;
import com.googlecode.lanterna.input.KeyStroke;
import ui.TerminalController;

  public abstract class LoginModal extends Modal {
    int field = 0;

    String[] fields = {"", "", null, null};

    public LoginModal(TextGraphics parentTextGraphics, TerminalController terminalController) {
        super(parentTextGraphics, terminalController);
    }

    @Override
    protected TerminalSize getSize() {
        return new TerminalSize(37, 9);
    }

    @Override
    public void onKeyStroke(KeyStroke keyStroke) {
        super.onKeyStroke(keyStroke);
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
                if (field == 3) {
                    close();
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
        textGraphics.putString(fieldStartPosition.withRelativeRow(1), "[" );
        textGraphics.putString(fieldStartPosition.withRelative(1, 1), "%-32s".formatted(fields[0].replace(' ', '␣')));
        textGraphics.putString(fieldStartPosition.withRelative(32, 1), "]" );

        fieldStartPosition = fieldStartPosition.withRelativeRow(3);
        defaultColor();
        textGraphics.putString(fieldStartPosition, "Password:");
        highlightSelectedField(1);
        textGraphics.putString(fieldStartPosition.withRelativeRow(1), "[" );
        textGraphics.putString(fieldStartPosition.withRelative(1, 1), "%-32s".formatted("*".repeat(fields[1].length())));
        textGraphics.putString(fieldStartPosition.withRelative(32, 1), "]" );

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
              textGraphics.setBackgroundColor(TextColor.ANSI.YELLOW);
              textGraphics.setForegroundColor(TextColor.ANSI.WHITE_BRIGHT);
          } else {
              textGraphics.setBackgroundColor(TextColor.ANSI.WHITE);
              textGraphics.setForegroundColor(TextColor.ANSI.BLACK);
          }
        }
  }
