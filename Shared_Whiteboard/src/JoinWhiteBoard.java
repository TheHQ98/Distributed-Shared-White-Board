/**
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
    public static void main(String[] args) {
        checkArgs(args);

        try {
            Registry registry = LocateRegistry.getRegistry(args[0], Integer.parseInt(args[1]));

            IRemoteServer remoteServer = (IRemoteServer) registry.lookup(ClientParams.REGISTRY_NAME);

            if (remoteServer.checkName(args[2])) {
                JOptionPane.showMessageDialog(null, "Username already exists: " + args[2] +
                        "\n" + "Please try other name.", "Warning", JOptionPane.WARNING_MESSAGE);
                System.exit(0);
            }

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

            boolean result = remoteServer.askAccess(args[2]);
            if (!result) {
                JOptionPane.showMessageDialog(null, "Access denied, please contact the manager",
                        "Warning", JOptionPane.WARNING_MESSAGE);
                System.exit(0);
            }

            IRemoteClient remoteClient = new RemoteClient(args[2], false, remoteServer);

            remoteServer.signIn(remoteClient);
            remoteServer.addUser(args[2]);
            remoteClient.init();
            System.out.println("Client connected to server");
            remoteClient.askUpdateList();
            remoteClient.askJoinMessage();

            // Using for catch user quit, user may normally close the app, or force quit the app
            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                //System.err.println("CACHED FORCE CLOSING");
                try {
                    remoteServer.userClose(args[2]);
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

    private static void checkArgs(String[] args) {
        if (args.length != 3) {
            JOptionPane.showMessageDialog(null, "Arguments is not enough.\n" +
                            "Format: <serverIPAddress> <serverPort> <username>",
                    "Warning", JOptionPane.WARNING_MESSAGE);
            System.exit(0);
        }
    }
}
