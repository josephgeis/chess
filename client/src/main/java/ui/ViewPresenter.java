package ui;

import client.ChessClient;
import com.googlecode.lanterna.graphics.TextGraphics;
import ui.modals.LoginModal;
import ui.modals.MessageModal;
import ui.views.LoggedInView;
import ui.views.PreLoginView;
import ui.views.View;

import java.util.ArrayDeque;

public abstract class ViewPresenter {
    final TextGraphics textGraphics;
    protected ArrayDeque<View> viewStack = new ArrayDeque<>();
    private final ChessClient chessClient;

    public ViewPresenter(TextGraphics textGraphics, ChessClient chessClient) {
        this.textGraphics = textGraphics;
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
        viewStack.push(new LoginModal(ViewPresenter.this.textGraphics, this::unwind) {
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
                        new LoggedInView(ViewPresenter.this.textGraphics, chessClient.getState().getLoggedInUser())
                );
                viewStack.push(
                        new MessageModal(
                                "Success",
                                "Logged in as " + getUsername(),
                                ViewPresenter.this.textGraphics,
                                onDismiss)
                );
            }

            @Override
            protected void onLoginFailure(Throwable throwable) {
                viewStack.pop();
                viewStack.push(
                        new MessageModal("Error", throwable.getMessage(), ViewPresenter.this.textGraphics, onDismiss)
                );
            }
        });
    }
}
