package ui;

import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.graphics.TextGraphics;
import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.terminal.Terminal;
import com.googlecode.lanterna.terminal.TerminalResizeListener;

import java.io.IOException;
import java.util.concurrent.Callable;

public abstract class View {
    Terminal terminal;
    TerminalResizeListener resizeListener;
    protected TextGraphics textGraphics;

    protected View(Terminal terminal) {
        this.terminal = terminal;
    }

    protected abstract void drawFrame() throws IOException;
    protected abstract Callable<Void> getKeyStrokeHandler(KeyStroke keyStroke);

    private void processKeyStroke(KeyStroke keyStroke) throws Exception {
        if (keyStroke == null) {
            return;
        }

        Callable<Void> handler = getKeyStrokeHandler(keyStroke);
        if (handler != null) {
            handler.call();
        }
    }

    public void loop() throws Exception {
        drawFrame();
        terminal.flush();

        KeyStroke keyStroke = terminal.pollInput();
        processKeyStroke(keyStroke);
    }

    public void mount() throws IOException {
        textGraphics = terminal.newTextGraphics();
        resizeListener = new TerminalResizeListener() {
            @Override
            public void onResized(Terminal terminal, TerminalSize terminalSize) {
                try {
                    View.this.onResized(terminal, terminalSize);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        };
        terminal.addResizeListener(resizeListener);
    }
    void onResized(Terminal terminal, TerminalSize terminalSize) throws IOException { };

    public void unmount() throws IOException {
        terminal.removeResizeListener(resizeListener);
    }
}
