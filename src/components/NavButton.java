package components;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * NavButton represents a single navigation button in the navigation bar.
 * Features hover effects and click handling.
 *
 * @author Your Name
 * @version 1.0
 */
public class NavButton extends JButton {

    /**
     * Constructor that creates a navigation button.
     *
     * @param text the text to display on the button
     */
    public NavButton(String text) {
        setText(text);
        setFont(new Font("Arial", Font.BOLD, 16));
        setForeground(Color.BLACK);
        setBackground(new Color(130, 170, 255)); // Same as navbar background
        setFocusPainted(false);
        setBorderPainted(false);
        setContentAreaFilled(false);
        setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Add hover effect
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                setForeground(new Color(50, 50, 150));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                setForeground(Color.BLACK);
            }
        });
    }
}