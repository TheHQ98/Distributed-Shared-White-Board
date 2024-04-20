/**
 * @author Josh Feng, 1266669, chenhaof@student.unimelb.edu.au
 * @date 18 April 2024
 */

package WhiteBoard;

import javax.swing.*;
import javax.swing.JMenuBar;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class WhiteBoardGUI {
    private JFrame frame;

    public WhiteBoardGUI() {
        frame = new JFrame();
        frame.setTitle(ClientParams.GUI_TITLE);
        frame.setSize(ClientParams.GUI_WIDTH, ClientParams.GUI_HEIGHT);

        setScreenSize();
        //combinedPanel();
        //frame.add(tabbedPane);
        menuBar();

        frame.setVisible(true);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    // when start the gui, it will pop up in the middle of the screen
    private void setScreenSize() {
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int x = (int) ((screenSize.getWidth() - frame.getWidth()) / 2);
        int y = (int) ((screenSize.getHeight() - frame.getHeight()) / 2);
        frame.setLocation(x, y);
    }

    // set a menu bar
    private void menuBar() {
        JMenuBar menuBar = new JMenuBar();
        frame.setJMenuBar(menuBar);
        // Create file menu
        JMenu fileMenu = new JMenu("File");
        menuBar.add(fileMenu);

        // new file option
        JMenuItem newItem = new JMenuItem("New");
        newItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // process
                JOptionPane.showMessageDialog(null, "New File");
            }
        });
        fileMenu.add(newItem);

        // open file option
        JMenuItem openItem = new JMenuItem("Open");
        openItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // process
                JOptionPane.showMessageDialog(null, "Open File");
            }
        });
        fileMenu.add(openItem);

        // save file option
        JMenuItem saveItem = new JMenuItem("Save");
        saveItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // process
                JOptionPane.showMessageDialog(null, "Save File");
            }
        });
        fileMenu.add(saveItem);

        // save as file option
        JMenuItem saveAsItem = new JMenuItem("SaveAs");
        saveAsItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // process
                JOptionPane.showMessageDialog(null, "Save As a File");
            }
        });
        fileMenu.add(saveAsItem);

        // close file option
        JMenuItem closeItem = new JMenuItem("Close");
        closeItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // process
                JOptionPane.showMessageDialog(null, "Close File");
            }
        });
        fileMenu.add(closeItem);

        frame.setJMenuBar(menuBar);
    }
}
