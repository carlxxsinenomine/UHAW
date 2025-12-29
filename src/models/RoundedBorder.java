package models;

import javax.swing.border.AbstractBorder;
import java.awt.*;

// Custom border with rounded corners for modern UI styling
// Extends AbstractBorder to provide smooth, rounded corner borders with customizable radius and color
public class RoundedBorder extends AbstractBorder {
    private final int radius;
    private final Color color;
    
    // Constructor creating a new rounded corner border
    // radius: corner radius in pixels, color: border color to apply
    public RoundedBorder(int radius, Color color) {
        this.radius = radius;
        this.color = color;
    }
    
    // Returns the corner radius
    public int getRadius() {
        return radius;
    }
    
    // Returns the border color
    public Color getColor() {
        return color;
    }
    
    // Paints the rounded border on the component using anti-aliasing for smooth edges
    @Override
    public void paintBorder(Component c, Graphics g, int x, int y, int w, int h) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(this.color);
        g2.drawRoundRect(x, y, w - 1, h - 1, radius, radius);
        g2.dispose();
    }
    
    // Returns the border insets (spacing around the component)
    @Override
    public Insets getBorderInsets(Component c) {
        return new Insets(2, 2, 2, 2);
    }
    
    /**
     * Updates the provided insets object with border insets.
     * @param c the component (unused)
     * @param i the insets object to update
     * @return the updated insets with 2 pixels on all sides
     */
    @Override
    public Insets getBorderInsets(Component c, Insets i) {
        i.left = i.right = i.bottom = i.top = 2;
        return i;
    }
}
