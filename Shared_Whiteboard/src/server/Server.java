/**
 * Server side
 * Server will create a remoteServer and create registry
 *
 * @author Josh Feng, 1266669, chenhaof@student.unimelb.edu.au
 * @date 22 April 2024
 */

package server;

import remote.IRemoteServer;
import remote.RemoteServer;
import whiteBoard.ClientParams;

import javax.swing.*;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class Server {
    private final int serverPort;

    public Server(int serverPort) {
        this.serverPort = serverPort;
    }

    // create RemoteServer, create registry and bind
    public void start() {
        try {
            IRemoteServer remoteServer = new RemoteServer();
            Registry registry = LocateRegistry.createRegistry(serverPort);
            registry.bind(ClientParams.REGISTRY_NAME, remoteServer);
            System.out.println("RMI Ready");
        } catch (java.rmi.server.ExportException e) {
            System.err.println("Server exception: Port " + serverPort + " is already in use. Trying another port...");
            JOptionPane.showMessageDialog(null, "Port number " + serverPort +
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
