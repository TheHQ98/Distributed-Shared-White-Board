package server;

import remote.IRemoteServer;
import remote.RemoteServer;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class Server {
    private final String[] args;


    public Server(String[] args) {
        this.args = args;
    }


    public void start() {
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
