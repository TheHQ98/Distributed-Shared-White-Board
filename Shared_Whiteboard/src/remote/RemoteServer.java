/**
 * @author Josh Feng, 1266669, chenhaof@student.unimelb.edu.au
 * @date 27 April 2024
 */

package remote;

import serverGUI.ServerGUI;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.Collections;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class RemoteServer extends UnicastRemoteObject implements IRemoteServer {
    private static BufferedImage frame;
    private String managerName;
    private Set<IRemoteClient> userList;
    private ServerGUI serverGUI;

    public RemoteServer() throws RemoteException {
        super();
        this.userList = Collections.newSetFromMap(new ConcurrentHashMap<>());

        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                serverGUI = new ServerGUI();
                serverGUI.init();
            }
        });
    }

    @Override
    public byte[] updateImage() throws IOException {
        if (frame != null) {
            ByteArrayOutputStream data = new ByteArrayOutputStream();
            ImageIO.write(frame, "png", data);
            return data.toByteArray();
        }
        return null;
    }

    @Override
    public void getImage(byte[] imageData, String name) throws IOException {
        frame = byteArrayToImage(imageData);
        //syncCanvas(imageData, name);
    }

    @Override
    public void getCanvas(IRemoteCanvas remoteCanvas) throws IOException {
        for (IRemoteClient client : userList) {
            if (client.getName().equals(remoteCanvas.getName())) {
            } else {
                //System.out.println("Update: " + client.getName());
                client.syncCanvas(remoteCanvas);
            }
        }
    }


    @Override
    public void printTest(String text) throws RemoteException {
        System.out.println(text);
    }

    @Override
    public byte[] imageToByteArray(BufferedImage image) throws IOException {
        ByteArrayOutputStream data = new ByteArrayOutputStream();
        ImageIO.write(image, "png", data);
        return data.toByteArray();
    }

    @Override
    public BufferedImage byteArrayToImage(byte[] imageData) throws IOException {
        ByteArrayInputStream data = new ByteArrayInputStream(imageData);
        return ImageIO.read(data);
    }

    @Override
    public void signIn(IRemoteClient remoteClient) throws RemoteException {
        userList.add(remoteClient);
    }

    @Override
    public void setManagerName(String name) throws RemoteException {
        this.managerName = name;
        for (IRemoteClient client : userList) {
            System.out.println(client.getName());
        }
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                serverGUI.setManagerName(name);
            }
        });
    }

    @Override
    public void addUser(String name) throws RemoteException {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                serverGUI.updateUserList(name);
            }
        });
    }

    @Override
    public void removeUser(String name) throws RemoteException {
        System.out.println(name + " request leave.");
        for (IRemoteClient client : userList) {
            if (client.getName().equals(name)) {
                userList.remove(client);
                SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                        serverGUI.removeUser(name);
                    }
                });
                break;
            }
        }
    }

    @Override
    public void getManagerName() throws RemoteException {
        System.out.println(managerName);
    }

    @Override
    public void getUserList(String name) throws IOException {
        for (IRemoteClient client : userList) {
            if (client.getName().equals(name)) {
            } else {
                client.updateCanvas(null);
            }
        }
    }

    @Override
    public void syncCanvas(byte[] imageData, String name) throws IOException {
        for (IRemoteClient client : userList) {
            if (client.getName().equals(name)) {
            } else {
                client.updateCanvas(null);
            }
        }
    }

    @Override
    public void managerLeave() throws RemoteException {
        for (IRemoteClient client : userList) {
            if (client.getName().equals(managerName)) {
            } else {
                System.out.println("Remove: " + client.getName());
                userList.remove(client);
                SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                        try {
                            serverGUI.removeUser(client.getName());
                        } catch (RemoteException e) {
                            throw new RuntimeException(e);
                        }
                    }
                });
                client.askQuit(managerName);
            }
        }

        for (IRemoteClient client : userList) {
            if (client.getName().equals(managerName)) {
                userList.remove(client);
                SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                        serverGUI.removeManager(managerName);
                    }
                });
            }
        }
    }

    @Override
    public void broadcastMessage(String message, String name) throws IOException {
        for (IRemoteClient client : userList) {
            if (client.getName().equals(name)) {
                client.syncMessage("You: " + message);
            } else {
                client.syncMessage(name + ": " + message);
            }
        }
    }

    @Override
    public void broadcastJoinMessage(String message) throws IOException {
        for (IRemoteClient client : userList) {
            client.syncMessage(message);
        }
    }

    @Override
    public void updateList() throws RemoteException {
        DefaultListModel<String> tempModel = serverGUI.getList();
        System.out.println(tempModel);
        for (IRemoteClient client : userList) {
            client.syncList(tempModel);
        }
    }

    @Override
    public void askQuit(String name) throws RemoteException {
        for (IRemoteClient client : userList) {
            if (client.getName().equals(name)) {
                System.out.println("Remove: " + client.getName());
                userList.remove(client);
                SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                        try {
                            serverGUI.removeUser(client.getName());
                        } catch (RemoteException e) {
                            throw new RuntimeException(e);
                        }
                    }
                });
                client.askQuit(managerName);
            }
        }

        updateList(); //TODO 有bug 列表不会实时显示
    }

}
