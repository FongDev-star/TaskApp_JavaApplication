package ui;

import model.User;

import javax.swing.*;
import java.awt.*;

public class UserDashboard extends JPanel {
    public UserDashboard(User user) {
        setLayout(new BorderLayout());
        setBackground(UITheme.BACKGROUND);
        add(new TaskPanel(user.getId(), false), BorderLayout.CENTER);
    }
}
