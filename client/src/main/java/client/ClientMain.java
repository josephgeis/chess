package client;

import chess.*;
import ui.EscapeSequences;
import ui.TerminalController;

import java.io.IOException;

import static java.lang.Thread.sleep;

/**
 * Initializes the terminal objects and has the event loop
 */
public class ClientMain {
    static String serverHost = "localhost";
    static int serverPort = 8080;

    static TerminalController terminalController;
    static EnhancedServerFacade serverFacade;
    static ClientState clientState = new ClientState();
    static ChessClient chessClient;

    public static void main(String[] args) {
        var piece = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN);

        System.out.println("♕ 240 Chess Client: " + piece);
        String clientName = System.getenv("CLIENT_NAME");

        if (args.length == 2) {
            serverHost = args[0];
            serverPort = Integer.parseInt(args[1]);
        } else if (args.length == 1 || args.length > 2) {
            System.out.println(EscapeSequences.SET_TEXT_BOLD +
                    EscapeSequences.SET_TEXT_COLOR_RED +
                    "Expecting 0 or 2 arguments: [host=localhost] [port=8080]" +
                    EscapeSequences.RESET_TEXT_COLOR +
                    EscapeSequences.RESET_TEXT_BOLD_FAINT);
            System.exit(1);
        }

        ChessHttpClient httpClient = new ChessHttpClient(serverHost, serverPort);
        ChessWsClient wsClient;
        try {
            wsClient = new ChessWsClient(serverHost, serverPort);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        serverFacade = new EnhancedServerFacade(httpClient, wsClient);
        chessClient = new ChessClient(serverFacade, clientState);
        terminalController = new TerminalController(chessClient, clientName);

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
