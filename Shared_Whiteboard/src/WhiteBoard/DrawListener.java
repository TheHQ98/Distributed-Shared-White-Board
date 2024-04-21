package WhiteBoard;

import java.awt.*;
import java.awt.BasicStroke;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;

public class DrawListener extends MouseAdapter {
    // define coordinates
    private int x1, y1, x2, y2;

    // define toolbar panel
    private ToolBar toolBar;
    private Graphics2D g2d;
    private DrawPanel drawingPanel;
    private Color color;
    private String toolType;

    private BufferedImage tempImage;
    private Graphics2D g2dTemp;

    public DrawListener(ToolBar toolBar, DrawPanel drawingPanel) {
        g2d = (Graphics2D) drawingPanel.getGraphics();
        this.toolBar = toolBar;
        this.drawingPanel = drawingPanel;
        tempImage = null;
        g2dTemp = null;
    }

    @Override
    public void mousePressed(MouseEvent e) {
        // get initial coordinates
        x1 = e.getX();
        y1 = e.getY();
        System.out.println("初始坐标：" +x1 + " " + y1);

        // get current color
        color = toolBar.getColor();
        // get tool tye
        toolType = toolBar.getToolType();
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        // get final coordinates
        x2 = e.getX();
        y2 = e.getY();
        System.out.println("最终坐标：" +x2 + " " + y2);

        // set default thickness of the pen
        g2d.setStroke(new BasicStroke(3.0f));

        if (ClientParams.LINE.equals(toolType)) {
            g2d.setPaint(color);
            g2d.drawLine(x1, y1, x2, y2);
        }
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        super.mouseDragged(e);
    }
}
