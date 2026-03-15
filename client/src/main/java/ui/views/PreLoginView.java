 package ui.views;

import com.googlecode.lanterna.SGR;
import com.googlecode.lanterna.TerminalPosition;
import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.graphics.TextGraphics;
import ui.EventPublisher;
import ui.TerminalController;
import ui.menubar.MenuItems;
import ui.modals.LoginModal;

import java.util.EnumSet;

 public class PreLoginView extends View {

     boolean showHelp = false;

     public PreLoginView(TextGraphics parentTextGraphics, TerminalController terminalController) {
         super(parentTextGraphics, terminalController);
         menuItems = MenuItems.NOT_LOGGED_IN;
     }

     @Override
    public void draw() {
        textGraphics.setBackgroundColor(TextColor.ANSI.BLUE);
        textGraphics.setForegroundColor(TextColor.ANSI.WHITE_BRIGHT);
        textGraphics.fillRectangle(
                TerminalPosition.TOP_LEFT_CORNER,
                textGraphics.getSize(),
                ' '
        );

        textGraphics.putString(TerminalPosition.OFFSET_1x1, "240 Chess Client");
        textGraphics.putString(new TerminalPosition(2, 2), "Not Logged In");

        if (showHelp) {
            String[] fnKeys = {"F1", "F2", "F5", "F6"};
            String[] helpStrings = {
                    "Log in as an existing user",
                    "Register as a new user",
                    "Toggle this help message",
                    "Quit the program"
            };

            TerminalPosition helpPosition = new TerminalPosition(2, 4);
            for (int i = 0; i < helpStrings.length; i++) {
                textGraphics.setModifiers(EnumSet.of(SGR.BOLD));
                textGraphics.putString(helpPosition.withRelativeRow(i), fnKeys[i]);

                textGraphics.clearModifiers();
                textGraphics.putString(helpPosition.withRelative(fnKeys[i].length() + 1, i), helpStrings[i]);
            }
        }
    }

     @Override
     public void onLoad() {
         super.onLoad();
         showHelp = false;

         registerEventHandler(EventPublisher.EventType.SHOW_HELP, this::showHelpScreen);
         registerEventHandler(EventPublisher.EventType.LOG_IN, this::showLoginModal);
     }

     public void showHelpScreen() {
        showHelp = !showHelp;
     }
     public void showLoginModal() {
         LoginModal loginModal = new LoginModal(textGraphics, terminalController) {
             @Override
             public void onClose() {
                 terminalController.popView();
             }
         };
         terminalController.pushView(loginModal);
     }
 }
