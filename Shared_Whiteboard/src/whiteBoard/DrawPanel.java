/**
 * @author Josh Feng, 1266669, chenhaof@student.unimelb.edu.au
 * @date 18 April 2024
 */

package whiteBoard;

import remote.IRemoteCanvas;

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

public class DrawPanel extends JPanel {
    private int x1, y1, x2, y2;
    private ToolBar toolBar;
    private Color color;
    private String toolType;
    private Graphics2D g2d;
    private BufferedImage frame;
    private BufferedImage savedFrame;
    private IRemoteCanvas remoteCanvas;
    private boolean isManager;

    public DrawPanel(ToolBar toolBar, IRemoteCanvas remoteCanvas, boolean isManager) {
        this.toolBar = toolBar;
        this.remoteCanvas = remoteCanvas;
        this.isManager = isManager;
        addMouseListener(startListener);
        addMouseMotionListener(motionLister);
        addMouseListener(endListener);
        setDoubleBuffered(false);
    }

    private void init() {
        frame = new BufferedImage(ClientParams.GUI_WIDTH, ClientParams.GUI_HEIGHT, BufferedImage.TYPE_INT_RGB);
        g2d = (Graphics2D) frame.getGraphics();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setPaint(Color.WHITE);
        g2d.setStroke(new BasicStroke(3.0f));
        cleanCanvas();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (frame == null) {
            if (isManager) {
                init();
                renderFrame(frame);
            } else {
                try {
                    byte[] imageData = remoteCanvas.updateImage();
                    frame = ImageIO.read(new ByteArrayInputStream(imageData));
                    g2d = (Graphics2D) frame.getGraphics();
                    g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g2d.setPaint(Color.WHITE);
                    g2d.setStroke(new BasicStroke(3.0f));
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        g.drawImage(frame, 0, 0, this);
    }

    public void renderFrame(BufferedImage f) {
        g2d.drawImage(f, 0, 0, null);
        repaint();
    }

    // Clean up the canvas
    public void cleanCanvas() {
        g2d.setPaint(Color.white);
        g2d.fillRect(0, 0, ClientParams.GUI_WIDTH, ClientParams.GUI_HEIGHT);
        g2d.setPaint(color);
        repaint();
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

    public BufferedImage getFrame() {
        return frame;
    }

    private final MouseListener startListener = new MouseAdapter() {
        @Override
        public void mousePressed(java.awt.event.MouseEvent e) {
            // get initial coordinates
            x1 = e.getX();
            y1 = e.getY();
            // get current color
            color = toolBar.getColor();
            // get tool tye
            toolType = toolBar.getToolType();
            saveCanvas();

            if (ClientParams.TEXT.equals(toolType)) {
                textInput();
            }
        }
    };

    private final MouseMotionAdapter motionLister = new MouseMotionAdapter() {
        @Override
        public void mouseDragged(java.awt.event.MouseEvent e) {
            // get final coordinates
            x2 = e.getX();
            y2 = e.getY();

            g2d.setStroke(new BasicStroke(3.0f));
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
                x1 = x2;
                y1 = y2;
                sendImage();
            } else if (ClientParams.ERASER.equals(toolType)) {
                g2d.setColor(Color.WHITE);
                g2d.setStroke(new BasicStroke(toolBar.getEraserSize()));
                g2d.drawLine(x2, y2, x2, y2);
            }
            repaint();
        }
    };

    private final MouseListener endListener = new MouseAdapter() {
        @Override
        public void mouseReleased(java.awt.event.MouseEvent e) {
            //System.out.println("End with: " + x2 + " " + y2);
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

        int result = JOptionPane.showConfirmDialog(null, panel,
                "Type text", JOptionPane.OK_CANCEL_OPTION);

        if (result == JOptionPane.OK_OPTION) {
            renderFrame(savedFrame);
            g2d.setColor(color);
            g2d.setFont(new Font("Arial", Font.PLAIN, (Integer) fontSize.getValue()));
            g2d.drawString(text.getText(), x1, y1);
        }

    }

    private byte[] imageToByteArray(BufferedImage image) throws IOException {
        ByteArrayOutputStream data = new ByteArrayOutputStream();
        ImageIO.write(image, "png", data);
        return data.toByteArray();
    }

    private void sendImage() {
        try {
            byte[] imageData = imageToByteArray(frame);
            remoteCanvas.getImage(imageData);
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }
}