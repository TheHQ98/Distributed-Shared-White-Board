/**
 * @author Josh Feng, 1266669, chenhaof@student.unimelb.edu.au
 * @date 22 April 2024
 */
import remote.IRemoteClient;
import remote.RemoteClient;
import remote.IRemoteServer;

import javax.swing.*;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class JoinWhiteBoard {
    public static void main(String[] args) {
        try {
            Registry registry = LocateRegistry.getRegistry("localhost", Integer.parseInt(args[0]));

            IRemoteServer remoteServer = (IRemoteServer) registry.lookup("SharedWhiteBoard");

            if (remoteServer.checkName(args[1])) {
                JOptionPane.showMessageDialog(null, "Username already exists: " + args[1] +
                        "\n" + "Please try other name.", "Warning", JOptionPane.WARNING_MESSAGE);
                System.exit(0);
            }

            while (remoteServer.getIsClosedState()) {
                Object[] options = {"Retry", "Close"};

                int answer = JOptionPane.showOptionDialog(null,
                        "Manager has not opened a new file yet.",
                        "Server",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.WARNING_MESSAGE,
                        null,
                        options,
                        options[0]);
                if (answer == JOptionPane.YES_OPTION) {
                    continue;
                } else if (answer == JOptionPane.NO_OPTION) {
                    System.exit(0);
                }
            }


            boolean result = remoteServer.askAccess(args[1]);
            if (!result) {
                JOptionPane.showMessageDialog(null, "Access denied, please contact the manager",
                        "Warning", JOptionPane.WARNING_MESSAGE);
                System.exit(0);
            }

            IRemoteClient remoteClient = new RemoteClient(args[1], false, remoteServer);

            remoteServer.signIn(remoteClient);
            remoteServer.addUser(args[1]);
            remoteClient.init();
            System.out.println("Client connected to server");
            remoteClient.askUpdateList();
            remoteClient.askJoinMessage();
        } catch (Exception e) {
            System.err.println("Server not started");
            JOptionPane.showMessageDialog(null, "Server not started." +
                    " Manager have not create white board yet.", "Warning", JOptionPane.WARNING_MESSAGE);
        }
    }
}
