package remote;

import whiteBoard.WhiteBoardGUI;

import javax.swing.*;
import java.io.IOException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class RemoteClient extends UnicastRemoteObject implements IRemoteClient {
    private String name;
    private boolean isManager;
    private IRemoteServer remoteCanvas;
    private WhiteBoardGUI whiteBoardGUI;

    public RemoteClient(String name, boolean isManager, IRemoteServer remoteServer) throws RemoteException {
        this.name = name;
        this.isManager = isManager;
        this.remoteCanvas = remoteServer;
    }

    @Override
    public void init() throws IOException {
        whiteBoardGUI = new WhiteBoardGUI(name, isManager, remoteCanvas);
    }

    @Override
    public String getName() throws RemoteException {
        return name;
    }

    @Override
    public void updateCanvas(byte[] imageData) throws IOException {
    }

    @Override
    public void syncCanvas(IRemoteCanvas remoteCanvas) throws RemoteException {
        whiteBoardGUI.syncCanvas(remoteCanvas);
    }

    @Override
    public void syncMessage(String message) throws RemoteException {
        whiteBoardGUI.syncMessage(message);
    }

    @Override
    public void askQuit(String managerName) throws RemoteException {
        whiteBoardGUI.askQuit(managerName);
    }

    @Override
    public void syncList(DefaultListModel<String> tempModel) throws RemoteException {
        whiteBoardGUI.syncList(tempModel);
    }

    @Override
    public void askUpdateList() throws RemoteException {
        whiteBoardGUI.askUpdateList();
    }

    @Override
    public void askJoinMessage() throws IOException {
        whiteBoardGUI.askJoinMessage();
    }

    @Override
    public void askCleanCanvas() throws RemoteException {
        whiteBoardGUI.askCleanCanvas();
    }

}
