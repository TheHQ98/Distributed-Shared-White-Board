/**
 * ChatBox components
 * includes a display of user list, chat log box and chat input box
 *
 * @author Josh Feng, 1266669, chenhaof@student.unimelb.edu.au
 * @date 27 April 2024
 */

package whiteBoard;

import remote.IRemoteServer;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.rmi.RemoteException;

public class ChatBox extends JPanel {
    private JList<String> userList;
    private DefaultListModel<String> userModel;
    private JTextArea chatArea;
    private JTextField chatInputField;
    private JButton submitButton;
    private IRemoteServer remoteServer;
    private final String userID;
    private final boolean isManager;

    public ChatBox(IRemoteServer remoteServer, String userID, boolean isManager) throws IOException {
        this.remoteServer = remoteServer;
        this.userID = userID;
        this.isManager = isManager;
        init();
    }

    public void init() throws RemoteException {
        setLayout(new BorderLayout());

        // initial user list
        userModel = new DefaultListModel<>();
        userList = new JList<>(userModel);
        JScrollPane userListScrollPane = new JScrollPane(userList);
        userListScrollPane.setPreferredSize(new Dimension(200, 200));

        // only allowed manager to double-click user list
        if (isManager) {
            userList.addMouseListener(new MouseAdapter() {
                public void mouseClicked(MouseEvent evt) {
                    JList list = (JList)evt.getSource();

                    if (evt.getClickCount() == 2) {
                        int index = list.locationToIndex(evt.getPoint());
                        String name = (String)list.getModel().getElementAt(index);
                        if (!name.equals(userID)) {
                            if(JOptionPane.showConfirmDialog(ChatBox.this,
                                    "Are you sure you want to kick " + name + " out?",
                                    "Warning", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                                try {
                                    askQuit(name);
                                } catch (IOException e) {
                                    System.err.println("Unable to remove: " + name);
                                }
                            }
                        }
                    }
                }
            });
        }

        // initial chat box
        chatArea = remoteServer.getChatArea();
        chatArea.setEditable(false);
        JScrollPane chatScrollPane = new JScrollPane(chatArea);
        chatScrollPane.setPreferredSize(new Dimension(200, 200));

        // initial chat input box
        JPanel chatInputPanel = new JPanel();
        chatInputField = new JTextField();
        submitButton = new JButton("Enter");
        submitButton.setPreferredSize(new Dimension(50, 10));

        // listen submit button
        submitButton.addActionListener(e -> {
            try {
                sendMessage();
            } catch (IOException ex) {
                System.err.println("IO ERROR");
            }
        });

        chatInputPanel.setLayout(new BorderLayout());
        chatInputPanel.add(chatInputField, BorderLayout.CENTER);
        chatInputPanel.add(submitButton, BorderLayout.EAST);
        chatInputPanel.setPreferredSize(new Dimension(200, 200));

        // add split panel
        JSplitPane bottomSplitPane = new JSplitPane(
                JSplitPane.VERTICAL_SPLIT,
                chatScrollPane,
                chatInputPanel
        );
        bottomSplitPane.setDividerLocation(250);
        bottomSplitPane.setResizeWeight(1);

        JSplitPane splitPane = new JSplitPane(
                JSplitPane.VERTICAL_SPLIT,
                userListScrollPane,
                bottomSplitPane
        );
        splitPane.setDividerLocation(150);
        splitPane.setResizeWeight(0);

        add(splitPane, BorderLayout.CENTER);
    }


    // get latest user list from server
    public void updateUserList() throws RemoteException {
        remoteServer.updateList();
    }

    // add new message to chat log box
    public void appendMessage(String message) {
        chatArea.append(message + "\n");
    }

    // send message to server
    private void sendMessage() throws IOException {
        String message = chatInputField.getText();
        if (!message.isEmpty()) {
            broadcastMessage(message);
            chatInputField.setText("");
        }
    }

    // send message to server
    public void broadcastMessage(String message) throws IOException {
        remoteServer.broadcastMessage(message, userID);
    }

    // get message from server
    public void syncMessage(String message) {
        SwingUtilities.invokeLater(() -> {
            appendMessage(message);
        });
    }

    // send join message to server
    public void broadcastJoinMessage(String message) throws IOException {
        remoteServer.broadcastSystemMessage(message);
    }

    // send join message to server
    public void joinMessage() throws IOException {
        broadcastJoinMessage("SYSTEM: " + userID + " joined");
    }

    // update user list from server
    public void syncList(DefaultListModel<String> tempModel) {
        SwingUtilities.invokeLater(() -> {
            userModel.clear();
            for (int i = 0; i < tempModel.getSize(); i++) {
                userModel.addElement(tempModel.getElementAt(i));
            }
            userList.revalidate();
            userList.repaint();
        });
    }

    // manager double-click the user list and ask someone to leave
    private void askQuit(String name) throws IOException {
        remoteServer.askQuit(name);
        userModel.removeElement(name);
        remoteServer.broadcastSystemMessage("SYSTEM: Manager kick out " + name);
    }
}

