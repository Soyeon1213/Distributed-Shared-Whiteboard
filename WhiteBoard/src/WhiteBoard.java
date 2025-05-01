import javax.swing.*;
import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.io.*;
import java.util.*;

public class WhiteBoard extends JPanel {
    private static final long serialVersionUID = 1L;

    public ArrayList<Shape> shapeList = new ArrayList<>();
    public File file = null;

    public WhiteBoard() {
        super();
        setBackground(Color.WHITE);
    }

    // Draw shapes on the whiteboard by using Graphics2D
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        for (Shape shape : shapeList) {
            g2.setPaint(shape.color);
            g2.setStroke(new BasicStroke(shape.size));
            switch (shape.type) {
                case "Line":
                    g2.draw(new Line2D.Double(shape.x1, shape.y1, shape.x2, shape.y2));
                    break;
                case "Oval":
                    g2.draw(new Ellipse2D.Double(shape.x1, shape.y1, shape.x2, shape.y2));
                    break;
                case "Rectangle":
                    g2.draw(new Rectangle2D.Double(shape.x1, shape.y1, shape.x2, shape.y2));
                    break;
                case "Text":
                    g2.setFont(new Font("Arial", Font.PLAIN, shape.size));  // Use size for text size
                    g2.drawString(shape.text, (int) shape.x1, (int) shape.y1);
                    break;
            }
        }
        g2.dispose();
    }

    // Add a shape to the whiteboard
    public void draw(Shape shape) {
        shapeList.add(shape);
        repaint();
    }

    @SuppressWarnings("unchecked")
    // Open a whiteboard from a file
    public void openWhiteBoard() {
        try {
            FileInputStream fileInput = new FileInputStream(file);
            ObjectInputStream objectInput = new ObjectInputStream(fileInput);
            Object obj = objectInput.readObject();
            if (obj instanceof ArrayList<?>) {
                ArrayList<?> tempList = (ArrayList<?>) obj;
                ArrayList<Shape> newShapeList = new ArrayList<>();
                for (Object o : tempList) {
                    if (o instanceof Shape) {
                        newShapeList.add((Shape) o);
                    } else {
                        throw new IOException("Data is not of type Shape");
                    }
                }
                shapeList = newShapeList;
            } else {
                throw new IOException("Data is not an ArrayList");
            }
            objectInput.close();
            repaint();
        } catch (Exception e) {
            showError("Error reading whiteboard file: " + e.getMessage());
        }
    }

    // Save the whiteboard to a file
    public void saveWhiteBoard() {
        try {
            FileOutputStream fileOutput = new FileOutputStream(file);
            ObjectOutputStream objectOutput = new ObjectOutputStream(fileOutput);
            objectOutput.writeObject(shapeList);
            objectOutput.close();
        } catch (Exception e) {
            showError("Error writing whiteboard file: " + e.getMessage());
        }
    }

    // Create a new whiteboard
    public void newWhiteBoard() {
        shapeList = new ArrayList<>();
        file = null;
        repaint();
    }

    protected void showError(String message) {
        System.out.println(message);
        JOptionPane.showMessageDialog(null, message, "File Error", JOptionPane.ERROR_MESSAGE);
    }
}
