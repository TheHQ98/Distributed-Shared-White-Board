/**
* @author Josh Feng, 1266669, chenhaof@student.unimelb.edu.au
* @date 18 April 2024
*/

import remote.IRemoteClient;
import remote.RemoteClient;
import remote.IRemoteServer;
import server.Server;
import whiteBoard.ClientParams;

import javax.swing.*;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class CreateWhiteBoard {
    public static void main(String[] args) {
        // check arguments
        checkArgs(args);
        String serverIPAddress = args[0];
        int serverPort = Integer.parseInt(args[1]);
        String username = args[2];

        // Start server
        Server server = new Server(serverPort);
        server.start();

        // start Manager Whiteboard
        try {
            Registry registry = LocateRegistry.getRegistry(serverIPAddress, serverPort);

            IRemoteServer remoteServer = (IRemoteServer) registry.lookup(ClientParams.REGISTRY_NAME);
            IRemoteClient remoteClient = new RemoteClient(username, true, remoteServer);

            remoteServer.setManagerName(username);
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

    private static void checkArgs(String[] args) {
        if (args.length != 3) {
            JOptionPane.showMessageDialog(null, "Arguments is not enough.\n" +
                            "Format: <serverIPAddress> <serverPort> <username>",
                    "Warning", JOptionPane.WARNING_MESSAGE);
            System.exit(0);
        }
    }
}
