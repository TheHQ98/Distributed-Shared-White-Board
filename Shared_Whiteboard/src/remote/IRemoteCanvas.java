/**
 * Interface for remote canvas, use for collect user data for draw detail
 *
 * @author Josh Feng, 1266669, chenhaof@student.unimelb.edu.au
 * @date 27 April 2024
 */

package remote;

import java.awt.*;
import java.rmi.Remote;
import java.rmi.RemoteException;

public interface IRemoteCanvas extends Remote {
    String getToolType() throws RemoteException;
    Color getColor() throws RemoteException;
    Point getStartPoint() throws RemoteException;
    Point getEndPoint() throws RemoteException;
    String getUserID() throws RemoteException;
    String getText() throws RemoteException;
    Integer getTextSize() throws RemoteException;
    float getEraserSize() throws RemoteException;
}
