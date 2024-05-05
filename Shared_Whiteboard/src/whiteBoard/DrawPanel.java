/**
 * @author Josh Feng, 1266669, chenhaof@student.unimelb.edu.au
 * @date 18 April 2024
 */

package whiteBoard;

import remote.IRemoteCanvas;
import remote.IRemoteServer;
import remote.RemoteCanvas;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.Color;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionAdapter;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.WritableRaster;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.rmi.RemoteException;
import java.util.Objects;

public class DrawPanel extends JPanel {
    private int x1, y1, x2, y2;
    private ToolBar toolBar;
    private Color color;
    private String toolType;
    private Graphics2D g2d;
    private BufferedImage frame;
    private BufferedImage savedFrame;
    private IRemoteServer remoteServer;
    private boolean isManager;
    private String name;
    private Point startPoint;
    private Point endPoint;
    private boolean isClosed;
    private boolean isMotion;

    public DrawPanel(ToolBar toolBar, IRemoteServer remoteServer, boolean isManager, String name) {
        this.toolBar = toolBar;
        this.remoteServer = remoteServer;
        this.isManager = isManager;
        this.name = name;
        isClosed = false;
        addMouseListener(startListener);
        addMouseMotionListener(motionLister);
        addMouseListener(endListener);
        setDoubleBuffered(false);
    }

    private void init() {
        frame = new BufferedImage(ClientParams.CANVAS_WIDTH, ClientParams.CANVAS_HEIGHT, BufferedImage.TYPE_INT_RGB);
        g2d = (Graphics2D) frame.getGraphics();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setPaint(Color.WHITE);
        g2d.setStroke(new BasicStroke(ClientParams.DEFAULT_STROKE));
        cleanCanvas();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (frame == null) {
            if (isManager) {
                init();
                renderFrame(frame);
                sendImage();
            } else {
                try {
                    byte[] imageData = remoteServer.updateImage();
                    frame = ImageIO.read(new ByteArrayInputStream(imageData));
                    g2d = (Graphics2D) frame.getGraphics();
                    g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g2d.setPaint(Color.WHITE);
                    g2d.setStroke(new BasicStroke(ClientParams.DEFAULT_STROKE));
                } catch (IOException e) {
                    ClientParams.IO_ERROR();
                    System.err.println("IOException: " + e);
                }
            }
        }
        g.drawImage(frame, 0, 0, this);
    }

    public void renderFrame(BufferedImage frame) {
        g2d.drawImage(frame, 0, 0, null);
        repaint();
    }

    // Clean up the canvas
    public void cleanCanvas() {
        g2d.setPaint(Color.white);
        g2d.fillRect(0, 0, ClientParams.GUI_WIDTH, ClientParams.GUI_HEIGHT);
        g2d.setPaint(color);
    }

    public void newCanvas() {
        init();
        renderFrame(frame);
        sendImage();
    }

    // Save the canvas as an image
    public void saveCanvas() {
        ColorModel colorModel = frame.getColorModel();
        WritableRaster raster = frame.copyData(null);
        savedFrame = new BufferedImage(colorModel, raster, false, null);
    }

    // Get image of the current canvas
    public BufferedImage getCanvasImage() {
        saveCanvas();
        return savedFrame;
    }

    private final MouseListener startListener = new MouseAdapter() {
        @Override
        public void mousePressed(java.awt.event.MouseEvent e) {
            if (isClosed) {
                JOptionPane.showMessageDialog(DrawPanel.this,
                        "Canvas closed, you need to create a new file",
                        "Warning", JOptionPane.WARNING_MESSAGE);
                return;
            }
            // get initial coordinates
            x1 = e.getX();
            y1 = e.getY();
            startPoint = new Point(x1, y1);
            // get current color
            color = toolBar.getColor();
            // get tool tye
            toolType = toolBar.getToolType();
            saveCanvas();

            if (ClientParams.TEXT.equals(toolType)) {
                textInput();
            }

            isMotion = true;
        }
    };

