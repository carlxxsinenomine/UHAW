package screens;

import components.NavBarPanel;
import components.NavButton;

import javax.swing.*;
import java.awt.*;

public class UserScreen extends JFrame {
    public UserScreen() {
        setTitle("Modern Navigation Bar");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(860, 150);
        setLocationRelativeTo(null);

        JPanel navBar = new NavBarPanel();

        JPanel containerPanel = new JPanel(new BorderLayout());
        containerPanel.setBackground(Color.GRAY);
        containerPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        containerPanel.add(navBar, BorderLayout.NORTH);

        add(containerPanel);
        setVisible(true);
    }

    public static void main(String[] args) {
        new UserScreen();
    }
}
