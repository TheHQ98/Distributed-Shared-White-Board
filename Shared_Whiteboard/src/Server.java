import remote.IRemoteServer;
import remote.RemoteServer;
import serverGUI.ServerGUI;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class Server {
    public static void main(String[] args) {
        try {
            IRemoteServer remoteServer = new RemoteServer();

            Registry registry = LocateRegistry.createRegistry(Integer.parseInt(args[0]));
            registry.bind("SharedWhiteBoard", remoteServer);
            System.out.println("RMI Ready");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