    private final MouseMotionAdapter motionLister = new MouseMotionAdapter() {
        @Override
        public void mouseDragged(java.awt.event.MouseEvent e) {
            // get final coordinates
            x2 = e.getX();
            y2 = e.getY();

            g2d.setStroke(new BasicStroke(ClientParams.DEFAULT_STROKE));
            if (ClientParams.LINE.equals(toolType)) {
                renderFrame(savedFrame);
                g2d.setColor(color);
                g2d.drawLine(x1, y1, x2, y2);
            } else if (ClientParams.RECTANGLE.equals(toolType)) {
                renderFrame(savedFrame);
                g2d.setColor(color);
                g2d.drawRect(Math.min(x1, x2), Math.min(y1, y2), Math.abs(x1 - x2), Math.abs(y1 - y2));
            } else if (ClientParams.CIRCLE.equals(toolType)) {
                renderFrame(savedFrame);
                g2d.setColor(color);
                int radius = (int) Math.sqrt(Math.pow(x2 - x1, 2) + Math.pow(y2 - y1, 2)) / 2;
                int centerX = (x1 + x2) / 2;
                int centerY = (y1 + y2) / 2;
                int startX = centerX - radius;
                int startY = centerY - radius;
                g2d.drawOval(startX, startY, radius * 2, radius * 2);
            } else if (ClientParams.OVAL.equals(toolType)) {
                renderFrame(savedFrame);
                g2d.setColor(color);
                g2d.drawOval(Math.min(x1, x2), Math.min(y1, y2), Math.abs(x1 - x2), Math.abs(y1 - y2));
            } else if (ClientParams.DRAW.equals(toolType)) {
                g2d.setColor(color);
                g2d.drawLine(x1, y1, x2, y2);
                startPoint = new Point(x1, y1);
                endPoint = new Point(x2, y2);
                x1 = x2;
                y1 = y2;
                try {
                    RemoteCanvas remoteCanvas = new RemoteCanvas(toolType, color, startPoint, endPoint,
                            name, null, 0, toolBar.getEraserSize());
                    remoteServer.getCanvas(remoteCanvas);
                } catch (RemoteException ex) {
                    ClientParams.RMI_CONNECT_ERROR();
                    System.err.println("RemoteException: " + ex);
                } catch (IOException ex) {
                    ClientParams.IO_ERROR();
                    System.err.println("IOException: " + ex);
                }
            } else if (ClientParams.ERASER.equals(toolType)) {
                g2d.setColor(Color.WHITE);
                g2d.setStroke(new BasicStroke(toolBar.getEraserSize()));
                g2d.drawLine(x1, y1, x2, y2);
                startPoint = new Point(x1, y1);
                endPoint = new Point(x2, y2);
                x1 = x2;
                y1 = y2;
                try {
                    RemoteCanvas remoteCanvas = new RemoteCanvas(toolType, color, startPoint, endPoint,
                            name, null, 0, toolBar.getEraserSize());
                    remoteServer.getCanvas(remoteCanvas);
                } catch (RemoteException ex) {
                    ClientParams.RMI_CONNECT_ERROR();
                    System.err.println("RemoteException: " + ex);
                } catch (IOException ex) {
                    ClientParams.IO_ERROR();
                    System.err.println("IOException: " + ex);
                }
            }
            repaint();
        }
    };

    private final MouseListener endListener = new MouseAdapter() {
        @Override
        public void mouseReleased(java.awt.event.MouseEvent e) {
            System.out.println("Mouse Released");
            endPoint = new Point(x2, y2);
            try {
                RemoteCanvas remoteCanvas = new RemoteCanvas(toolType, color, startPoint, endPoint,
                        name, null, 0, toolBar.getEraserSize());
                remoteServer.getCanvas(remoteCanvas);
            } catch (RemoteException ex) {
                ClientParams.RMI_CONNECT_ERROR();
                System.err.println("RemoteException: " + ex);
            } catch (IOException ex) {
                ClientParams.IO_ERROR();
                System.err.println("IOException: " + ex);
            }

            // make sure new client get latest canvas
            sendImage();
            debug();
            // FIXME: COULD BE WRONG
            repaint();
            isMotion = false;
            x1 = x2 = y1 = y2 = -99;
        }
    };

    private void textInput() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        JTextField text = new JTextField(10);
        JLabel textLabel = new JLabel("Type your text");
        panel.add(textLabel);
        panel.add(text);


        SpinnerNumberModel spinnerModel = new SpinnerNumberModel(30, 10, 50, 5);
        JSpinner fontSize = new JSpinner(spinnerModel);
        JLabel fontLabel = new JLabel("Select font size (Default 30):");
        panel.add(fontLabel);
        panel.add(fontSize);

        int result = JOptionPane.showConfirmDialog(DrawPanel.this, panel,
                "Type text", JOptionPane.OK_CANCEL_OPTION);

