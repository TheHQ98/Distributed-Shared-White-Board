/**
 * @author Josh Feng, 1266669, chenhaof@student.unimelb.edu.au
 * @date 27 April 2024
 */

package remote;

import javax.imageio.ImageIO;
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

    public RemoteServer() throws RemoteException {
        super();
        this.userList = Collections.newSetFromMap(new ConcurrentHashMap<>());
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
    }

    @Override
    public void addUser(String name) throws RemoteException {

    }

    @Override
    public void removeUser(String name) throws RemoteException {

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

}
