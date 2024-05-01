package remote;

import javax.swing.*;
import java.io.IOException;
import java.rmi.Remote;
import java.rmi.RemoteException;

public interface IRemoteClient extends Remote {
    void init() throws IOException;
    String getName() throws RemoteException;
    void updateCanvas(byte[] imageData) throws IOException;
    void syncCanvas(IRemoteCanvas remoteCanvas) throws IOException;
    void syncMessage(String message) throws IOException;
    void askQuit(String managerName) throws RemoteException;
    void syncList(DefaultListModel<String> tempModel) throws RemoteException;
    void askUpdateList() throws RemoteException;
    void askJoinMessage() throws IOException;
    void askCleanCanvas() throws RemoteException;
    void getCanvasFromServer(byte[] imageData) throws IOException;
}
