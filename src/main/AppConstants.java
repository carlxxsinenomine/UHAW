package main;

import java.awt.*;

/**
 * AppConstants - Centralized UI Design Constants for the UHAW Application.
 * 
 * This utility class consolidates all UI-related constants used throughout the application,
 * including colors, fonts, and spacing values. By centralizing these constants, we ensure:
 * 
 * Design Benefits:
 * - Visual Consistency: All screens use the same color palette and font styles
 * - Maintainability: Updating a color or font requires changing only one location
 * - Scalability: Easy to implement themes, dark mode, or reskinning
 * - Reusability: Components can import and use constants without hard-coding values
 * 
 * Color Organization:
 * - Primary Colors: Main brand colors used throughout the app
 * - Background Colors: Panel and container backgrounds
 * - Text Colors: Different text hierarchy levels
 * - Accent Colors: Interactive elements, alerts, and highlights
 * - Border Colors: Dividers and outline borders
 * 
 * Font Organization:
 * - Title Fonts: Large headings and section titles
 * - Body Fonts: Regular text and content areas
 * - Label Fonts: UI labels, buttons, and control text
 * 
 * Spacing & Sizing:
 * - Padding Constants: Consistent internal spacing within components
 * - Border Radius: Rounded corners for modern appearance
 * - Border Width: Consistent stroke widths for outlines
 */
public class AppConstants {

    // ============================================
    // PRIMARY COLORS
    // ============================================
    /** The primary brand blue color used for navigation bars, primary buttons, and interactive highlights */
    public static final Color PRIMARY_BLUE = new Color(130, 170, 255);
    /** Darker shade of primary blue for hover states, borders, and emphasis */
    public static final Color DARK_PRIMARY_BLUE = new Color(100, 140, 225);
    
    // ============================================
    // BACKGROUND COLORS
    // ============================================
    /** Light gray background for main content areas, providing good contrast with text */
    public static final Color BG_LIGHT_GRAY = new Color(245, 247, 250);
    /** Pure white background for card panels, input fields, and primary content containers */
    public static final Color BG_WHITE = Color.WHITE;
    /** Medium gray background for disabled or secondary areas */
    public static final Color BG_GRAY = new Color(220, 220, 220);
    /** Medium-dark gray used for table headers and section dividers */
    public static final Color BG_MEDIUM_GRAY = new Color(200, 200, 200);
    
    // ============================================
    // TEXT COLORS
    // ============================================
    /** Dark text color for primary content, high contrast and readability */
    public static final Color TEXT_DARK = new Color(50, 50, 50);
    /** Primary text color for body content and main information display */
    public static final Color TEXT_PRIMARY = new Color(50, 50, 50);
    /** Secondary text color for supporting information, labels, and metadata */
    public static final Color TEXT_SECONDARY = new Color(100, 100, 100);
    /** Light text color for text on dark backgrounds or inverted color schemes */
    public static final Color TEXT_LIGHT = new Color(230, 240, 255);
    
    // ============================================
    // ACCENT COLORS - Used for interactive elements and status indicators
    // ============================================
    /** Green accent color for success, confirm, and positive actions (e.g., Checkout buttons) */
    public static final Color ACCENT_GREEN = new Color(34, 139, 34);
    /** Red accent color for destructive, delete, and negative actions (e.g., Clear Cart button) */
    public static final Color ACCENT_RED = new Color(220, 53, 69);
    /** Blue accent color for information and secondary interactive elements */
    public static final Color ACCENT_BLUE = new Color(66, 133, 244);
    /** Orange accent color for warnings, notifications, and important highlights */
    public static final Color ACCENT_ORANGE = new Color(255, 160, 0);
    /** Bright green for dashboard statistics and positive data visualization */
    public static final Color ACCENT_GREEN_BRIGHT = new Color(15, 157, 88);
    
    // ============================================
    // BORDER COLORS
    // ============================================
    /** Light gray border color for subtle dividers, input fields, and panel outlines */
    public static final Color BORDER_LIGHT_GRAY = new Color(200, 200, 200);
    
    // ============================================
    // FONTS
    // ============================================
    
    // Title Fonts
    /** Extra-large bold title font (42pt) for main page headers and splash screens */
    public static final Font FONT_TITLE_LARGE = new Font("Arial", Font.BOLD, 42);
    /** Large XL bold title font (28pt) for section titles and important headings */
    public static final Font FONT_TITLE_XL = new Font("Arial", Font.BOLD, 28);
    /** Large regular bold title font (24pt) for subsection titles and main content headings */
    public static final Font FONT_TITLE_LARGE_REGULAR = new Font("Arial", Font.BOLD, 24);
    /** Italic subtitle font (24pt) for descriptive text and secondary headings */
    public static final Font FONT_SUBTITLE = new Font("Arial", Font.ITALIC, 24);
    
    // Body Fonts
    /** Large body font (18pt plain) for prominent descriptive text and feature descriptions */
    public static final Font FONT_BODY_LARGE = new Font("Arial", Font.PLAIN, 18);
    /** Regular body font (14pt plain) for standard text content and descriptions */
    public static final Font FONT_BODY_REGULAR = new Font("Arial", Font.PLAIN, 14);
    /** Small body font (13pt plain) for secondary text, helper text, and compact content */
    public static final Font FONT_BODY_SMALL = new Font("Arial", Font.PLAIN, 13);
    /** Italic body font (14pt italic) for emphasized text and status messages */
    public static final Font FONT_BODY_ITALIC = new Font("Arial", Font.ITALIC, 14);
    
    // Label Fonts
    /** Bold label font (14pt) for form labels, control labels, and table headers */
    public static final Font FONT_LABEL_BOLD = new Font("Arial", Font.BOLD, 14);
    /** Regular label font (12pt) for button text and lighter UI elements */
    public static final Font FONT_LABEL_REGULAR = new Font("Arial", Font.PLAIN, 12);
    /** Small bold label font (12pt bold) for compact bold text in tight spaces */
    public static final Font FONT_LABEL_BOLD_SMALL = new Font("Arial", Font.BOLD, 12);
    
    // ============================================
    // SPACING & SIZING - Consistent measurements for layout and spacing
    // ============================================
    /** Large padding (40px) for main container margins and major section spacing */
    public static final int PADDING_LARGE = 40;
    /** Medium padding (20px) for inter-section spacing and card padding */
    public static final int PADDING_MEDIUM = 20;
    /** Small padding (10px) for element spacing and minor gaps */
    public static final int PADDING_SMALL = 10;
    /** Tiny padding (5px) for tight spacing and minimal gaps */
    public static final int PADDING_TINY = 5;
    
    /** Standard corner radius (15px) for rounded corners on modern UI elements */
    public static final int CORNER_RADIUS = 15;
    /** Standard border width (1px) for subtle borders and dividers */
    public static final int BORDER_WIDTH = 1;
    /** Thick border width (2px) for prominent borders and emphasis */
    public static final int BORDER_WIDTH_THICK = 2;
    
    // ============================================
    // PRIVATE CONSTRUCTOR - Utility class, no instances
    // ============================================
    private AppConstants() {
        throw new AssertionError("Cannot instantiate AppConstants utility class");
    }
}
