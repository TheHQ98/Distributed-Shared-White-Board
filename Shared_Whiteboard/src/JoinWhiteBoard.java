/**
 * @author Josh Feng, 1266669, chenhaof@student.unimelb.edu.au
 * @date 22 April 2024
 */
import whiteBoard.WhiteBoardGUI;
import remote.IRemoteCanvas;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class JoinWhiteBoard {
    public static void main(String[] args) {
        try {
            Registry registry = LocateRegistry.getRegistry("localhost", 8080);

            IRemoteCanvas remoteCanvas = (IRemoteCanvas) registry.lookup("SharedWhiteBoard");

            new WhiteBoardGUI("user", false, remoteCanvas);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
