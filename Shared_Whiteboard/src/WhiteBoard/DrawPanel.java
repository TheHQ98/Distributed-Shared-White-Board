package WhiteBoard;

import javax.swing.*;
import java.awt.*;
import java.awt.Color;
import java.awt.Dimension;

public class DrawPanel extends JPanel {
    private JPanel drawCanvas;

    public DrawPanel() {
        drawCanvas = new JPanel();
        init();
    }

    private void init() {
        this.setLayout(new FlowLayout(FlowLayout.RIGHT));
        this.setBackground(Color.WHITE);
        drawCanvas.setBackground(Color.WHITE);
        drawCanvas.setPreferredSize(new Dimension(ClientParams.GUI_WIDTH, ClientParams.GUI_HEIGHT));
        this.add(drawCanvas);
    }
}
