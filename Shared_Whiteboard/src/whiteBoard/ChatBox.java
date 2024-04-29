package whiteBoard;

import remote.IRemoteServer;

import javax.swing.*;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ChatBox extends JPanel {
    private JList<String> userList;
    private DefaultListModel<String> userModel;
    private JTextArea chatArea;
    private JTextField chatInputField;
    private JButton submitButton;
    private IRemoteServer remoteServer;
    private final String userID;
    private final boolean isManager;

    public ChatBox(IRemoteServer remoteServer, String userID, boolean isManager) {
        this.remoteServer = remoteServer;
        this.userID = userID;
        this.isManager = isManager;

        init();
        appendMessage(getCurrentTime() + userID + " joined");
        updateUserList(userID);
    }

    public void init() {
        setLayout(new BorderLayout());

        // 用户列表的初始化
        userModel = new DefaultListModel<>();
        userList = new JList<>(userModel);
        JScrollPane userListScrollPane = new JScrollPane(userList);
        userListScrollPane.setPreferredSize(new Dimension(200, 200)); // 设置用户列表首选尺寸

        // 聊天区域的初始化
        chatArea = new JTextArea();
        chatArea.setEditable(false);
        JScrollPane chatScrollPane = new JScrollPane(chatArea);
        chatScrollPane.setPreferredSize(new Dimension(200, 400)); // 设置聊天区域首选尺寸

        // 聊天输入区域的初始化
        JPanel chatInputPanel = new JPanel();
        chatInputField = new JTextField(); // 设置合适的列数或使用setPreferredSize
        submitButton = new JButton("Enter");
        submitButton.setPreferredSize(new Dimension(50, 10));

        submitButton.addActionListener(e -> sendMessage());

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
        bottomSplitPane.setDividerLocation(350); // 聊天区域与输入区域的分割线位置
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
    public void updateUserList(String user) {
        userModel.addElement(user);
    }

    // 添加消息到聊天区域的方法
    public void appendMessage(String message) {
        chatArea.append(message + "\n");
    }

    private String getCurrentTime() {
        SimpleDateFormat formatter = new SimpleDateFormat("HH:mm");
        return formatter.format(new Date()) + ": ";
    }

    public void sendMessage() {
        String message = chatInputField.getText();
        if (!message.isEmpty()) {
            appendMessage(getCurrentTime() + userID + ": " + message);
            chatInputField.setText("");
        }
    }

}

