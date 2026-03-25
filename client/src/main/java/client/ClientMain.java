package client;

import chess.*;
import ui.TerminalController;

import java.io.IOException;

import static java.lang.Thread.sleep;

/**
 * Initializes the terminal objects and has the event loop
 */
public class ClientMain {
    static final String SERVER_HOST = "localhost";
    static final int SERVER_PORT = 8080;

    static TerminalController terminalController;
    static EnhancedServerFacade serverFacade;
    static ClientState clientState = new ClientState();
    static ChessClient chessClient;

    public static void main(String[] args) {
        var piece = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN);

        System.out.println("♕ 240 Chess Client: " + piece);

        ChessHttpClient httpClient = new ChessHttpClient(SERVER_HOST, SERVER_PORT);
        ChessWsClient wsClient;
        try {
            wsClient = new ChessWsClient(SERVER_HOST, SERVER_PORT);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        serverFacade = new EnhancedServerFacade(httpClient, wsClient);
        chessClient = new ChessClient(serverFacade, clientState);
        terminalController = new TerminalController(chessClient);

        RuntimeException exitException = null;
        try {
            init();
            while (!clientState.quit) {
                eventLoop();
                sleep(10);
            }
        } catch (Exception e) {
            try {
                terminalController.displayUnhandledException(e);
                exitException = new RuntimeException(e);
            } catch (IOException ex) {
                RuntimeException runtimeException = new RuntimeException(ex);
                runtimeException.addSuppressed(e);
                exitException = runtimeException;
            }
        } finally {
            try {
                terminalController.tearDown();
            } catch (IOException e) {
                if (exitException != null) {
                    RuntimeException tearDownException = new RuntimeException("An exception was thrown while closing.", e);
                    tearDownException.addSuppressed(exitException.getCause());
                    for (Throwable throwable : exitException.getSuppressed()) {
                        tearDownException.addSuppressed(throwable);
                    }
                    exitException = tearDownException;
                }
            }
        }

        if (exitException != null) {
            throw exitException;
        }
    }

    private static void init() throws IOException {
        terminalController.init();
    }

    static void eventLoop() throws Exception {
        terminalController.eventLoop();
    }
}
