package ui;

import com.googlecode.lanterna.graphics.TextGraphics;
import ui.modals.LoginModal;
import ui.modals.MessageModal;
import ui.views.PreLoginView;
import ui.views.View;

import java.util.ArrayDeque;

public abstract class ViewPresenter {
    final TextGraphics textGraphics;
    ArrayDeque<View> viewStack = new ArrayDeque<View>();

    public ViewPresenter(TextGraphics textGraphics) {
        this.textGraphics = textGraphics;
    }

    View activeView() {
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
        viewStack.push(new LoginModal(textGraphics, this::unwind) {
            @Override
            protected void onSubmit() {
                // TODO: properly dispatch the TerminalController to do the login
                onLoginSuccess();
            }

            @Override
            protected void onLoginSuccess() {
                viewStack.pop();
                viewStack.push(
                        new MessageModal("Success", "Logged in as", textGraphics, onDismiss)
                );
            }

            @Override
            protected void onLoginFailure(Throwable throwable) {
                viewStack.pop();
                viewStack.push(
                        new MessageModal("Error", "Login failed", textGraphics, onDismiss)
                );
            }
        });
    }
}
