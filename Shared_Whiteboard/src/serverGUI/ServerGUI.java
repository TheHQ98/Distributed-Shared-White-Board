package serverGUI;

import javax.swing.*;
import javax.swing.JPanel;
import java.awt.*;

public class ServerGUI extends JFrame {
    private JList<String> managerList;
    private JList<String> userList;
    private DefaultListModel<String> managerModel;
    private DefaultListModel<String> userModel;

    public ServerGUI() {
        setTitle(ServerParams.GUI_TITLE);
        setSize(ServerParams.GUI_WIDTH, ServerParams.GUI_HEIGHT);
        setLocationRelativeTo(null);
        setVisible(true);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    public void init() {

        // 设置布局管理器为 BoxLayout，沿 Y 轴排列
        setLayout(new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));

        // 创建管理员列表和标签
        managerModel = new DefaultListModel<>();
        managerList = new JList<>(managerModel);
        JScrollPane managerScrollPane = new JScrollPane(managerList);

        // 创建用户列表和标签
        userModel = new DefaultListModel<>();
        userList = new JList<>(userModel);
        JScrollPane userScrollPane = new JScrollPane(userList);
        JLabel userLabel = new JLabel("Users");
        userLabel.setAlignmentX(Component.CENTER_ALIGNMENT); // 将标签居中对齐

        // 设置首选高度为30像素
        managerScrollPane.setPreferredSize(new Dimension(Integer.MAX_VALUE, 30));
        JLabel managerLabel = new JLabel("Manager");
        managerLabel.setAlignmentX(Component.CENTER_ALIGNMENT); // 将标签居中对齐

        // 创建包含标签和列表的面板
        JPanel managerPanel = new JPanel();
        managerPanel.setLayout(new BoxLayout(managerPanel, BoxLayout.Y_AXIS));
        managerPanel.add(managerLabel);
        managerPanel.add(managerScrollPane);


        JPanel userPanel = new JPanel();
        userPanel.setLayout(new BoxLayout(userPanel, BoxLayout.Y_AXIS));
        userPanel.add(userLabel);
        userPanel.add(userScrollPane);

        // 将面板添加到主窗口
        add(managerPanel);
        add(userPanel);
    }

    public void setManagerName(String manager) {
        SwingUtilities.invokeLater(() -> {
            managerModel.clear();
            if (manager != null && !manager.trim().isEmpty()) {
                managerModel.addElement(manager);
                managerList.revalidate();
                managerList.repaint();
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
        System.out.println(tempModel.size());
        return tempModel;
    }

}
