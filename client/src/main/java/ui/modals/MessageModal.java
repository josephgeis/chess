package ui.modals;

import com.googlecode.lanterna.SGR;
import com.googlecode.lanterna.TerminalPosition;
import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.graphics.TextGraphics;
import ui.TerminalController;

import java.util.Arrays;

public class MessageModal extends Modal {

    String title;
    String[] message;
    final String INSTRUCTIONS = "Press any key to continue...";

    public MessageModal(String title, String message, TextGraphics parentTextGraphics, TerminalController terminalController) {
        super(parentTextGraphics, terminalController);
        this.title = title;
        setMessage(message);
        setTextGraphics(parentTextGraphics);
    }

    public void setMessage(String message) {
        this.message = message.split("\n");
    }

    int calculateModalWidth() {
        int[] messageLinesLengths = new int[message.length + 2];
        messageLinesLengths[message.length] = title.length();
        messageLinesLengths[message.length + 1] = INSTRUCTIONS.length();
        for (int i = 0; i < message.length; i++) {
            messageLinesLengths[i] = message[i].length();
        }
        int maxLineLength = Arrays.stream(messageLinesLengths).max().orElse(0);
        return maxLineLength + 2;
    }

    @Override
    protected TerminalSize getSize() {
        if (message == null) {
            return new TerminalSize(0, 0);
        }

        return new TerminalSize(
                calculateModalWidth(),
                message.length + 4
        );
    }

    @Override
    public void draw() {
        super.draw();
        textGraphics.putString(TerminalPosition.TOP_LEFT_CORNER.withRelativeColumn(1), title, SGR.BOLD);
        for (int i = 0; i < message.length; i++) {
            textGraphics.putString(TerminalPosition.OFFSET_1x1.withRelativeRow(i), message[i]);
        }
        textGraphics.putString(TerminalPosition.OFFSET_1x1.withRelativeRow(message.length + 1), INSTRUCTIONS, SGR.ITALIC);
    }
}
