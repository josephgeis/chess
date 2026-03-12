package ui;

import client.StopLoop;
import com.googlecode.lanterna.SGR;
import com.googlecode.lanterna.TerminalPosition;
import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.terminal.Terminal;

import java.io.IOException;
import java.util.EnumSet;
import java.util.concurrent.Callable;

public class MainMenuView extends View {

    int choice = 0;
    String[] choices = {"Log In", "Exit"};


    public MainMenuView(Terminal terminal) {
        super(terminal);
    }

    @Override
    public void mount() throws IOException {
        super.mount();
        initTerminal();
    }

    @Override
    public void unmount() throws IOException {
        super.unmount();
    }

    void onResized(Terminal terminal, TerminalSize terminalSize) throws IOException {
        initTerminal();
    }

    protected void initTerminal() throws IOException {
        textGraphics.setBackgroundColor(TextColor.ANSI.BLUE);
        textGraphics.setForegroundColor(TextColor.ANSI.WHITE);
        textGraphics.fillRectangle(
                TerminalPosition.TOP_LEFT_CORNER,
                new TerminalSize(98, 32),
                ' '
        );
    }

    @Override
    protected void drawFrame() throws IOException {
        textGraphics.setBackgroundColor(TextColor.ANSI.BLUE);
        textGraphics.putString(4, 4, "Welcome to CS 240 chess", SGR.BOLD);

        for (int i = 0; i < choices.length; i++) {
            if (i == choice) {
                textGraphics.setBackgroundColor(TextColor.ANSI.YELLOW);
                textGraphics.setModifiers(EnumSet.of(SGR.BOLD));
            } else {
                textGraphics.setBackgroundColor(TextColor.ANSI.BLUE);
                textGraphics.clearModifiers();
            }
            textGraphics.putString(6, 6 + i, choices[i]);
        }
    }

    Void handleDown() {
        choice = (++choice) % choices.length;
        return null;
    }

    Void handleUp() {
        choice = (choices.length + (--choice)) % choices.length;
        return null;
    }

    Void handleReturn() throws StopLoop, IOException {
        switch (choice) {
            case 1 -> handleQuit();
            case 0 -> handleLogin();
        }
        return null;
    }

    protected void handleLogin() throws IOException { }
    protected void handleQuit() throws IOException {
        throw new StopLoop();
    }

    @Override
    protected Callable<Void> getKeyStrokeHandler(KeyStroke keyStroke) {
        return switch (keyStroke.getKeyType()) {
            case ArrowUp -> this::handleUp;
            case ArrowDown -> this::handleDown;
            case Enter -> this::handleReturn;
            default -> null;
        };
    }
}
