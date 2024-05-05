/**
 * @author Josh Feng, 1266669, chenhaof@student.unimelb.edu.au
 * @date 18 April 2024
 */

package whiteBoard;

import javax.swing.*;
import java.awt.*;

public class ClientParams {
    public static final String GUI_TITLE = "White Board - ";
    public static final int GUI_WIDTH    = 800;
    public static final int GUI_HEIGHT   = 600;
    public static final int CANVAS_WIDTH        = GUI_WIDTH-210;
    public static final int CANVAS_HEIGHT        = GUI_HEIGHT-135;

    public static final float DEFAULT_STROKE = 3.0f;

    // icons path
    public static final String ICON_LINE      = "line.png";
    public static final String ICON_CIRCLE    = "circle.png";
    public static final String ICON_OVAL      = "oval.png";
    public static final String ICON_RECTANGLE = "rectangle.png";
    public static final String ICON_DRAW      = "draw.png";
    public static final String ICON_ERASER    = "eraser.png";
    public static final String ICON_TEXT      = "text.png";
    public static final String ICON_ADD       = "add.png";
    public static final String ICON_MINUS     = "minus.png";

    // toolbar command
    public static final String LINE      = "line";
    public static final String CIRCLE    = "circle";
    public static final String OVAL      = "oval";
    public static final String RECTANGLE = "rectangle";
    public static final String DRAW      = "draw";
    public static final String ERASER    = "eraser";
    public static final String TEXT      = "text";

    // 16 colors
    public static final Color SILVER = new Color(192, 192, 192);
    public static final Color BLACK = new Color(0, 0, 0);
    public static final Color RED = new Color(255, 0, 0);
    public static final Color GREEN = new Color(0, 255, 0);
    public static final Color BLUE = new Color(0, 0, 255);
    public static final Color YELLOW = new Color(255, 255, 0);
    public static final Color ORANGE = new Color(255, 165, 0);
    public static final Color PINK = new Color(255, 192, 203);
    public static final Color PURPLE = new Color(128, 0, 128);
    public static final Color BROWN = new Color(165, 42, 42);
    public static final Color CYAN = new Color(0, 255, 255);
    public static final Color MAGENTA = new Color(255, 0, 255);
    public static final Color LIME = new Color(191, 255, 0);
    public static final Color MAROON = new Color(128, 0, 0);
    public static final Color NAVY = new Color(0, 0, 128);
    public static final Color AQUA = new Color(0, 255, 255);

    public static void RMI_CONNECT_ERROR() {
        System.err.println("RMI Connect Fail");
        JOptionPane.showMessageDialog(null, "RMI Connect Fail",
                "Warning", JOptionPane.WARNING_MESSAGE);
        System.exit(0);
    }

    public static void IO_ERROR() {
        System.err.println("IO Error");
        JOptionPane.showMessageDialog(null, "IO Error",
                "Warning", JOptionPane.WARNING_MESSAGE);
        System.exit(0);
    }
}
