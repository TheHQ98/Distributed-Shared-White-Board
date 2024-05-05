package server;

import remote.IRemoteServer;
import remote.RemoteServer;
import whiteBoard.ClientParams;

import javax.swing.*;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class Server {
    private final String[] args;

    public Server(String[] args) {
        this.args = args;
    }

    public void start() {
        try {
            IRemoteServer remoteServer = new RemoteServer();
            Registry registry = LocateRegistry.createRegistry(Integer.parseInt(args[0]));
            registry.bind(ClientParams.REGISTRY_NAME, remoteServer);
            System.out.println("RMI Ready");
        } catch (java.rmi.server.ExportException e) {
            System.err.println("Server exception: Port " + args[0] + " is already in use. Trying another port...");
            JOptionPane.showMessageDialog(null, "Port number " + args[0] +
                    " is already in use, try other number. ", "Warning", JOptionPane.WARNING_MESSAGE);
            System.exit(0);
        } catch (Exception e) {
            System.err.println("Server exception: " + e.getMessage());
            JOptionPane.showMessageDialog(null, "RMI ERROR",
                    "Warning", JOptionPane.WARNING_MESSAGE);
            System.exit(0);
        }
    }
}
