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
    private final String userID;
    private final boolean isManager;

    public WhiteBoardGUI(String userID, boolean isManager) {
        this.userID = userID;
        this.isManager = isManager;

        frame = new JFrame();
        frame.setTitle(ClientParams.GUI_TITLE);
        frame.setSize(ClientParams.GUI_WIDTH, ClientParams.GUI_HEIGHT);

        // set menu bar
        menuBar();
        // set draw canvas
        DrawPanel drawPanel = new DrawPanel();
        // set toolbar
        ToolBar toolBar = new ToolBar();
        frame.add(drawPanel, BorderLayout.CENTER);
        frame.add(toolBar, BorderLayout.WEST);

        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        DrawListener drawListener = new DrawListener(toolBar, drawPanel);
        drawPanel.addMouseListener(drawListener);
        drawPanel.addMouseMotionListener(drawListener);
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
