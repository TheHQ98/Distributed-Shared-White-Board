/**
 * Toolbar panel, allow user to select tool type and colour
 *
 * @author Josh Feng, 1266669, chenhaof@student.unimelb.edu.au
 * @date 18 April 2024
 */
package whiteBoard;

import javax.swing.*;
import java.awt.*;
import java.awt.GridLayout;
import java.net.URL;

public class ToolBar extends JPanel {
    private JPanel toolBar;
    private JPanel colorBar;
    private JPanel eraserSizePanel;
    private JPanel sizeBar;
    private String toolType;
    private Color colorType;
    private JLabel currentTool;
    private JLabel currentColor;
    private float eraserSize = 50.00f;
    private static final Color[] colors = {
            ClientParams.SILVER,
            ClientParams.BLACK,
            ClientParams.RED,
            ClientParams.GREEN,
            ClientParams.BLUE,
            ClientParams.YELLOW,
            ClientParams.ORANGE,
            ClientParams.PINK,
            ClientParams.PURPLE,
            ClientParams.BROWN,
            ClientParams.CYAN,
            ClientParams.MAGENTA,
            ClientParams.LIME,
            ClientParams.MAROON,
            ClientParams.NAVY,
            ClientParams.AQUA
    };

    public ToolBar() {
        toolType = ClientParams.LINE;
        colorType = ClientParams.BLACK;
        toolBar = new JPanel();
        colorBar = new JPanel();
        eraserSizePanel = new JPanel();
        sizeBar = new JPanel();
        currentTool = new JLabel("Current tool: " + toolType);
        currentColor = new JLabel("Current color: " + colorToString(colorType));
        init();
    }

    private void init() {
        this.setLayout(new BorderLayout());

        // setup tool types
        toolBar.setLayout(new GridLayout(2, 4));
        String[] iconImg = {
                ClientParams.ICON_LINE,
                ClientParams.ICON_CIRCLE,
                ClientParams.ICON_OVAL,
                ClientParams.ICON_RECTANGLE,
                ClientParams.ICON_DRAW,
                ClientParams.ICON_ERASER,
                ClientParams.ICON_TEXT
        };

        // render images
        for (String iconName : iconImg) {
            URL imageUrl = getClass().getClassLoader().getResource(iconName);
            if (imageUrl == null) {
                System.err.println("Resource not found: " + iconName);
                continue;
            }
            ImageIcon image = new ImageIcon(imageUrl);
            JButton button = new JButton(image);
            button.setPreferredSize(new Dimension(36, 36));
            String actionCommand = iconName.substring(iconName.lastIndexOf('/') + 1,
                    iconName.lastIndexOf('.'));
            button.setActionCommand(actionCommand);
            button.addActionListener(e -> {
                toolType = e.getActionCommand();
                currentTool.setText("Current tool: " + toolType);
            });
            toolBar.add(button);
        }
        this.add(toolBar, BorderLayout.WEST);


        // setup color bar
        colorBar.setLayout(new GridLayout(2, 10));
        for (Color color : colors) {
            JButton button = new JButton();
            button.setPreferredSize(new Dimension(28, 28));
            button.setBackground(color);
            button.setOpaque(true);
            button.setBorderPainted(false);
            button.addActionListener(e -> {
                colorType = new Color(color.getRGB());
                currentColor.setText("Current color: " + colorToString(colorType));
            });
            colorBar.add(button);
        }
        this.add(colorBar, BorderLayout.EAST);

        // setup sizes of eraser
        eraserSizePanel.setLayout(new BoxLayout(eraserSizePanel, BoxLayout.Y_AXIS));
        JLabel sizeLabel = new JLabel("sizes of eraser");
        sizeLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        JLabel sizeValueLabel = new JLabel(String.valueOf(eraserSize));
        sizeValueLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        eraserSizePanel.add(sizeLabel);
        sizeBar.setLayout(new FlowLayout(FlowLayout.CENTER, 4, 4));
        String[] sizeIMG = {
                ClientParams.ICON_ADD,
                ClientParams.ICON_MINUS
        };
        for (String iconName : sizeIMG) {
            URL imageUrl = getClass().getClassLoader().getResource(iconName);
            if (imageUrl == null) {
                System.err.println("Resource not found: " + iconName);
                continue;
            }
            ImageIcon image = new ImageIcon(imageUrl);
            JButton button = new JButton(image);
            button.setPreferredSize(new Dimension(25, 25));
            button.setActionCommand(iconName.substring(iconName.lastIndexOf('/') + 1, iconName.lastIndexOf('.')));
            button.addActionListener(e -> {
                if (e.getActionCommand().equals("add")) {
                    eraserSize += 20.00f;
                } else {
                    if (eraserSize - 20.00f > 0.00f) {
                        eraserSize -= 20.00f;
                    }
                }
                sizeValueLabel.setText(String.valueOf(eraserSize));
            });
            sizeBar.add(button);
        }
        eraserSizePanel.add(sizeBar);
        eraserSizePanel.add(sizeValueLabel);
        this.add(eraserSizePanel, BorderLayout.CENTER);

        // add current tool and color labels to north panel
        JPanel northPanel = new JPanel();
        northPanel.setLayout(new BoxLayout(northPanel, BoxLayout.X_AXIS));
        currentTool.setAlignmentX(Component.LEFT_ALIGNMENT);
        currentColor.setAlignmentX(Component.RIGHT_ALIGNMENT);
        northPanel.add(currentTool);
        northPanel.add(Box.createHorizontalGlue());
        northPanel.add(currentColor);
        this.add(northPanel, BorderLayout.NORTH);
    }

    public String getToolType() {
        return toolType;
    }

    public Color getColor() {
        return colorType;
    }

    public float getEraserSize() {
        return eraserSize;
    }

    public String colorToString(Color color) {
        if (color.equals(ClientParams.SILVER)) {
            return "SILVER";
        } else if (color.equals(ClientParams.BLACK)) {
            return "BLACK";
        } else if (color.equals(ClientParams.RED)) {
            return "RED";
        } else if (color.equals(ClientParams.GREEN)) {
            return "GREEN";
        } else if (color.equals(ClientParams.BLUE)) {
            return "BLUE";
        } else if (color.equals(ClientParams.YELLOW)) {
            return "YELLOW";
        } else if (color.equals(ClientParams.ORANGE)) {
            return "ORANGE";
        } else if (color.equals(ClientParams.PINK)) {
            return "PINK";
        } else if (color.equals(ClientParams.PURPLE)) {
            return "PURPLE";
        } else if (color.equals(ClientParams.BROWN)) {
            return "BROWN";
        } else if (color.equals(ClientParams.CYAN)) {
            return "CYAN";
        } else if (color.equals(ClientParams.MAGENTA)) {
            return "MAGENTA";
        } else if (color.equals(ClientParams.LIME)) {
            return "LIME";
        } else if (color.equals(ClientParams.MAROON)) {
            return "MAROON";
        } else if (color.equals(ClientParams.NAVY)) {
            return "NAVY";
        } else if (color.equals(ClientParams.AQUA)) {
            return "AQUA";
        }
        return "";
    }
}
