package ui;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import model.User;

public class Main {
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
        } catch (Exception ignored) {
        }
        SwingUtilities.invokeLater(Main::showLoginThenApp);
    }
    
    private static void showLoginThenApp() {
        LoginDialog login = new LoginDialog();
        login.setVisible(true);

        User user = login.getAuthenticatedUser();
        if (user == null) {
            System.exit(0);
            return;
        }

        MainFrame frame = new MainFrame(user, Main::showLoginThenApp);
        frame.setVisible(true);
    }
}
