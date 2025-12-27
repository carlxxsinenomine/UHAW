package main;

import java.awt.*;

/**
 * Central constants class for colors, fonts, and other UI-related constants.
 * This class consolidates all design constants used across the application.
 */
public class AppConstants {

    // ============================================
    // PRIMARY COLORS
    // ============================================
    public static final Color PRIMARY_BLUE = new Color(130, 170, 255);
    public static final Color DARK_PRIMARY_BLUE = new Color(100, 140, 225);
    
    // ============================================
    // BACKGROUND COLORS
    // ============================================
    public static final Color BG_LIGHT_GRAY = new Color(245, 247, 250);
    public static final Color BG_WHITE = Color.WHITE;
    public static final Color BG_GRAY = new Color(220, 220, 220);
    public static final Color BG_MEDIUM_GRAY = new Color(200, 200, 200);
    
    // ============================================
    // TEXT COLORS
    // ============================================
    public static final Color TEXT_DARK = new Color(50, 50, 50);
    public static final Color TEXT_PRIMARY = new Color(50, 50, 50);
    public static final Color TEXT_SECONDARY = new Color(100, 100, 100);
    public static final Color TEXT_LIGHT = new Color(230, 240, 255);
    
    // ============================================
    // ACCENT COLORS
    // ============================================
    public static final Color ACCENT_GREEN = new Color(34, 139, 34);
    public static final Color ACCENT_RED = new Color(220, 53, 69);
    public static final Color ACCENT_BLUE = new Color(66, 133, 244);
    public static final Color ACCENT_ORANGE = new Color(255, 160, 0);
    public static final Color ACCENT_GREEN_BRIGHT = new Color(15, 157, 88);
    
    // ============================================
    // BORDER COLORS
    // ============================================
    public static final Color BORDER_LIGHT_GRAY = new Color(200, 200, 200);
    
    // ============================================
    // FONTS
    // ============================================
    
    // Title Fonts
    public static final Font FONT_TITLE_LARGE = new Font("Arial", Font.BOLD, 42);
    public static final Font FONT_TITLE_XL = new Font("Arial", Font.BOLD, 28);
    public static final Font FONT_TITLE_LARGE_REGULAR = new Font("Arial", Font.BOLD, 24);
    public static final Font FONT_SUBTITLE = new Font("Arial", Font.ITALIC, 24);
    
    // Body Fonts
    public static final Font FONT_BODY_LARGE = new Font("Arial", Font.PLAIN, 18);
    public static final Font FONT_BODY_REGULAR = new Font("Arial", Font.PLAIN, 14);
    public static final Font FONT_BODY_SMALL = new Font("Arial", Font.PLAIN, 13);
    public static final Font FONT_BODY_ITALIC = new Font("Arial", Font.ITALIC, 14);
    
    // Label Fonts
    public static final Font FONT_LABEL_BOLD = new Font("Arial", Font.BOLD, 14);
    public static final Font FONT_LABEL_REGULAR = new Font("Arial", Font.PLAIN, 12);
    public static final Font FONT_LABEL_BOLD_SMALL = new Font("Arial", Font.BOLD, 12);
    
    // ============================================
    // SPACING & SIZING
    // ============================================
    public static final int PADDING_LARGE = 40;
    public static final int PADDING_MEDIUM = 20;
    public static final int PADDING_SMALL = 10;
    public static final int PADDING_TINY = 5;
    
    public static final int CORNER_RADIUS = 15;
    public static final int BORDER_WIDTH = 1;
    public static final int BORDER_WIDTH_THICK = 2;
    
    // ============================================
    // PRIVATE CONSTRUCTOR - Utility class, no instances
    // ============================================
    private AppConstants() {
        throw new AssertionError("Cannot instantiate AppConstants utility class");
    }
}
