/**
 * @author Josh Feng, 1266669, chenhaof@student.unimelb.edu.au
 * @date 27 April 2024
 */

package remote;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.rmi.Remote;
import java.rmi.RemoteException;

public interface IRemoteCanvas extends Remote {
    public byte[] updateImage() throws IOException;
    public void getImage(byte[] imageData) throws IOException;
    public void printTest(String text) throws RemoteException;
    public byte[] imageToByteArray(BufferedImage image) throws IOException;
    public BufferedImage byteArrayToImage(byte[] imageData) throws IOException;
}
