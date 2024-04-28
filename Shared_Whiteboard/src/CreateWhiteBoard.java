/**
* @author Josh Feng, 1266669, chenhaof@student.unimelb.edu.au
* @date 18 April 2024
*/

import remote.IRemoteClient;
import remote.RemoteClient;
import remote.IRemoteServer;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class CreateWhiteBoard {
    private static String name = "Manager";
    public static void main(String[] args) {
        try {
            Registry registry = LocateRegistry.getRegistry("localhost", 8080);

            IRemoteServer remoteServer = (IRemoteServer) registry.lookup("SharedWhiteBoard");
            IRemoteClient remoteClient = new RemoteClient(name, true, remoteServer);

            remoteServer.setManagerName(name);
            remoteServer.signIn(remoteClient);
            remoteClient.init();
            //new WhiteBoardGUI("admin", true, remoteServer, remoteUserList);
            System.out.println("Client connected to server");
        } catch (Exception e) {
            System.out.println("Client exception: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
