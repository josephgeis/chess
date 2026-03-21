package ui;

import com.googlecode.lanterna.graphics.TextGraphics;
import ui.views.View;

import java.util.ArrayDeque;

public class ViewPresenter {
    final TerminalController terminalController;
    final TextGraphics textGraphics;
    ArrayDeque<View> viewStack = new ArrayDeque<View>();

    public ViewPresenter(TerminalController terminalController, TextGraphics textGraphics) {
        this.terminalController = terminalController;
        this.textGraphics = textGraphics;
    }

    View activeView() {
        return viewStack.peek();
    }

    public void pushView(View newView) {
        if (activeView() != null) {
            activeView().onUnload();
        }
        viewStack.push(newView);
        activeView().onLoad();
    }

    public <T extends View> void pushNewView(Class<T> viewClass) {
        T newView;
        try {
            newView = viewClass
                    .getConstructor(TextGraphics.class, TerminalController.class)
                    .newInstance(textGraphics, terminalController);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        pushView(newView);
    }

    public void popView() {
        activeView().onUnload();
        viewStack.pop();
        if (activeView() != null) {
            activeView().onLoad();
            activeView().setTextGraphics(terminalController.textGraphics);
        }
    }
}
