package remote;

import java.io.IOException;
import java.rmi.Remote;
import java.rmi.RemoteException;

public interface IRemoteClient extends Remote {
    void init() throws RemoteException;
    String getName() throws RemoteException;
    void updateCanvas(byte[] imageData) throws IOException;
    void syncCanvas(IRemoteCanvas remoteCanvas) throws IOException;
}
