package remote;

import whiteBoard.WhiteBoardGUI;

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
    public void init() throws RemoteException {
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

}
