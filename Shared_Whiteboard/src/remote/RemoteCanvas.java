/**
 * Interface for remote canvas, use for collect user data for draw detail
 *
 * @author Josh Feng, 1266669, chenhaof@student.unimelb.edu.au
 * @date 27 April 2024
 */

package remote;

import java.awt.*;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class RemoteCanvas extends UnicastRemoteObject implements IRemoteCanvas {
    private final String toolType;
    private final Color color;
    private final Point startPoint;
    private final Point endPoint;
    private final String userID;
    private final String text;
    private final int textSize;
    private final float eraserSize;

    public RemoteCanvas(String toolType, Color color, Point startPoint,
                        Point endPoint, String userID, String text,
                        int textSize, float eraserSize) throws RemoteException {
        super();
        this.toolType = toolType;
        this.color = color;
        this.startPoint = startPoint;
        this.endPoint = endPoint;
        this.userID = userID;
        this.text = text;
        this.textSize = textSize;
        this.eraserSize = eraserSize;
    }

    // get user tool type
    @Override
    public String getToolType() throws RemoteException {
        return toolType;
    }

    // get user color
    @Override
    public Color getColor() throws RemoteException {
        return color;
    }

    // get starting point
    @Override
    public Point getStartPoint() throws RemoteException {
        return startPoint;
    }

    // get ending point
    @Override
    public Point getEndPoint() throws RemoteException {
        return endPoint;
    }

    //
    @Override
    public String getUserID() throws RemoteException {
        return userID;
    }

    @Override
    public String getText() throws RemoteException {
        if (text == null) {
            return "";
        }
        return text;
    }

    @Override
    public Integer getTextSize() throws RemoteException {
        return textSize;
    }

    @Override
    public float getEraserSize() throws RemoteException {
        return eraserSize;
    }
}
