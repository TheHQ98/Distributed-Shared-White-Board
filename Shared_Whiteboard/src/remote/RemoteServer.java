/**
 * RMI Server
 *
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
    private static BufferedImage image;       // server side will back up latest canvas image
    private String managerName;               // store manager name
    private Set<IRemoteClient> userList;      // store all the users
    private ServerDB serverDB;

    public RemoteServer() throws RemoteException {
        super();
        this.userList = Collections.newSetFromMap(new ConcurrentHashMap<>());
        serverDB = new ServerDB();
        serverDB.init();
    }

    // send canvas image to new user
    @Override
    public byte[] updateImage() throws IOException {
        if (image != null) {
            ByteArrayOutputStream data = new ByteArrayOutputStream();
            ImageIO.write(image, "png", data);
            return data.toByteArray();
        }
        return null;
    }

    // get the latest canvas image from user side
    @Override
    public void getImage(byte[] imageData) throws IOException {
        image = byteArrayToImage(imageData);
    }

    // ask all user to sync canvas
    @Override
    public void broadcastCanvas(IRemoteCanvas remoteCanvas) throws IOException {
        for (IRemoteClient client : userList) {
            if (client.getUserID().equals(remoteCanvas.getUserID())) {
            } else {
                client.syncCanvas(remoteCanvas);
            }
        }
    }

    // convert bufferedImage to byteArray
    @Override
    public byte[] imageToByteArray(BufferedImage image) throws IOException {
        ByteArrayOutputStream data = new ByteArrayOutputStream();
        ImageIO.write(image, "png", data);
        return data.toByteArray();
    }

    // convert byteArray to bufferedImage
    @Override
    public BufferedImage byteArrayToImage(byte[] imageData) throws IOException {
        ByteArrayInputStream data = new ByteArrayInputStream(imageData);
        return ImageIO.read(data);
    }

    // sign in new user
    @Override
    public void signIn(IRemoteClient remoteClient) throws RemoteException {
        userList.add(remoteClient);
    }

    // set manager name
    @Override
    public void setManagerName(String name) throws RemoteException {
        this.managerName = name;
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                serverDB.setManagerName(name);
            }
        });
    }

    // add new username into serverDB
    @Override
    public void addUser(String name) throws RemoteException {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                serverDB.updateUserList(name);
            }
        });
    }

    // remove username from serverDB
    @Override
    public void removeUser(String name) throws IOException {
        for (IRemoteClient client : userList) {
            if (client.getUserID().equals(name)) {
                userList.remove(client);
                serverDB.removeUser(name);
                break;
            }
        }
        updateList();
        broadcastSystemMessage("SYSTEM: " + name  + " has left.");
    }

    // manager quit, ask all user to quit
    @Override
    public void managerLeave() throws RemoteException {
        for (IRemoteClient client : userList) {
            if (client.getUserID().equals(managerName)) {
            } else {
                userList.remove(client);
                SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                        try {
                            serverDB.removeUser(client.getUserID());
                        } catch (RemoteException e) {
                            System.err.println("RMI Error");
                        }
                    }
                });
                client.askQuit(managerName);
            }
        }

        for (IRemoteClient client : userList) {
            if (client.getUserID().equals(managerName)) {
                userList.remove(client);
                SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                        serverDB.removeManager(managerName);
                    }
                });
            }
        }
    }

    // sync message to all user
    @Override
    public void broadcastMessage(String message, String name) throws IOException {
        for (IRemoteClient client : userList) {
            if (client.getUserID().equals(name)) {
                client.syncMessage("You: " + message);
            } else {
                client.syncMessage(name + ": " + message);
            }
        }
        updateChatArea(name + ": " + message);
    }

    // sync system message to all user
    @Override
    public void broadcastSystemMessage(String message) throws IOException {
        for (IRemoteClient client : userList) {
            client.syncMessage(message);
        }
        updateChatArea(message);
    }

    // update user list to all user
    @Override
    public void updateList() throws RemoteException {
        DefaultListModel<String> tempModel = serverDB.getList();
        for (IRemoteClient client : userList) {
            client.syncList(tempModel);
        }
    }

    // ask someone to quit
    @Override
    public void askQuit(String name) throws RemoteException {
        for (IRemoteClient client : userList) {
            if (client.getUserID().equals(name)) {
                userList.remove(client);
                try {
                    serverDB.removeUser(client.getUserID());
                } catch (RemoteException e) {
                    System.err.println("RMI Error");
                }
                client.askQuit(managerName);
            }
        }
        updateList();
    }

    // serverDB update chat log
    @Override
    public void updateChatArea(String message) throws RemoteException {
        serverDB.updateCharArea(message);
    }

    // get chat log from serverDB
    @Override
    public JTextArea getChatArea() throws RemoteException {
        return serverDB.getChatArea();
    }

    // ask all user to open a new canvas
    @Override
    public void newCanvas() throws IOException {
        for (IRemoteClient client : userList) {
            client.askCleanCanvas();
        }
        broadcastSystemMessage("SYSTEM: Manager opened a new canvas.");
    }

    // manager opened a exist canvas, ask all user to update canvas
    @Override
    public void updateCanvas() throws IOException {
        for (IRemoteClient client : userList) {
            if (client.getUserID().equals(managerName)) {
            } else {
                client.getCanvasFromServer(imageToByteArray(image));
            }
        }
    }

    // manager close the canvas, ask all user to close canvas
    @Override
    public void closeCanvas() throws IOException {
        for (IRemoteClient client : userList) {
            try {
                if (!client.getUserID().equals(managerName)) {
                    Thread t = new Thread(() -> {
                        try {
                            client.askCloseCanvas();
                        } catch (RemoteException e) {
                            System.err.println("RMI Error");
                        }
                    });
                    t.start();
                }
            } catch (RemoteException e) {
                System.err.println("RMI Error");
            }
        }
        broadcastSystemMessage("SYSTEM: Manager closed canvas.");
    }

    // new user ask manager to get access
    @Override
    public boolean askAccess(String name) throws RemoteException {
        for (IRemoteClient client : userList) {
            if (client.getUserID().equals(managerName)) {
                return client.askRequest(name);
            }
        }
        return false;
    }

    // check is the new username already exist in the server
    @Override
    public boolean checkName(String name) throws RemoteException {
        if (name.equals(managerName)){
            return true;
        }
        for (IRemoteClient client : userList) {
            if (client.getUserID().equals(name)) {
                return true;
            }
        }
        return false;
    }

    // check is the manager is closed canvas
    @Override
    public boolean getIsClosedState() throws RemoteException {
        for (IRemoteClient client : userList) {
            if (client.getUserID().equals(managerName)) {
                return client.getIsClosedState();
            }
        }
        return false;
    }

    // user shut down the application
    @Override
    public void userClose(String name) throws IOException {
        removeUser(name);
    }
}
