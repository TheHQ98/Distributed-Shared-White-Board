/**
 * @author Josh Feng, 1266669, chenhaof@student.unimelb.edu.au
 * @date 27 April 2024
 */

package remote;

import javax.swing.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.rmi.Remote;
import java.rmi.RemoteException;

public interface IRemoteServer extends Remote {
    byte[] updateImage() throws IOException;
    void getImage(byte[] imageData, String name) throws IOException;
    void getCanvas(IRemoteCanvas remoteCanvas) throws IOException;
    byte[] imageToByteArray(BufferedImage image) throws IOException;
    BufferedImage byteArrayToImage(byte[] imageData) throws IOException;
    void signIn(IRemoteClient remoteClient) throws RemoteException;
    void setManagerName(String name) throws RemoteException;
    void addUser(String name) throws RemoteException;
    void removeUser(String name) throws IOException;
    void managerLeave() throws RemoteException;
    void broadcastMessage(String message, String name) throws IOException;
    void broadcastSystemMessage(String message) throws IOException;
    void updateList() throws RemoteException;
    void askQuit(String name) throws RemoteException;
    void updateCharArea(String message) throws RemoteException;
    JTextArea getChatArea() throws RemoteException;
    void newCanvas() throws IOException;
    void updateCanvas() throws IOException;
    void closeCanvas() throws IOException;
    boolean askAccess(String name) throws RemoteException;
    boolean checkName(String name) throws RemoteException;
    boolean getIsClosedState() throws RemoteException;
    void userClose(String name) throws IOException;
}
