/**
 * @author Josh Feng, 1266669, chenhaof@student.unimelb.edu.au
 * @date 22 April 2024
 */
import remote.IRemoteClient;
import remote.RemoteClient;
import remote.IRemoteServer;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class JoinWhiteBoard {
    public static void main(String[] args) {
        try {
            Registry registry = LocateRegistry.getRegistry("localhost", Integer.parseInt(args[0]));

            IRemoteServer remoteServer = (IRemoteServer) registry.lookup("SharedWhiteBoard");
            IRemoteClient remoteClient = new RemoteClient(args[1], false, remoteServer);

            remoteServer.signIn(remoteClient);
            remoteClient.init();
            //new WhiteBoardGUI("user", false, remoteCanvas, remoteUserList);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