        if (result == JOptionPane.OK_OPTION) {
            renderFrame(savedFrame);
            g2d.setColor(color);
            g2d.setFont(new Font("Arial", Font.PLAIN, (Integer) fontSize.getValue()));
            g2d.drawString(text.getText(), x1, y1);

            endPoint = new Point(x2, y2);
            try {
                RemoteCanvas remoteCanvas = new RemoteCanvas(toolType, color, startPoint, endPoint,
                        name, text.getText(), (Integer) fontSize.getValue(), toolBar.getEraserSize());
                remoteServer.getCanvas(remoteCanvas);
            } catch (RemoteException ex) {
                ClientParams.RMI_CONNECT_ERROR();
                System.err.println("RemoteException: " + ex);
            } catch (IOException ex) {
                ClientParams.IO_ERROR();
                System.err.println("IOException: " + ex);
            }

            // make sure new client get latest canvas
            sendImage();
        }

    }

    private byte[] imageToByteArray(BufferedImage image) throws IOException {
        ByteArrayOutputStream data = new ByteArrayOutputStream();
        ImageIO.write(image, "png", data);
        return data.toByteArray();
    }

    private BufferedImage byteArrayToImage(byte[] imageData) throws IOException {
        ByteArrayInputStream data = new ByteArrayInputStream(imageData);
        return ImageIO.read(data);
    }

    public void sendImage() {
        try {
            byte[] imageData = imageToByteArray(frame);
            remoteServer.getImage(imageData, name);
        } catch (IOException ex) {
            ClientParams.IO_ERROR();
            System.err.println("IOException: " + ex);
        }
    }

    public void sendSavedImage(BufferedImage savedFrame) {
        try {
            byte[] imageData = imageToByteArray(savedFrame);
            remoteServer.getImage(imageData, name);
        } catch (IOException ex) {
            ClientParams.IO_ERROR();
            System.err.println("IOException: " + ex);
        }
    }

    public void syncCanvas(IRemoteCanvas remoteCanvas) throws RemoteException {
        //TODO may wrong
        if (Objects.equals(remoteCanvas.getEndPoint(), new Point(-99, -99))) {
            return;
        }
        if (Objects.equals(remoteCanvas.getStartPoint(), new Point(-99, -99))) {
            return;
        }
        g2d.setStroke(new BasicStroke(ClientParams.DEFAULT_STROKE));
        if (ClientParams.DRAW.equals(remoteCanvas.getToolType())) {
            g2d.setColor(remoteCanvas.getColor());
            Point point1 = remoteCanvas.getStartPoint();
            Point point2 = remoteCanvas.getEndPoint();
            g2d.drawLine(point1.x, point1.y, point2.x, point2.y);
            point1.x = point2.x;
            point1.y = point2.y;
        } else if (ClientParams.LINE.equals(remoteCanvas.getToolType())) {
            Point point1 = remoteCanvas.getStartPoint();
            Point point2 = remoteCanvas.getEndPoint();
            g2d.setColor(remoteCanvas.getColor());
            g2d.drawLine(point1.x, point1.y, point2.x, point2.y);
        } else if (ClientParams.RECTANGLE.equals(remoteCanvas.getToolType())) {
            Point point1 = remoteCanvas.getStartPoint();
            Point point2 = remoteCanvas.getEndPoint();
            g2d.setColor(remoteCanvas.getColor());
            g2d.drawRect(Math.min(point1.x, point2.x), Math.min(point1.y, point2.y),
                    Math.abs(point1.x - point2.x), Math.abs(point1.y - point2.y));
        } else if (ClientParams.CIRCLE.equals(remoteCanvas.getToolType())) {
            Point point1 = remoteCanvas.getStartPoint();
            Point point2 = remoteCanvas.getEndPoint();
            g2d.setColor(remoteCanvas.getColor());
            int radius = (int) Math.sqrt(Math.pow(point2.x - point1.x, 2) + Math.pow(point2.y - point1.y, 2)) / 2;
            int centerX = (point1.x + point2.x) / 2;
            int centerY = (point1.y + point2.y) / 2;
            int startX = centerX - radius;
            int startY = centerY - radius;
            g2d.drawOval(startX, startY, radius * 2, radius * 2);
        } else if (ClientParams.OVAL.equals(remoteCanvas.getToolType())) {
            Point point1 = remoteCanvas.getStartPoint();
            Point point2 = remoteCanvas.getEndPoint();
            g2d.setColor(remoteCanvas.getColor());
            g2d.drawOval(Math.min(point1.x, point2.x), Math.min(point1.y, point2.y),
                    Math.abs(point1.x - point2.x), Math.abs(point1.y - point2.y));
        } else if (ClientParams.TEXT.equals(remoteCanvas.getToolType())) {
            Point point = remoteCanvas.getStartPoint();
            g2d.setColor(remoteCanvas.getColor());
            g2d.setFont(new Font("Arial", Font.PLAIN, remoteCanvas.getTextSize()));
            g2d.drawString(remoteCanvas.getText(), point.x, point.y);
        } else if (ClientParams.ERASER.equals(remoteCanvas.getToolType())) {
            g2d.setColor(Color.WHITE);
            g2d.setStroke(new BasicStroke(remoteCanvas.getEraserSize()));
            Point point1 = remoteCanvas.getStartPoint();
            Point point2 = remoteCanvas.getEndPoint();
            g2d.drawLine(point1.x, point1.y, point2.x, point2.y);
            point1.x = point2.x;
            point1.y = point2.y;
        }
        repaint();
    }

    public void getCanvasFromServer(byte[] imageData) throws IOException {
        savedFrame = byteArrayToImage(imageData);
        renderFrame(savedFrame);
    }

    public void changeIsClosedState(boolean state) {
        isClosed = state;
    }

    public boolean getIsClosed() {
        return isClosed;
    }

    // TODO DEBUG MESSAGE
    public void debug() {
        System.out.println(isMotion);
        System.out.println("DEBUG-Draw: " + toolType + " Point: " + startPoint + " " + endPoint);
    }

    public void askRender() {
        if (isMotion) {
            renderFrame(savedFrame);
        }
    }
}