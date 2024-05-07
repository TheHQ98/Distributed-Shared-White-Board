/**
 * Interface for remote client, each user will create a remote client
 * Server will get remote client and ask for sync canvas and other order
 *
 * @author Josh Feng, 1266669, chenhaof@student.unimelb.edu.au
 * @date 27 April 2024
 */

package remote;

import javax.swing.*;
import java.io.IOException;
import java.rmi.Remote;
import java.rmi.RemoteException;

public interface IRemoteClient extends Remote {
    void init() throws IOException;
    String getUserID() throws RemoteException;
    void syncCanvas(IRemoteCanvas remoteCanvas) throws IOException;
    void syncMessage(String message) throws IOException;
    void askQuit(String managerName) throws RemoteException;
    void syncList(DefaultListModel<String> tempModel) throws RemoteException;
    void askUpdateList() throws RemoteException;
    void systemJoinMessage() throws IOException;
    void askCleanCanvas() throws RemoteException;
    void getCanvasFromServer(byte[] imageData) throws IOException;
    void askCloseCanvas() throws RemoteException;
    boolean askRequest(String name) throws RemoteException;
    boolean getIsClosedState() throws RemoteException;
}
