/**
 * @author Josh Feng, 1266669, chenhaof@student.unimelb.edu.au
 * @date 18 April 2024
 */

package WhiteBoard;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.JMenuBar;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class WhiteBoardGUI {
    private JFrame frame;
    private final String userID;
    private final boolean isManager;
    DrawPanel drawPanel;
    private String filePath;

    public WhiteBoardGUI(String userID, boolean isManager) {
        this.userID = userID;
        this.isManager = isManager;

        frame = new JFrame();
        frame.setTitle(ClientParams.GUI_TITLE);
        frame.setSize(ClientParams.GUI_WIDTH, ClientParams.GUI_HEIGHT);

        // set menu bar
        menuBar();
        // set toolbar
        ToolBar toolBar = new ToolBar();
        // set draw canvas
        drawPanel = new DrawPanel(toolBar);
        frame.add(drawPanel, BorderLayout.CENTER);
        frame.add(toolBar, BorderLayout.SOUTH);

        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

//        DrawListener drawListener = new DrawListener(toolBar, drawPanel);
//        drawPanel.addMouseListener(drawListener);
//        drawPanel.addMouseMotionListener(drawListener);
    }

    // set a menu bar
    private void menuBar() {
        if (!isManager) {
            JMenuBar menuBar = new JMenuBar();
            frame.setJMenuBar(menuBar);
            // Create empty menu
            JMenu fileMenu = new JMenu();
            menuBar.add(fileMenu);
            return;
        }
        JMenuBar menuBar = new JMenuBar();
        frame.setJMenuBar(menuBar);
        // Create file menu
        JMenu fileMenu = new JMenu("File");
        menuBar.add(fileMenu);

        // new file option
        JMenuItem newItem = new JMenuItem("New");
        newItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                newFile();
            }
        });
        fileMenu.add(newItem);

        // open file option
        JMenuItem openItem = new JMenuItem("Open");
        openItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                openFile();
            }
        });
        fileMenu.add(openItem);

        // save file option
        JMenuItem saveItem = new JMenuItem("Save");
        saveItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                save();
            }
        });
        fileMenu.add(saveItem);

        // save as file option
        JMenuItem saveAsItem = new JMenuItem("SaveAs");
        saveAsItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                saveAs();
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

    private void newFile() {
        int answer = JOptionPane.showConfirmDialog(null,
                "Are you sure you want to create a new canvas?\n" +
                        "The exist canvas will be delete.", "Warning", JOptionPane.YES_NO_OPTION);
        if (answer == JOptionPane.YES_OPTION) {
            drawPanel.cleanCanvas();
        }
    }

    private void openFile() {
        FileDialog fileDialog = new FileDialog(frame, "Open", FileDialog.LOAD);
        fileDialog.setVisible(true);
        if (fileDialog.getFile() != null) {
            filePath = fileDialog.getDirectory() + fileDialog.getFile();
            try {
                BufferedImage image = ImageIO.read(new File(filePath));
                drawPanel.renderFrame(image);
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        }
    }

    private void save() {
        if (filePath != null) {
            try {
                ImageIO.write(drawPanel.getCanvasImage(), "png", new File(filePath));
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        } else {
            int answer = JOptionPane.showConfirmDialog(null,
                    "You have not save it as a file\n" +
                            "Press yes to save it as a file", "Warning", JOptionPane.YES_NO_OPTION);
            if (answer == JOptionPane.YES_OPTION) {
                saveAs();
            }
        }
    }
    private void saveAs() {
        FileDialog fileDialog = new FileDialog(frame, "Save As", FileDialog.SAVE);
        fileDialog.setVisible(true);
        if (fileDialog.getFile() != null) {
            filePath = fileDialog.getDirectory() + fileDialog.getFile() + ".png";
            try {
                ImageIO.write(drawPanel.getCanvasImage(), "png", new File(filePath));
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        }
    }
}
