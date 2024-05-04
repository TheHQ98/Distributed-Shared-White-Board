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


            // TODO need to fix
//            try {
//                System.out.println("Waiting for manager to accept...");
//                Thread.sleep(3000);
//            } catch (InterruptedException e) {
//                // 如果线程在sleep期间被中断，处理中断异常
//                System.err.println("Sleep was interrupted");
//            }

            if (remoteServer.checkName(args[1])) {
                JOptionPane.showMessageDialog(null, "Username already exists: " + args[1] + "\n" +
                        "Please try other name.", "Warning", JOptionPane.WARNING_MESSAGE);
                System.exit(0);
            }

            boolean result = remoteServer.askAccess(args[1]);
            if (!result) {
                JOptionPane.showMessageDialog(null, "Access denied, please contact the manager", "Warning", JOptionPane.WARNING_MESSAGE);
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
            e.printStackTrace();
        }
    }
}
