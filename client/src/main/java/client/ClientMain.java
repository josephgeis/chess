package client;

import chess.*;
import ui.EventPublisher;
import ui.TerminalController;

import java.io.IOException;

import static java.lang.Thread.sleep;

/**
 * Initializes the terminal objects and has the event loop
 */
public class ClientMain {
    static TerminalController terminalController;
    static EventPublisher eventPublisher = EventPublisher.getInstance();
    static ServerFacade serverFacade = new ServerFacade("localhost", 8080);
    static ClientState clientState = new ClientState();
    static ChessClient chessClient;

    public static void main(String[] args) {
        var piece = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN);

        System.out.println("♕ 240 Chess Client: " + piece);

        chessClient = new ChessClient(serverFacade, clientState);
        terminalController = new TerminalController(chessClient);

        try {
            init();
            while (!clientState.quit) {
                eventLoop();
                sleep(10);
            }
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

    private static void init() throws IOException {
        terminalController.init();
        eventPublisher.registerEventHandler(EventPublisher.EventType.QUIT_PROGRAM, ClientMain::quitProgram);
    }

    static void eventLoop() throws Exception {
        terminalController.eventLoop();
    }

    static void quitProgram() {
        clientState.quitProgram();
    }

}
