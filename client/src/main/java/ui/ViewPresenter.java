package ui;

import client.ChessClient;
import com.googlecode.lanterna.TerminalPosition;
import com.googlecode.lanterna.graphics.TextGraphics;
import response.GameListing;
import ui.modals.*;
import ui.views.ListGamesView;
import ui.views.LoggedInView;
import ui.views.PreLoginView;
import ui.views.View;

import java.util.ArrayDeque;
import java.util.ArrayList;

public abstract class ViewPresenter {
    final TextGraphics parentTextGraphics;
    protected ArrayDeque<View> viewStack = new ArrayDeque<>();
    private final ChessClient chessClient;

    public ViewPresenter(TextGraphics textGraphics, ChessClient chessClient) {
        this.parentTextGraphics = textGraphics;
        this.chessClient = chessClient;
    }

    public View activeView() {
        return viewStack.peek();
    }

    abstract void entryPoint();

    void unwind() {
        assert !viewStack.isEmpty();
        activeView().onUnload();
        viewStack.pop();
        if (activeView() != null) {
            activeView().onLoad();
        }
    }

    void loadView(View view) {
        if (!viewStack.isEmpty()) {
            activeView().onUnload();
        }
        viewStack.push(view);
        view.onLoad();
    }

    void replaceView(View newView) {
        unwind();
        loadView(newView);
    }

    void performLoginSegue() {
        assert activeView() instanceof PreLoginView;
        loadView(new LoginFormModal(parentTextGraphics, this::unwind) {
            @Override
            protected void onSubmit() {
                chessClient.makeLoginRequest(getUsername(), getPassword())
                        .thenAccept(response -> performCompleteLoginSegue(response.username()))
                        .exceptionally(ex -> {
                            performFailedFormRequestSegue(ex.getCause());
                            return null;
                        });
            }
        });
    }

    void performRegisterSegue() {
        assert activeView() instanceof PreLoginView;
        loadView(new RegisterFormModal(parentTextGraphics, this::unwind) {
            @Override
            protected void onSubmit() {
                chessClient.makeRegisterRequest(getUsername(), getPassword(), getEmail())
                        .thenAccept(response -> performCompleteLoginSegue(response.username()))
                        .exceptionally(ex -> {
                            performFailedFormRequestSegue(ex.getCause());
                            return null;
                        });
            }
        });
    }

    void performCompleteLoginSegue(String username) {
        assert activeView() instanceof LoginFormModal ||
                activeView() instanceof RegisterFormModal;
        unwind();
        viewStack.push(
                new LoggedInView(parentTextGraphics, chessClient.getState().getLoggedInUser()) {
                    @Override
                    protected void onLogout() {
                        performLogoutSegue();
                    }

                    @Override
                    protected void onListGames() {
                        performListGamesSegue(ListGamesView.ViewMode.LIST_ONLY);
                    }

                    @Override
                    protected void onCreateGame() {
                        performCreateGameSegue();
                    }
                }
        );
        loadView(
                new MessageModal(
                        "Success",
                        "Logged in as " + username,
                        parentTextGraphics,
                        this::unwind)
        );
    }

    void performFailedFormRequestSegue(Throwable throwable) {
        assert activeView() instanceof FormModal;
        replaceView(
                new MessageModal("Error", throwable.getMessage(), parentTextGraphics, this::unwind)
        );
    }

    void performLogoutSegue() {
        assert activeView() instanceof LoggedInView;
        unwind();

        chessClient.makeLogoutRequest().exceptionally(throwable -> {
            loadView(
                    new MessageModal("Error", throwable.getCause().getMessage(), parentTextGraphics, this::unwind)
            );
            return null;
        });
    }

    void performListGamesSegue(ListGamesView.ViewMode viewMode) {
        assert activeView() instanceof LoggedInView;
        loadView(
                new ListGamesView(parentTextGraphics, viewMode, this::unwind) {
                    boolean isLoading = false;

                    @Override
                    protected void reloadGames() {
                        if (isLoading) {
                            return;
                        }

                        isLoading = true;
                        chessClient.makeListGamesRequest()
                                .thenAccept(response -> {
                                    setGames(new ArrayList<>(response.games()));
                                    setCursor(0);
                                    isLoading = false;
                                });
                    }

                    @Override
                    protected void drawGames(TerminalPosition startPosition) {
                        if (isLoading) {
                            textGraphics.putString(startPosition, "Loading...");
                        } else {
                            super.drawGames(startPosition);
                        }
                    }
                }
        );
    }

    void performCreateGameSegue() {
        assert activeView() instanceof LoggedInView;
        loadView(
                new CreateGameModal(parentTextGraphics, this::unwind) {
                    @Override
                    protected void onSubmit() {
                        chessClient.makeCreateGameRequest(getGameName())
                                .thenAccept(response -> performCompleteCreateGameSegue(getGameName()))
                                .exceptionally(ex -> {
                                    performFailedFormRequestSegue(ex.getCause());
                                    return null;
                                });
                    }
                }
        );
    }

    private void performCompleteCreateGameSegue(String gameName) {
        assert activeView() instanceof CreateGameModal;
        replaceView(new MessageModal(
                "Success",
                "Created new game: " + gameName,
                parentTextGraphics,
                this::unwind));
    }
}
