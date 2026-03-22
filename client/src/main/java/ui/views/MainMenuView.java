package ui.views;

import com.googlecode.lanterna.SGR;
import com.googlecode.lanterna.TerminalPosition;
import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.graphics.TextGraphics;
import ui.EventPublisher;

import java.util.EnumSet;

public abstract class MainMenuView extends View {
    boolean showHelp = false;

    String[] fnKeys = {};
    String[] helpStrings = {};
    String tagline = "";

    TextColor backgroundColor = TextColor.ANSI.DEFAULT;
    TextColor foregroundColor = TextColor.ANSI.DEFAULT;

    MainMenuView(TextGraphics parentTextGraphics) {
        super(parentTextGraphics);
    }

    @Override
    public void draw() {
        textGraphics.setBackgroundColor(backgroundColor);
        textGraphics.setForegroundColor(foregroundColor);
        textGraphics.fillRectangle(
                TerminalPosition.TOP_LEFT_CORNER,
                textGraphics.getSize(),
                ' '
        );

        textGraphics.putString(TerminalPosition.OFFSET_1x1, "240 Chess Client");
        textGraphics.putString(new TerminalPosition(2, 2), tagline);

        if (showHelp) {
            TerminalPosition helpPosition = new TerminalPosition(2, 4);
            for (int i = 0; i < helpStrings.length; i++) {
                textGraphics.setModifiers(EnumSet.of(SGR.BOLD));
                textGraphics.putString(helpPosition.withRelativeRow(i), fnKeys[i]);

                textGraphics.clearModifiers();
                textGraphics.putString(helpPosition.withRelative(fnKeys[i].length() + 1, i), helpStrings[i]);
            }
        }
    }

    @Override
    public void onLoad() {
        super.onLoad();
        showHelp = false;

        registerEventHandler(EventPublisher.EventType.SHOW_HELP, this::showHelpScreen);
    }

    public void showHelpScreen() {
        showHelp = !showHelp;
    }
}
