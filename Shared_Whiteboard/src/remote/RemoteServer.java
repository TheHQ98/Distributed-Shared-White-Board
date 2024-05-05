/**
 * @author Josh Feng, 1266669, chenhaof@student.unimelb.edu.au
 * @date 27 April 2024
 */

package remote;

import server.ServerDB;

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
    private ServerDB serverDB;

    public RemoteServer() throws RemoteException {
        super();
        this.userList = Collections.newSetFromMap(new ConcurrentHashMap<>());

        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                serverDB = new ServerDB();
                serverDB.init();
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
        // TODO no need name
    }

    @Override
    public void getCanvas(IRemoteCanvas remoteCanvas) throws IOException {
        for (IRemoteClient client : userList) {
            if (client.getName().equals(remoteCanvas.getName())) {
            } else {
                client.syncCanvas(remoteCanvas);
            }
        }
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
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                serverDB.setManagerName(name);
            }
        });
    }

    @Override
    public void addUser(String name) throws RemoteException {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                serverDB.updateUserList(name);
            }
        });
    }

    @Override
    public void removeUser(String name) throws IOException {
        for (IRemoteClient client : userList) {
            if (client.getName().equals(name)) {
                userList.remove(client);
                serverDB.removeUser(name);
                break;
            }
        }
        updateList();
        broadcastSystemMessage("SYSTEM: " + name  + " has left.");
    }

    @Override
    public void managerLeave() throws RemoteException {
        for (IRemoteClient client : userList) {
            if (client.getName().equals(managerName)) {
            } else {
                userList.remove(client);
                SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                        try {
                            serverDB.removeUser(client.getName());
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
                        serverDB.removeManager(managerName);
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

        //TODO Send message to database
        updateCharArea(name + ": " + message);
    }

    @Override
    public void broadcastSystemMessage(String message) throws IOException {
        for (IRemoteClient client : userList) {
            client.syncMessage(message);
        }
        updateCharArea(message);
    }

    @Override
    public void updateList() throws RemoteException {
        DefaultListModel<String> tempModel = serverDB.getList();
        for (IRemoteClient client : userList) {
            client.syncList(tempModel);
        }
    }

    @Override
    public void askQuit(String name) throws RemoteException {
        for (IRemoteClient client : userList) {
            if (client.getName().equals(name)) {
                userList.remove(client);
                try {
                    serverDB.removeUser(client.getName());
                } catch (RemoteException e) {
                    System.err.println("RMI Error");
                }
                client.askQuit(managerName);
            }
        }
        updateList();
    }

    @Override
    public void updateCharArea(String message) throws RemoteException {
        serverDB.updateCharArea(message);
    }

    @Override
    public JTextArea getChatArea() throws RemoteException {
        return serverDB.getChatArea();
    }

    @Override
    public void newCanvas() throws IOException {
        for (IRemoteClient client : userList) {
            client.askCleanCanvas();
        }
        broadcastSystemMessage("SYSTEM: Manager opened a new canvas.");
    }

    @Override
    public void updateCanvas() throws IOException {
        for (IRemoteClient client : userList) {
            if (client.getName().equals(managerName)) {
            } else {
                client.getCanvasFromServer(imageToByteArray(frame));
            }
        }
    }

    @Override
    public void closeCanvas() throws IOException {
        for (IRemoteClient client : userList) {
            try {
                if (!client.getName().equals(managerName)) {
                    Thread t = new Thread(() -> {
                        try {
                            client.askCloseCanvas();
                        } catch (RemoteException e) {
                            throw new RuntimeException(e);
                        }
                    });
                    t.start();
                }
            } catch (RemoteException e) {
                throw new RuntimeException(e);
            }
        }
        broadcastSystemMessage("SYSTEM: Manager closed canvas.");
    }

    @Override
    public boolean askAccess(String name) throws RemoteException {
        for (IRemoteClient client : userList) {
            if (client.getName().equals(managerName)) {
                return client.askRequest(name);
            }
        }
        return false;
    }

    @Override
    public boolean checkName(String name) throws RemoteException {
        if (name.equals(managerName)){
            return true;
        }

        for (IRemoteClient client : userList) {
            if (client.getName().equals(name)) {
                return true;
            }
        }

        return false;
    }

    @Override
    public boolean getIsClosedState() throws RemoteException {
        for (IRemoteClient client : userList) {
            if (client.getName().equals(managerName)) {
                return client.getIsClosedState();
            }
        }
        return false;
    }

    @Override
    public void userClose(String name) throws IOException {
        removeUser(name);
    }
}
