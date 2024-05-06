package server;

import javax.swing.*;

public class ServerDB extends JFrame {
    private DefaultListModel<String> managerModel;
    private DefaultListModel<String> userModel;
    private JTextArea chatArea;

    public ServerDB() {}

    public void init() {
        managerModel = new DefaultListModel<>();
        userModel = new DefaultListModel<>();
        chatArea = new JTextArea();
    }

    public void setManagerName(String manager) {
        SwingUtilities.invokeLater(() -> {
            managerModel.clear();
            if (manager != null && !manager.trim().isEmpty()) {
                managerModel.addElement(manager);
            }
        });
    }

    public void updateUserList(String user) {
        userModel.addElement(user);
    }

    public void removeUser(String user) {
        userModel.removeElement(user);
    }

    public void removeManager(String manager) {
        managerModel.removeElement(manager);
    }

    public DefaultListModel<String> getList() {
        DefaultListModel<String> tempModel = new DefaultListModel<>();

        // 添加 managerModel 的所有元素
        for (int i = 0; i < managerModel.size(); i++) {
            tempModel.addElement(managerModel.get(i));
        }

        // 添加 userModel 的所有元素
        for (int i = 0; i < userModel.size(); i++) {
            tempModel.addElement(userModel.get(i));
        }
        //System.out.println(tempModel.size());
        return tempModel;
    }

    public void updateCharArea(String message) {
        SwingUtilities.invokeLater(() -> {
            chatArea.append(message + "\n");
        });
    }

    public JTextArea getChatArea() {
        return chatArea;
    }

}
