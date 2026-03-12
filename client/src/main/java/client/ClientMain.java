package client;

import chess.*;
import com.googlecode.lanterna.TerminalPosition;
import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.terminal.DefaultTerminalFactory;
import com.googlecode.lanterna.terminal.Terminal;
import ui.MainMenuView;
import ui.View;

import java.io.IOException;
import java.util.ArrayDeque;
import java.util.Stack;


public class ClientMain {
    static ArrayDeque<View> viewStack = new ArrayDeque<>();

    public static void main(String[] args) {
        var piece = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN);
        System.out.println("♕ 240 Chess Client: " + piece);

        DefaultTerminalFactory defaultTerminalFactory = new DefaultTerminalFactory();
        View currentView = null;
        try (Terminal terminal = defaultTerminalFactory.createTerminal()) {
            terminal.enterPrivateMode();
            terminal.clearScreen();
            terminal.setCursorVisible(false);

            pushView(new MainMenuView(terminal) {
                protected void handleLogin() throws IOException {
                    pushView(new MainMenuView(terminal) {
                        @Override
                        protected void initTerminal() throws IOException {
                            textGraphics.setBackgroundColor(TextColor.ANSI.GREEN);
                            textGraphics.fillRectangle(
                                    TerminalPosition.TOP_LEFT_CORNER,
                                    new TerminalSize(98, 32),
                                    ' '
                            );
                        }

                        @Override
                        protected void handleQuit() throws IOException {
                            popView();
                        }
                    });
                }
            });

            do {
                currentView = viewStack.getLast();
                currentView.loop();
            } while (true);

        } catch (StopLoop ignored) { } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            try {
                if (!viewStack.isEmpty()) {
                    currentView = viewStack.getLast();
                    currentView.unmount();
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    static void pushView(View view) throws IOException {
        if(!viewStack.isEmpty()) {
            viewStack.getLast().unmount();
        }
        viewStack.push(view);
        view.mount();
    }

    static void popView() throws IOException {
        viewStack.getLast().unmount();
        viewStack.pop();
        if(!viewStack.isEmpty()) {
            viewStack.getLast().mount();
        } else {
            throw new StopLoop();
        }
    }
}
