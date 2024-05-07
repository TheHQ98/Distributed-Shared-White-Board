/**
 * This class is use for manager to start the whiteboard
 * Server will first start, then manager whiteboard GUI will start
 * Must include arguments: <serverIPAddress> <serverPort> <username>
 *
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
    private static String serverIPAddress;
    private static int serverPort;
    private static String username;

    public static void main(String[] args) {
        // check arguments
        checkArgs(args);
        serverIPAddress = args[0];
        try {
            serverPort = Integer.parseInt(args[1]);
        } catch (NumberFormatException e) {
            System.err.println(e.getMessage());
            JOptionPane.showMessageDialog(null, "Port number must be an integer: " + args[1],
                    "Warning", JOptionPane.WARNING_MESSAGE);
            System.exit(0);
        }
        username = args[2];

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
            remoteClient.systemJoinMessage();
            System.out.println("Manager Whiteboard ready");
        } catch (Exception e) {
            System.err.println("ERROR: " + e);
            JOptionPane.showMessageDialog(null, "Error occurs",
                    "Warning", JOptionPane.WARNING_MESSAGE);
            System.exit(0);
        }
    }

    // check arguments is enough
    private static void checkArgs(String[] args) {
        if (args.length != 3) {
            JOptionPane.showMessageDialog(null, "Arguments is not enough.\n" +
                            "Format: <serverIPAddress> <serverPort> <username>",
                    "Warning", JOptionPane.WARNING_MESSAGE);
            System.exit(0);
        }
    }
}
