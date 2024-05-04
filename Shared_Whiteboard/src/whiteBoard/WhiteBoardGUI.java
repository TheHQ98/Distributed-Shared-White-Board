/**
 * @author Josh Feng, 1266669, chenhaof@student.unimelb.edu.au
 * @date 18 April 2024
 */

package whiteBoard;

import remote.IRemoteCanvas;
import remote.IRemoteServer;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.JMenuBar;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.rmi.RemoteException;

public class WhiteBoardGUI {
    private JFrame frame;
    private final String userID;
    private final boolean isManager;
    DrawPanel drawPanel;
    private String filePath;
    private ChatBox chatBox;
    private IRemoteServer remoteServer;

    public WhiteBoardGUI(String userID, boolean isManager, IRemoteServer remoteServer) throws IOException {
        this.userID = userID;
        this.isManager = isManager;
        this.remoteServer = remoteServer;

        frame = new JFrame();
        frame.setTitle(ClientParams.GUI_TITLE + userID);
        frame.setSize(ClientParams.GUI_WIDTH, ClientParams.GUI_HEIGHT);
        frame.setMinimumSize(new Dimension(ClientParams.GUI_WIDTH, ClientParams.GUI_HEIGHT));

        // set menu bar
        menuBar();
        // set toolbar
        ToolBar toolBar = new ToolBar();
        // set draw canvas
        drawPanel = new DrawPanel(toolBar, remoteServer, isManager, userID);
        frame.add(drawPanel, BorderLayout.CENTER);
        frame.add(toolBar, BorderLayout.SOUTH);

        // set chat box
        chatBox = new ChatBox(remoteServer, userID, isManager);
        frame.add(chatBox, BorderLayout.EAST);

        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
        frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent we) {
                int result = JOptionPane.showConfirmDialog(
                        frame,
                        "Are you sure you want to exit? \n" +
                                "Make sure you save your canvas as a file.\n" +
                                "Other user will be close automatically.",
                        "Exit Confirmation",
                        JOptionPane.YES_NO_OPTION
                );
                if (result == JOptionPane.YES_OPTION) {
                    if (isManager) {
                        System.out.println("Manager left the room.");
                        try {
                            remoteServer.managerLeave();
                        } catch (RemoteException e) {
                            throw new RuntimeException(e);
                        }
                    } else {
                        System.out.println(userID + " left the room.");
                        try {
                            remoteServer.removeUser(userID);
                        } catch (RemoteException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    }

                    frame.dispose();
                    System.exit(0);
                }
            }
        });

    }

    // set a menu bar
    private void menuBar() {
        JMenuBar menuBar = new JMenuBar();
        frame.setJMenuBar(menuBar);

        if (isManager) {
            // Create file menu
            JMenu fileMenu = new JMenu("File");
            menuBar.add(fileMenu);

            // new file option
            JMenuItem newItem = new JMenuItem("New");
            newItem.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    try {
                        newFile();
                    } catch (IOException ex) {
                        throw new RuntimeException(ex);
                    }
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
                    try {
                        close();
                    } catch (IOException ex) {
                        throw new RuntimeException(ex);
                    }
                }
            });
            fileMenu.add(closeItem);
        }
        frame.setJMenuBar(menuBar);
    }

    private void newFile() throws IOException {
        if (drawPanel.getIsClosed()) {
            remoteServer.newCanvas();
            filePath = null;
            drawPanel.changeIsClosedState(false);
            JOptionPane.showMessageDialog(frame, "Canvas created", "Canvas", JOptionPane.WARNING_MESSAGE);
        } else {
            int answer = JOptionPane.showConfirmDialog(null,
                    "Are you sure you want to create a new canvas?\n" +
                            "The exist canvas will be delete.", "Warning", JOptionPane.YES_NO_OPTION);
            if (answer == JOptionPane.YES_OPTION) {
                remoteServer.newCanvas();
                filePath = null;
                drawPanel.changeIsClosedState(false);
            }
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
                drawPanel.sendSavedImage(image);
                remoteServer.updateCanvas();
                remoteServer.broadcastSystemMessage("SYSTEM: Manager opened a new file");
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
            int answer = JOptionPane.showConfirmDialog(frame,
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

    private void close() throws IOException {
        //TODO
        int answer = JOptionPane.showConfirmDialog(frame,
                "Canvas will be close.\n" +
                        "Do you want to save the canvas?\n" +
                        "Press yes to save it as a file.", "Warning", JOptionPane.YES_NO_OPTION);
        if (answer == JOptionPane.YES_OPTION) {
            save();
        }
        drawPanel.newCanvas();
        drawPanel.changeIsClosedState(true);
        remoteServer.closeCanvas();
    }

    public void syncCanvas(IRemoteCanvas remoteCanvas) throws RemoteException {
        drawPanel.syncCanvas(remoteCanvas);
    }

    public void syncMessage(String message) {
        chatBox.syncMessage(message);
    }

    public void askQuit(String managerName) {
        SwingUtilities.invokeLater(() -> {
            JOptionPane.showMessageDialog(frame, "Manager(" + managerName + ") closed your access. whiteboard will be close", "Message from manager", JOptionPane.WARNING_MESSAGE);
            frame.dispose();
            System.exit(0);
        });
    }

    public void syncList(DefaultListModel<String> tempModel) {
        chatBox.syncList(tempModel);
    }

    public void askUpdateList() throws RemoteException {
        chatBox.updateUserList();
    }

    public void askJoinMessage() throws IOException {
        chatBox.joinMessage();
    }

    public void askCleanCanvas() {
        drawPanel.newCanvas();
    }

    public void askGetCanvasFromServer(byte[] imageData) throws IOException {
        drawPanel.getCanvasFromServer(imageData);
    }

    public void askCloseCanvas() {
        SwingUtilities.invokeLater(() -> {
            JOptionPane.showMessageDialog(frame, "Manager closed the canvas. Do you want to reconnect?", "Message from manager", JOptionPane.WARNING_MESSAGE);
            frame.dispose();
            System.exit(0);
        });
    }

    public boolean requestAccess(String name) {
        drawPanel.askRender();
        int answer = JOptionPane.showConfirmDialog(frame,
                name + " want to access the canvas", "Share Request", JOptionPane.YES_NO_OPTION);
        if (answer == JOptionPane.YES_OPTION) {
            return true;
        }
        return false;
    }
}
