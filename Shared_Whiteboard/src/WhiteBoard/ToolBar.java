package WhiteBoard;

import javax.swing.*;
import java.awt.*;
import java.awt.GridLayout;
import java.lang.reflect.Array;

public class ToolBar extends JPanel {
    private JPanel toolBar;

    public ToolBar() {
        toolBar = new JPanel();
        init();
    }

    private void init() {
        this.setLayout(new FlowLayout(FlowLayout.LEFT));

        toolBar.setLayout(new GridLayout(5, 2, 4,4));
        String[] iconImg = {ClientParams.ICON_LINE,
                ClientParams.ICON_CIRCLE,
                ClientParams.ICON_OVAL,
                ClientParams.ICON_RECTANGLE,
                ClientParams.ICON_DRAW,
                ClientParams.ICON_ERASER,
                ClientParams.ICON_TEXT};

        for (int i=0; i < iconImg.length; i++) {
            ImageIcon image = new ImageIcon(iconImg[i]);
            JButton button = new JButton(image);
            button.setPreferredSize(new Dimension(36, 36));
            toolBar.add(button);
        }

        this.add(toolBar);
    }
}
