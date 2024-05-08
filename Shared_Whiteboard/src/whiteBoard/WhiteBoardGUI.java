/**
 * user side GUI
 * allow user draw, select tool type, select colour, check user list, check message and send message
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
    private final boolean isManager;
    DrawPanel drawPanel;
    private String filePath;
    private ChatBox chatBox;
    private IRemoteServer remoteServer;

    public WhiteBoardGUI(String userID, boolean isManager, IRemoteServer remoteServer) throws IOException {
        this.isManager = isManager;
        this.remoteServer = remoteServer;

        // basic information
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
                        //System.out.println("Manager left the room.");
                        try {
                            remoteServer.managerLeave();
                        } catch (RemoteException e) {
                            ClientParams.RMI_CONNECT_ERROR();
                            System.err.println("RemoteException: " + e);
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

        // only for manager
        if (isManager) {
            // create file menu
            JMenu fileMenu = new JMenu("File");
            menuBar.add(fileMenu);

            // new file option
            JMenuItem newItem = new JMenuItem("New");
            newItem.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    try {
                        newFile();
                    } catch (IOException ex) {
                        ClientParams.IO_ERROR();
                        System.err.println("IOException: " + ex);
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
                        ClientParams.IO_ERROR();
                        System.err.println("IOException: " + ex);
                    }
                }
            });
            fileMenu.add(closeItem);
        }
        frame.setJMenuBar(menuBar);
    }

    // new canvas method
    private void newFile() throws IOException {
        if (drawPanel.getIsClosed()) {
            remoteServer.newCanvas();
            filePath = null;
            drawPanel.changeIsClosedState(false);
            JOptionPane.showMessageDialog(frame, "Canvas created", "Canvas", JOptionPane.WARNING_MESSAGE);
        } else {
            int answer = JOptionPane.showConfirmDialog(frame,
                    "Are you sure you want to create a new canvas?\n" +
                            "The exist canvas will be delete.", "Warning", JOptionPane.YES_NO_OPTION);
            if (answer == JOptionPane.YES_OPTION) {
                remoteServer.newCanvas();
                filePath = null;
                drawPanel.changeIsClosedState(false);
            }
        }
    }

    // open an exist canvas
    private void openFile() {
        FileDialog fileDialog = new FileDialog(frame, "Open", FileDialog.LOAD);
        fileDialog.setVisible(true);

        if (fileDialog.getFile() != null) {
            if (drawPanel.getIsClosed()) {
                filePath = fileDialog.getDirectory() + fileDialog.getFile();
                try {
                    BufferedImage image = ImageIO.read(new File(filePath));
                    drawPanel.renderFrame(image);
                    drawPanel.sendSavedImage(image);
                    remoteServer.updateCanvas();
                    remoteServer.broadcastSystemMessage("SYSTEM: Manager opened a exist canvas");
                } catch (IOException ex) {
                    ClientParams.IO_ERROR();
                    System.err.println("IOException: " + ex);
                }
                drawPanel.changeIsClosedState(false);
                JOptionPane.showMessageDialog(frame, "Canvas opened", "Canvas", JOptionPane.WARNING_MESSAGE);
            } else {
                filePath = fileDialog.getDirectory() + fileDialog.getFile();
                try {
                    BufferedImage image = ImageIO.read(new File(filePath));
                    drawPanel.renderFrame(image);
                    drawPanel.sendSavedImage(image);
                    remoteServer.updateCanvas();
                    remoteServer.broadcastSystemMessage("SYSTEM: Manager opened a exist canvas");
                } catch (IOException ex) {
                    ClientParams.IO_ERROR();
                    System.err.println("IOException: " + ex);
                }
            }
        }
    }

    // save current canvas method
    private void save() {
        if (filePath != null) {
            try {
                ImageIO.write(drawPanel.getCanvasImage(), "png", new File(filePath));
            } catch (IOException ex) {
                ClientParams.IO_ERROR();
                System.err.println("IOException: " + ex);
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

    // saveAs current canvas method
    private void saveAs() {
        FileDialog fileDialog = new FileDialog(frame, "Save As", FileDialog.SAVE);
        fileDialog.setVisible(true);
        if (fileDialog.getFile() != null) {
            filePath = fileDialog.getDirectory() + fileDialog.getFile() + ".png";
            try {
                ImageIO.write(drawPanel.getCanvasImage(), "png", new File(filePath));
            } catch (IOException ex) {
                ClientParams.IO_ERROR();
                System.err.println("IOException: " + ex);
            }
        }
    }

    // close current canvas method
    private void close() throws IOException {
        if (drawPanel.getIsClosed()) {
            JOptionPane.showMessageDialog(frame, "Canvas already closed",
                    "Canvas", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Object[] options = {"Save", "No", "Cancel"};
        int answer = JOptionPane.showOptionDialog(frame,
                "Canvas will be closed.\n" +
                        "Do you want to save the canvas?",
                "Warning",
                JOptionPane.YES_NO_CANCEL_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                options,
                options[0]);

        if (answer == JOptionPane.YES_OPTION) {
            save();
        } else if (answer == JOptionPane.CANCEL_OPTION) {
            return;
        }

        drawPanel.newCanvas();
        drawPanel.changeIsClosedState(true);
        remoteServer.closeCanvas();
    }

    // server ask for sync canvas
    public void syncCanvas(IRemoteCanvas remoteCanvas) throws RemoteException {
        drawPanel.syncCanvas(remoteCanvas);
    }

    // server ask for sync message
    public void syncMessage(String message) {
        chatBox.syncMessage(message);
    }

    // manager ask server this user to quit
    public void askQuit(String managerName) {
        SwingUtilities.invokeLater(() -> {
            JOptionPane.showMessageDialog(frame, "Manager(" + managerName + ") closed your access." +
                    " whiteboard will be close", "Message from manager", JOptionPane.WARNING_MESSAGE);
            frame.dispose();
            System.exit(0);
        });
    }

    // sync user list from server
    public void syncList(DefaultListModel<String> tempModel) {
        chatBox.syncList(tempModel);
    }

    // update user list from server
    public void askUpdateList() throws RemoteException {
        chatBox.updateUserList();
    }

    // add system join message
    public void askJoinMessage() throws IOException {
        chatBox.joinMessage();
    }

    // ask clean the canvas
    public void askCleanCanvas() {
        drawPanel.newCanvas();
    }

    // get canvas from server
    public void askGetCanvasFromServer(byte[] imageData) throws IOException {
        drawPanel.getCanvasFromServer(imageData);
    }

    // ask close canvas
    public void askCloseCanvas() throws RemoteException {
        int answer = JOptionPane.showConfirmDialog(frame,
                "Manager closed the canvas. Do you want to reconnect?", "Warning", JOptionPane.YES_NO_OPTION);
        if (answer == JOptionPane.YES_OPTION) {
            while (remoteServer.getIsClosedState()) {
                Object[] options = {"Retry", "Close"};
                int result = JOptionPane.showOptionDialog(frame,
                        "Manager have not open a new file yet",
                        "Message from manager",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.WARNING_MESSAGE,
                        null,
                        options,
                        options[0]);
                if (result == JOptionPane.YES_OPTION) {
                    continue;
                } else {
                    Thread t = new Thread(() -> {
                        frame.dispose();
                        System.exit(0);
                    });
                    t.start();
                }
            }
        } else {
            Thread t = new Thread(() -> {
                frame.dispose();
                System.exit(0);
            });
            t.start();
        }
    }

    // ask access
    public boolean requestAccess(String name) throws RemoteException {
        drawPanel.askRender();
        int answer = JOptionPane.showConfirmDialog(frame,
                name + " wants to share your whiteboard", "Share Request", JOptionPane.YES_NO_OPTION);
        if (answer == JOptionPane.YES_OPTION) {
            // check name is exist again
            if (remoteServer.checkName(name)) {
                return false;
            }
            return true;
        }
        return false;
    }

    // get is closed state from manager
    public boolean getIsClosedState() {
        return drawPanel.getIsClosed();
    }
}
