/**
 * Server database
 * user for store the manager and user list, also store chat log
 *
 * @author Josh Feng, 1266669, chenhaof@student.unimelb.edu.au
 * @date 27 April 2024
 */

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

    // store manager name
    public void setManagerName(String manager) {
        SwingUtilities.invokeLater(() -> {
            managerModel.clear();
            if (manager != null && !manager.trim().isEmpty()) {
                managerModel.addElement(manager);
            }
        });
    }

    // add new username into userModel
    public void updateUserList(String user) {
        userModel.addElement(user);
    }

    // remove username from userModel
    public void removeUser(String user) {
        userModel.removeElement(user);
    }

    // remove manager from managerModel
    public void removeManager(String manager) {
        managerModel.removeElement(manager);
    }

    // get all usernames from userModel and managerModel
    public DefaultListModel<String> getList() {
        DefaultListModel<String> tempModel = new DefaultListModel<>();

        for (int i = 0; i < managerModel.size(); i++) {
            tempModel.addElement(managerModel.get(i));
        }
        for (int i = 0; i < userModel.size(); i++) {
            tempModel.addElement(userModel.get(i));
        }

        return tempModel;
    }

    // add new message into chatArea
    public void updateCharArea(String message) {
        SwingUtilities.invokeLater(() -> {
            chatArea.append(message + "\n");
        });
    }

    // get chat log
    public JTextArea getChatArea() {
        return chatArea;
    }

}
