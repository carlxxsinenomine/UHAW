package models;

import javax.swing.border.AbstractBorder;
import java.awt.*;

/**
 * RoundedBorder - A custom border with rounded corners for modern UI styling.
 * 
 * This class extends Swing's AbstractBorder to provide smooth, rounded corner borders
 * with customizable radius and color. It uses anti-aliasing for high-quality rendering,
 * ensuring the rounded edges look smooth on any resolution. This is essential for
 * creating a modern, polished appearance in the UHAW application.
 * 
 * Features:
 * - Customizable corner radius: Adjust how rounded the corners are
 * - Custom colors: Match the border color to your application's design system
 * - Anti-aliased rendering: Smooth edges without jagged appearance
 * - Consistent insets: Maintains a 2-pixel border spacing
 * 
 * Usage:
 * Apply this border to any Swing component using setBorder(new RoundedBorder(...)).
 * Commonly used for panels, buttons, and input fields to maintain design consistency.
 */
public class RoundedBorder extends AbstractBorder {
    private final int radius;
    private final Color color;
    
    /**
     * Creates a new rounded corner border with specified styling.
     *
     * @param radius the corner radius in pixels (larger values = more rounded)
     * @param color the border color to apply
     */
    public RoundedBorder(int radius, Color color) {
        this.radius = radius;
        this.color = color;
    }
    
    /**
     * Gets the corner radius.
     * @return the radius in pixels
     */
    public int getRadius() {
        return radius;
    }
    
    /**
     * Gets the border color.
     * @return the color of this border
     */
    public Color getColor() {
        return color;
    }
    
    /**
     * Paints the rounded border on the component.
     * 
     * This method uses Graphics2D with anti-aliasing enabled to draw smooth,
     * high-quality rounded corners.
     */
    @Override
    public void paintBorder(Component c, Graphics g, int x, int y, int w, int h) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(this.color);
        g2.drawRoundRect(x, y, w - 1, h - 1, radius, radius);
        g2.dispose();
    }
    
    /**
     * Returns the border insets (spacing around the component).
     * @return insets of 2 pixels on all sides
     */
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
