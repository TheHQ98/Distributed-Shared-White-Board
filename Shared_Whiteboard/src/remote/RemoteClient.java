/**
 * Interface for remote client, each user will create a remote client
 * Server will get remote client and ask for sync canvas and other order
 *
 * @author Josh Feng, 1266669, chenhaof@student.unimelb.edu.au
 * @date 27 April 2024
 */

package remote;

import whiteBoard.WhiteBoardGUI;

import javax.swing.*;
import java.io.IOException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class RemoteClient extends UnicastRemoteObject implements IRemoteClient {
    private String userID;
    private boolean isManager;
    private IRemoteServer remoteCanvas;
    private WhiteBoardGUI whiteBoardGUI;

    public RemoteClient(String userID, boolean isManager, IRemoteServer remoteServer) throws RemoteException {
        this.userID = userID;
        this.isManager = isManager;
        this.remoteCanvas = remoteServer;
    }

    @Override
    public void init() throws IOException {
        whiteBoardGUI = new WhiteBoardGUI(userID, isManager, remoteCanvas);
    }

    @Override
    public String getUserID() throws RemoteException {
        return userID;
    }

    // get latest canvas from server
    @Override
    public void syncCanvas(IRemoteCanvas remoteCanvas) throws RemoteException {
        whiteBoardGUI.syncCanvas(remoteCanvas);
    }

    // get history of chat log from server
    @Override
    public void syncMessage(String message) throws RemoteException {
        whiteBoardGUI.syncMessage(message);
    }

    // manager ask this user to leave
    @Override
    public void askQuit(String managerName) throws RemoteException {
        whiteBoardGUI.askQuit(managerName);
    }

    // get user list from server
    @Override
    public void syncList(DefaultListModel<String> tempModel) throws RemoteException {
        whiteBoardGUI.syncList(tempModel);
    }

    // server ask user to update user list
    @Override
    public void askUpdateList() throws RemoteException {
        whiteBoardGUI.askUpdateList();
    }

    // broadcast a user joined message
    @Override
    public void systemJoinMessage() throws IOException {
        whiteBoardGUI.askJoinMessage();
    }

    // when manager open a new canvas, ask all users to clean canvas
    @Override
    public void askCleanCanvas() throws RemoteException {
        whiteBoardGUI.askCleanCanvas();
    }

    // when manager open an exist canvas, ask all users to get canvas
    @Override
    public void getCanvasFromServer(byte[] imageData) throws IOException {
        whiteBoardGUI.askGetCanvasFromServer(imageData);
    }

    // when manager press close button, ask all users to close
    @Override
    public void askCloseCanvas() throws RemoteException {
        whiteBoardGUI.askCloseCanvas();
    }

    // ask manager to get access
    @Override
    public boolean askRequest(String name) throws RemoteException {
        return whiteBoardGUI.requestAccess(name);
    }

    // check is manager already closed canvas
    @Override
    public boolean getIsClosedState() throws RemoteException {
        return whiteBoardGUI.getIsClosedState();
    }

}
