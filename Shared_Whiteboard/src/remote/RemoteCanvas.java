package remote;

import java.awt.*;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class RemoteCanvas extends UnicastRemoteObject implements IRemoteCanvas {
    private final String toolType;
    private final Color color;
    private final Point startPoint;
    private final Point endPoint;
    private final String name;
    private final String text;
    private final int textSize;
    private final float eraserSize;

    public RemoteCanvas(String toolType, Color color, Point startPoint, Point endPoint, String name, String text, int textSize, float eraserSize) throws RemoteException {
        super();
        this.toolType = toolType;
        this.color = color;
        this.startPoint = startPoint;
        this.endPoint = endPoint;
        this.name = name;
        this.text = text;
        this.textSize = textSize;
        this.eraserSize = eraserSize;
    }


    @Override
    public String getToolType() throws RemoteException {
        return toolType;
    }

    @Override
    public Color getColor() throws RemoteException {
        return color;
    }

    @Override
    public Point getStartPoint() throws RemoteException {
        return startPoint;
    }

    @Override
    public Point getEndPoint() throws RemoteException {
        return endPoint;
    }

    @Override
    public String getName() throws RemoteException {
        return name;
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
