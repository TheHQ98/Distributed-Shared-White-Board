/**
* @author Josh Feng, 1266669, chenhaof@student.unimelb.edu.au
* @date 18 April 2024
*/

import remote.IRemoteClient;
import remote.RemoteClient;
import remote.IRemoteServer;
import server.Server;

import javax.swing.*;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class CreateWhiteBoard {
    private static String name = "Manager";
    public static void main(String[] args) {
        // Start server
        Server server = new Server(args);
        server.start();

        // Start Manager Whiteboard
        try {
            Registry registry = LocateRegistry.getRegistry("localhost", Integer.parseInt(args[0]));

            IRemoteServer remoteServer = (IRemoteServer) registry.lookup("SharedWhiteBoard");
            IRemoteClient remoteClient = new RemoteClient(name, true, remoteServer);

            remoteServer.setManagerName(name);
            remoteServer.signIn(remoteClient);
            remoteClient.init();
            System.out.println("Client connected to server");
            remoteClient.askUpdateList();
            remoteClient.askJoinMessage();
            System.out.println("Manager Whiteboard ready");
        } catch (Exception e) {
            System.err.println("ERROR: " + e);
            JOptionPane.showMessageDialog(null, "Error occurs",
                    "Warning", JOptionPane.WARNING_MESSAGE);
            System.exit(0);
        }
    }
}
