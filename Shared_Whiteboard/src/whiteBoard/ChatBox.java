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

        // 用户列表的初始化
        userModel = new DefaultListModel<>();
        userList = new JList<>(userModel);
        JScrollPane userListScrollPane = new JScrollPane(userList);
        userListScrollPane.setPreferredSize(new Dimension(200, 200));

        if (isManager) {
            userList.addMouseListener(new MouseAdapter() {
                public void mouseClicked(MouseEvent evt) {
                    // 获取点击的是哪个 JList
                    JList list = (JList)evt.getSource();

                    // 双击鼠标检测
                    if (evt.getClickCount() == 2) {
                        // 获取鼠标双击的项目的索引
                        int index = list.locationToIndex(evt.getPoint());

                        // 获取该索引处的元素
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
        chatScrollPane.setPreferredSize(new Dimension(200, 200)); // 设置聊天区域首选尺寸

        // 聊天输入区域的初始化
        JPanel chatInputPanel = new JPanel();
        chatInputField = new JTextField(); // 设置合适的列数或使用setPreferredSize
        submitButton = new JButton("Enter");
        submitButton.setPreferredSize(new Dimension(50, 10));

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
        chatInputPanel.setPreferredSize(new Dimension(200, 200)); // 设置输入面板首选尺寸

        // 聊天内容和输入区域的分割面板
        JSplitPane bottomSplitPane = new JSplitPane(
                JSplitPane.VERTICAL_SPLIT,
                chatScrollPane,
                chatInputPanel
        );
        bottomSplitPane.setDividerLocation(250); // 聊天区域与输入区域的分割线位置
        bottomSplitPane.setResizeWeight(1); // 聊天区域在窗口调整时获取额外空间

        // 总体分割面板（用户列表和下半部分的组合）
        JSplitPane splitPane = new JSplitPane(
                JSplitPane.VERTICAL_SPLIT,
                userListScrollPane,
                bottomSplitPane
        );
        splitPane.setDividerLocation(150); // 用户列表和聊天区域的分割线位置
        splitPane.setResizeWeight(0); // 用户列表在窗口调整时不获取额外空间

        // 添加总体分割面板到主面板
        add(splitPane, BorderLayout.CENTER);
    }


    // 更新用户列表的方法
    public void updateUserList() throws RemoteException {
//        userModel.addElement(user);
        remoteServer.updateList();
    }

    // 添加消息到聊天区域的方法
    public void appendMessage(String message) {
        chatArea.append(message + "\n");
    }

    private void sendMessage() throws IOException {
        String message = chatInputField.getText();
        if (!message.isEmpty()) {
            broadcastMessage(message);
            chatInputField.setText("");
        }
    }

    public void broadcastMessage(String message) throws IOException {
        remoteServer.broadcastMessage(message, userID);
    }

    public void syncMessage(String message) {
        SwingUtilities.invokeLater(() -> {
            appendMessage(message);
        });
    }

    public void broadcastJoinMessage(String message) throws IOException {
        remoteServer.broadcastSystemMessage(message);
    }

    public void joinMessage() throws IOException {
        broadcastJoinMessage("SYSTEM: " + userID + " joined");
    }

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

    private void askQuit(String name) throws IOException {
        remoteServer.askQuit(name);
        userModel.removeElement(name);
        remoteServer.broadcastSystemMessage("SYSTEM: Manager kick out " + name);
    }
}

