package client;

import chess.*;
import ui.TerminalController;

import java.io.IOException;

/**
 * Initializes the terminal objects and has the event loop
 */
public class ClientMain {
    static TerminalController terminalController;

    public static void main(String[] args) {
        var piece = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN);
        System.out.println("♕ 240 Chess Client: " + piece);

        terminalController = new TerminalController();

        try {
            terminalController.init();
            while (eventLoop() == 0) ;
        } catch (Exception e) {
            throw new RuntimeException("An unhandled exception was thrown.", e);
        } finally {
            try {
                terminalController.tearDown();
            } catch (IOException e) {
                throw new RuntimeException("An exception was thrown while closing.", e);
            }
        }
    }

    static int eventLoop() throws Exception {
        terminalController.eventLoop();
        return 0;
    }
}
