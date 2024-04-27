import remote.IRemoteCanvas;
import remote.RemoteCanvas;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class Server {
    public static void main(String[] args) {
        try {
            IRemoteCanvas remoteCanvas = new RemoteCanvas();

            Registry registry = LocateRegistry.createRegistry(8080);
            registry.bind("SharedWhiteBoard", remoteCanvas);
            System.out.println("SharedWhiteBoard started");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
