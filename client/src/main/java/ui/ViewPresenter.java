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
        viewStack.pop();
        if (activeView() != null) {
            activeView().onLoad();
        }
    }

    void performLoginSegue() {
        assert activeView() instanceof PreLoginView;
        activeView().onUnload();
        viewStack.push(new LoginModal(parentTextGraphics, this::unwind) {
            @Override
            protected void onSubmit() {
                chessClient.makeLoginRequest(getUsername(), getPassword())
                        .thenAccept(loginResponse -> onLoginSuccess())
                        .exceptionally(ex -> {
                            onLoginFailure(ex.getCause());
                            return null;
                        });
            }

            @Override
            protected void onLoginSuccess() {
                assert activeView() instanceof LoginModal;
                viewStack.pop();
                assert activeView() instanceof PreLoginView;
                viewStack.pop();
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
                viewStack.push(
                        new MessageModal(
                                "Success",
                                "Logged in as " + getUsername(),
                                parentTextGraphics,
                                onDismiss)
                );
            }

            @Override
            protected void onLoginFailure(Throwable throwable) {
                viewStack.pop();
                viewStack.push(
                        new MessageModal("Login Failed", throwable.getMessage(), parentTextGraphics, onDismiss)
                );
            }
        });
    }

    void performLogoutSegue() {
        assert activeView() instanceof LoggedInView;
        activeView().onUnload();
        viewStack.pop();
        viewStack.push(
                new PreLoginView(parentTextGraphics, this::performLoginSegue)
        );
        activeView().onLoad();

        chessClient.makeLogoutRequest().exceptionally(throwable -> {
            activeView().onUnload();
            viewStack.push(
                    new MessageModal("Logout Failed", throwable.getCause().getMessage(), parentTextGraphics, this::unwind)
            );
            return null;
        });
    }

    void performListGamesSegue() {
        assert activeView() instanceof LoggedInView;
        activeView().onUnload();
        viewStack.push(
                new ListGamesView(parentTextGraphics, this::unwind)
        );
        activeView().onLoad();
    }
}
