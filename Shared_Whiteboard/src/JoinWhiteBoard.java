/**
 * This class is for user to join the manager's whiteboard
 * Must include arguments: <serverIPAddress> <serverPort> <username>
 *
 * @author Josh Feng, 1266669, chenhaof@student.unimelb.edu.au
 * @date 22 April 2024
 */

import remote.IRemoteClient;
import remote.RemoteClient;
import remote.IRemoteServer;
import whiteBoard.ClientParams;

import javax.swing.*;
import java.io.IOException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class JoinWhiteBoard {
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

        // try to connect to the server, and open whiteboard
        try {
            Registry registry = LocateRegistry.getRegistry(serverIPAddress, serverPort);
            IRemoteServer remoteServer = (IRemoteServer) registry.lookup(ClientParams.REGISTRY_NAME);

            // check is username is existed
            if (remoteServer.checkName(username)) {
                JOptionPane.showMessageDialog(null, "Username already exists: " + username +
                        "\n" + "Please try other name.", "Warning", JOptionPane.WARNING_MESSAGE);
                System.exit(0);
            }

            // check is manager has closed canvas
            while (remoteServer.getIsClosedState()) {
                Object[] options = {"Retry", "Close"};
                int answer = JOptionPane.showOptionDialog(null,
                        "Manager has not opened a new file yet.",
                        "From manager",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.WARNING_MESSAGE,
                        null,
                        options,
                        options[0]);
                if (answer == JOptionPane.NO_OPTION) {
                    System.exit(0);
                }
            }

            // ask manager to access
            boolean result = remoteServer.askAccess(username);
            if (!result) {
                JOptionPane.showMessageDialog(null, "Access denied, please contact the manager",
                        "Warning", JOptionPane.WARNING_MESSAGE);
                System.exit(0);
            }

            // create whiteboard and open GUI
            IRemoteClient remoteClient = new RemoteClient(username, false, remoteServer);
            remoteServer.signIn(remoteClient);
            remoteServer.addUser(username);
            remoteClient.init();
            System.out.println("Client connected to server");
            remoteClient.askUpdateList();
            remoteClient.systemJoinMessage();

            // Using for catch user quit, user may normally close the app, or force quit the app
            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                try {
                    remoteServer.userClose(username);
                } catch (IOException ignored) {
                }
            }));
        } catch (Exception e) {
            System.err.println("Server not started");
            JOptionPane.showMessageDialog(null, "Server not started." +
                    " Manager have not create white board yet.", "Warning", JOptionPane.WARNING_MESSAGE);
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
