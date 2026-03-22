package ui;

import client.ChessClient;
import com.googlecode.lanterna.graphics.TextGraphics;
import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.input.KeyType;
import com.googlecode.lanterna.screen.Screen;
import com.googlecode.lanterna.terminal.DefaultTerminalFactory;
import ui.menubar.MenuBar;
import ui.views.PreLoginView;
import ui.views.View;

import java.io.IOException;
import java.util.Set;

import static com.googlecode.lanterna.input.KeyType.*;

/**
 * Responsible for drawing the parts of the screen and receives keyboard input.
 * Screen parts:
 * 1. Field (where the chess board, games list are drawn [depending on context])
 * 2. Menu bar
 */
public class TerminalController {
    private final ChessClient chessClient;

    DefaultTerminalFactory defaultTerminalFactory = new DefaultTerminalFactory();
    Screen screen;
    TextGraphics textGraphics;
    ViewPresenter viewPresenter;

    MenuBar menuBar;

    public TerminalController(ChessClient chessClient) {
        this.chessClient = chessClient;
    }

    public void eventLoop() throws IOException {
        View view = viewPresenter.activeView();
        if (view == null) {
            chessClient.quitProgram();
            return;
        }

        if (screen.doResizeIfNecessary() != null) {
            screen.clear();
            menuBar.setTextGraphics(textGraphics);
            view.setTextGraphics(textGraphics);
        }

        view.draw();
        menuBar.setMenuItems(view.getMenuItems());
        menuBar.draw();
        screen.refresh();

        KeyStroke keyStroke = screen.pollInput();
        if (keyStroke != null) {
            handleInput(keyStroke);
        }
    }

    protected void handleInput(KeyStroke keyStroke) {
        Set<KeyType> fnKeys = Set.of(new KeyType[]{F1, F2, F3, F4, F5, F6});

        KeyType keyType = keyStroke.getKeyType();

        if (fnKeys.contains(keyType)) {
            Runnable callback = menuBar.getCallbackForMenuKey(keyType);
            if (callback != null) {
                callback.run();
            }
        } else if (keyStroke.getKeyType() == EOF) {
            chessClient.quitProgram();
        } else {
            viewPresenter.activeView().onKeyStroke(keyStroke);
        }
    }

    public void init() throws IOException {
        screen = defaultTerminalFactory.createScreen();
        textGraphics = screen.newTextGraphics();
        viewPresenter = new ViewPresenter(textGraphics, chessClient) {
            @Override
            void entryPoint() {
                viewStack.push(new PreLoginView(textGraphics) {
                    @Override
                    public void showLoginModal() {
                        performLoginSegue();
                    }

                    @Override
                    public void showRegisterModal() {
                        performRegisterSegue();
                    }

                    @Override
                    public void onQuit() {
                        chessClient.quitProgram();
                    }
                });
                activeView().onLoad();
            }
        };

        menuBar = new MenuBar(textGraphics);
        viewPresenter.entryPoint();

        screen.startScreen();
    }

    public void tearDown() throws IOException {
        if (screen != null) {
            screen.stopScreen();
        }
    }

    public void displayUnhandledException(Throwable throwable) throws IOException {
        viewPresenter.displayUnhandledException(throwable);
        eventLoop();
        screen.readInput();
    }
}
