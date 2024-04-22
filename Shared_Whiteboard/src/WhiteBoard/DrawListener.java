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

    public DrawListener(ToolBar toolBar, DrawPanel drawingPanel) {
        g2d = (Graphics2D) drawingPanel.getGraphics();
        this.toolBar = toolBar;
        this.drawingPanel = drawingPanel;
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
            g2d.setColor(color);
            g2d.drawLine(x1, y1, x2, y2);
        } else if (ClientParams.RECTANGLE.equals(toolType)) {
            g2d.setColor(color);
            g2d.drawRect(Math.min(x1, x2), Math.min(y1, y2), Math.abs(x1 - x2), Math.abs(y1 - y2));
        } else if (ClientParams.CIRCLE.equals(toolType)) {
            g2d.setColor(color);

            // 计算圆的半径，它是鼠标起点和终点之间距离的一半
            int radius = (int) Math.sqrt(Math.pow(x2 - x1, 2) + Math.pow(y2 - y1, 2)) / 2;

            // 计算圆的中心点
            int centerX = (x1 + x2) / 2;
            int centerY = (y1 + y2) / 2;

            // 使用中心点和半径来确定圆的左上角位置
            int startX = centerX - radius;
            int startY = centerY - radius;

            // 绘制圆，确保宽度和高度相等（都是直径）
            g2d.drawOval(startX, startY, radius * 2, radius * 2);
        } else if (ClientParams.OVAL.equals(toolType)) {
            g2d.setColor(color);
            g2d.drawOval(Math.min(x1, x2), Math.min(y1, y2), Math.abs(x1 - x2), Math.abs(y1 - y2));
        }
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        // get final coordinates
        x2 = e.getX();
        y2 = e.getY();

        if (ClientParams.ERASER.equals(toolType)) {
            g2d.setColor(Color.WHITE);
            g2d.setStroke(new BasicStroke(50.0f));
            g2d.drawLine(x2, y2, x2, y2);
        } else if (ClientParams.DRAW.equals(toolType)) {
            g2d.setColor(color);
            g2d.setStroke(new BasicStroke(3.0f));
            g2d.drawLine(x1, y1, x2, y2);
            x1 = x2;
            y1 = y2;
        }

    }
}