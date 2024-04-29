/**
 * @author Josh Feng, 1266669, chenhaof@student.unimelb.edu.au
 * @date 18 April 2024
 */

package whiteBoard;

import remote.IRemoteCanvas;
import remote.IRemoteServer;
import remote.RemoteCanvas;

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

    public WhiteBoardGUI(String userID, boolean isManager, IRemoteServer remoteServer) throws IOException {
        this.userID = userID;
        this.isManager = isManager;

        frame = new JFrame();
        frame.setTitle(ClientParams.GUI_TITLE + userID);
        frame.setSize(ClientParams.GUI_WIDTH, ClientParams.GUI_HEIGHT);

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
                                "Make sure you save your canvas as a file\n" +
                                "Other user will be close automatically",
                        "Exit Confirmation",
                        JOptionPane.YES_NO_OPTION
                );
                if (result == JOptionPane.YES_OPTION) {
                    // 如果是管理者，可以在这里添加离开时的逻辑，比如通知服务器等
                    if (isManager) {
                        // TODO: 添加管理者离开时的代码
                        System.out.println("Manager left the room.");
                        try {
                            remoteServer.managerLeave();
                        } catch (RemoteException e) {
                            throw new RuntimeException(e);
                        }
                    } else {
                        System.out.println( userID+ " left the room.");
                        try {
                            remoteServer.removeUser(userID);
                        } catch (RemoteException e) {
                            e.printStackTrace();
                        }
                    }

                    // 现在安全退出程序
                    frame.dispose(); // 关闭窗口
                    System.exit(0);  // 完全结束程序
                }
            }
        });

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
                try {
                    close();
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
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
            //TODO
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

    private void close() throws IOException {
        //TODO
    }

    public void syncCanvas(IRemoteCanvas remoteCanvas) throws RemoteException {
        drawPanel.syncCanvas(remoteCanvas);
    }

    public void syncMessage(String message) {
        chatBox.syncMessage(message);
    }

    public void askQuit(String managerName) {
        SwingUtilities.invokeLater(() -> {
            JOptionPane.showMessageDialog(frame, "Manager(" + managerName + ") has shut down. whiteboard will be close", "Message from manager", JOptionPane.WARNING_MESSAGE);
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
}
