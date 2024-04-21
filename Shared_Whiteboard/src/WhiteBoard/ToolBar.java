package WhiteBoard;

import javax.swing.*;
import java.awt.*;
import java.awt.GridLayout;
import java.lang.reflect.Array;

public class ToolBar extends JPanel {
    private JPanel toolBar;
    private JPanel colorBar;
    private String toolType;
    private Color colorType;
    private static final Color[] colors = {
            ClientParams.SILVER,
            ClientParams.BLACK,
            ClientParams.WHITE,
            ClientParams.RED,
            ClientParams.GREEN,
            ClientParams.BLUE,
            ClientParams.YELLOW,
            ClientParams.ORANGE,
            ClientParams.PINK,
            ClientParams.PURPLE,
            ClientParams.GRAY,
            ClientParams.BROWN,
            ClientParams.CYAN,
            ClientParams.MAGENTA,
            ClientParams.LIME,
            ClientParams.MAROON,
            ClientParams.OLIVE,
            ClientParams.NAVY,
            ClientParams.AQUA,
            ClientParams.TEAL
    };

    public ToolBar() {
        toolType = ClientParams.LINE;
        colorType = ClientParams.BLACK;
        toolBar = new JPanel();
        colorBar = new JPanel();
        init();
    }

    private void init() {
        this.setLayout(new BorderLayout());

        toolBar.setLayout(new GridLayout(4, 2, 4, 4));
        String[] iconImg = {ClientParams.ICON_LINE,
                ClientParams.ICON_CIRCLE,
                ClientParams.ICON_OVAL,
                ClientParams.ICON_RECTANGLE,
                ClientParams.ICON_DRAW,
                ClientParams.ICON_ERASER,
                ClientParams.ICON_TEXT};

        for (String s : iconImg) {
            ImageIcon image = new ImageIcon(s);
            JButton button = new JButton(image);
            button.setPreferredSize(new Dimension(36, 36));
            button.setActionCommand(s.substring(s.lastIndexOf('/') + 1, s.lastIndexOf('.')));
            button.addActionListener(e -> {
                toolType = e.getActionCommand();
                System.out.println(toolType);
            });

            toolBar.add(button);
        }
        this.add(toolBar, BorderLayout.NORTH);

        colorBar.setLayout(new GridLayout(10, 2));

        for (Color color : colors) {
            JButton button = new JButton();
            button.setPreferredSize(new Dimension(32, 32));
            button.setBackground(color);
            button.setOpaque(true);
            button.setBorderPainted(false);

            button.addActionListener(e -> {
                colorType = new Color(color.getRGB());
                System.out.println(colorType);
            });

            colorBar.add(button);
        }

        this.add(colorBar, BorderLayout.SOUTH);
    }

    public String getToolType() {
        return toolType;
    }

    public Color getColor() {
        return colorType;
    }
}
