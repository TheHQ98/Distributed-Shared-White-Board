/**
* @author Josh Feng, 1266669, chenhaof@student.unimelb.edu.au
* @date 18 April 2024
*/

import whiteBoard.WhiteBoardGUI;
import remote.IRemoteCanvas;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class CreateWhiteBoard {
    public static void main(String[] args) {
        try {
            Registry registry = LocateRegistry.getRegistry("localhost", 8080);

            IRemoteCanvas remoteCanvas = (IRemoteCanvas) registry.lookup("SharedWhiteBoard");

            new WhiteBoardGUI("admin", true, remoteCanvas);
            System.out.println("Client connected to server");
        } catch (Exception e) {
            System.out.println("Client exception: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
