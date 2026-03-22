package ui;

import client.ChessClient;
import com.googlecode.lanterna.graphics.TextGraphics;
import ui.modals.LoginModal;
import ui.modals.MessageModal;
import ui.views.ListGamesView;
import ui.views.LoggedInView;
import ui.views.PreLoginView;
import ui.views.View;

import java.util.ArrayDeque;

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
        loadView(new LoginModal(parentTextGraphics, this::unwind) {
            @Override
            protected void onSubmit() {
                chessClient.makeLoginRequest(getUsername(), getPassword())
                        .thenAccept(loginResponse -> performCompleteLoginSegue(loginResponse.username()))
                        .exceptionally(ex -> {
                            performFailedLoginSegue(ex.getCause());
                            return null;
                        });
            }
        });
    }

    void performCompleteLoginSegue(String username) {
        assert activeView() instanceof LoginModal;
        unwind();
        assert activeView() instanceof PreLoginView;
        unwind();
        viewStack.push(
                new LoggedInView(parentTextGraphics, chessClient.getState().getLoggedInUser()) {
                    @Override
                    protected void onLogout() {
                        super.onLogout();
                        performLogoutSegue();
                    }

                    @Override
                    protected void onListGames() {
                        super.onListGames();
                        performListGamesSegue();
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

    void performFailedLoginSegue(Throwable throwable) {
        assert activeView() instanceof LoginModal;
        replaceView(
                new MessageModal("Login Failed", throwable.getMessage(), parentTextGraphics, this::unwind)
        );
    }

    void performLogoutSegue() {
        assert activeView() instanceof LoggedInView;
        replaceView(
                new PreLoginView(parentTextGraphics, this::performLoginSegue)
        );

        chessClient.makeLogoutRequest().exceptionally(throwable -> {
            loadView(
                    new MessageModal("Logout Failed", throwable.getCause().getMessage(), parentTextGraphics, this::unwind)
            );
            return null;
        });
    }

    void performListGamesSegue() {
        assert activeView() instanceof LoggedInView;
        loadView(
                new ListGamesView(parentTextGraphics, this::unwind)
        );
    }
}
