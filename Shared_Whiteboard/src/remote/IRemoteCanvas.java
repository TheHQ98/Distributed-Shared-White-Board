package remote;

import java.awt.*;
import java.rmi.Remote;
import java.rmi.RemoteException;

public interface IRemoteCanvas extends Remote {
    String getToolType() throws RemoteException;
    Color getColor() throws RemoteException;
    Point getStartPoint() throws RemoteException;
    Point getEndPoint() throws RemoteException;
    String getName() throws RemoteException;
    String getText() throws RemoteException;
    Integer getTextSize() throws RemoteException;
    float getEraserSize() throws RemoteException;
}
