import java.awt.Color;
import java.io.Serializable;
import java.util.ArrayList;

public class Shape implements Serializable {
    private static final long serialVersionUID = 1L;

    public String type;
    public String text;
    public double x1, y1, x2, y2;
    public Color color;
    public int size;
    public ArrayList<Shape> freeDrawShapes;

    // Constructor for normal shapes
    public Shape(String type, double x1, double y1, double x2, double y2, Color color, int size) {
        this.type = type;
        this.x1 = x1;
        this.y1 = y1;
        this.x2 = x2;
        this.y2 = y2;
        this.color = color;
        this.size = size;
        this.freeDrawShapes = new ArrayList<>();
    }

    // Constructor for text
    public Shape(String type, String text, double x1, double y1, Color color, int textSize) {
        this.type = type;
        this.text = text;
        this.x1 = x1;
        this.y1 = y1;
        this.color = color;
        this.size = textSize;
        this.freeDrawShapes = new ArrayList<>();
    }

    // Constructor for FreeDraw and Erase
    public Shape(String type, ArrayList<Shape> freeDrawShapes, Color color, int size) {
        this.type = type;
        this.freeDrawShapes = freeDrawShapes;
        this.color = color;
        this.size = size;
    }
}
