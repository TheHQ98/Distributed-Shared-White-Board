/**
 * @author Josh Feng, 1266669, chenhaof@student.unimelb.edu.au
 * @date 27 April 2024
 */

package remote;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class RemoteCanvas extends UnicastRemoteObject implements IRemoteCanvas {
    private static BufferedImage frame;

    public RemoteCanvas() throws RemoteException {
        super();
    }

    @Override
    public byte[] updateImage() throws IOException {
        if (frame != null) {
            ByteArrayOutputStream data = new ByteArrayOutputStream();
            ImageIO.write(frame, "png", data);
            return data.toByteArray();
        }
        return null;
    }

    @Override
    public void getImage(byte[] imageData) throws IOException {
        frame = byteArrayToImage(imageData);
        //System.out.println("Image received");
    }


    @Override
    public void printTest(String text) throws RemoteException {
        System.out.println(text);
    }

    @Override
    public byte[] imageToByteArray(BufferedImage image) throws IOException {
        ByteArrayOutputStream data = new ByteArrayOutputStream();
        ImageIO.write(image, "png", data);
        return data.toByteArray();
    }

    @Override
    public BufferedImage byteArrayToImage(byte[] imageData) throws IOException {
        ByteArrayInputStream data = new ByteArrayInputStream(imageData);
        return ImageIO.read(data);
    }

}
