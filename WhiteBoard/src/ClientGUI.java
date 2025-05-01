import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.Map;

public class ClientGUI extends JFrame {
    private static final long serialVersionUID = 1L;

    public WhiteBoard whiteBoard = new WhiteBoard();
    public RemoteInterface server;
    private String username;

    public Chatting chatting;

    private JButton lineButton = new JButton("Line");
    private JButton circleButton = new JButton("Circle");
    private JButton ovalButton = new JButton("Oval");
    private JButton rectangleButton = new JButton("Rectangle");
    private JButton freeDrawButton = new JButton("Free Draw");
    private JButton eraseButton = new JButton("Erase");
    private JButton sizeButton = new JButton("Set Size (10)");
    private JButton textButton = new JButton("Text");
    private JButton textSizeButton = new JButton("Set Text Size (12)");

    private JTextField textInput = new JTextField();

    private JTextField chatInput;
    private JButton sendButton;

    private JLabel userListLabel = new JLabel("Connected Users: ");

    private Color selectedColor = Color.BLACK;
    private JButton colorButton = new JButton("Set Color");
    private JLabel colorDisplay = new JLabel();

    private int size = 10;
    private int textSize = 12;

    public ClientGUI(RemoteInterface server, String username) {
        super("WhiteBoard - " + username);
        this.server = server;
        this.username = username;

        setLayout(new BorderLayout());

        // Top panel for drawing tools
        JPanel topPanel = new JPanel(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(5, 5, 5, 5);
        c.fill = GridBagConstraints.HORIZONTAL;

        c.gridx = 0;
        c.gridy = 0;
        topPanel.add(lineButton, c);
        lineButton.addActionListener(new ShapeButtonListener("Line"));

        c.gridx = 1;
        topPanel.add(circleButton, c);
        circleButton.addActionListener(new ShapeButtonListener("Circle"));

        c.gridx = 2;
        topPanel.add(ovalButton, c);
        ovalButton.addActionListener(new ShapeButtonListener("Oval"));

        c.gridx = 3;
        topPanel.add(rectangleButton, c);
        rectangleButton.addActionListener(new ShapeButtonListener("Rectangle"));

        c.gridx = 4;
        topPanel.add(freeDrawButton, c);
        freeDrawButton.addActionListener(new ShapeButtonListener("FreeDraw"));

        c.gridx = 5;
        topPanel.add(eraseButton, c);
        eraseButton.addActionListener(new ShapeButtonListener("Erase"));

        c.gridx = 6;
        topPanel.add(sizeButton, c);
        sizeButton.addActionListener(new SizeButtonListener());

        c.gridx = 7;
        topPanel.add(textButton, c);
        textButton.addActionListener(new ShapeButtonListener("Text"));

        c.gridx = 8;
        topPanel.add(textSizeButton, c);
        textSizeButton.addActionListener(new TextSizeButtonListener());

        c.gridx = 9;
        topPanel.add(colorButton, c);
        colorButton.addActionListener(new ColorButtonListener());

        colorDisplay.setOpaque(true);
        colorDisplay.setBackground(selectedColor);
        colorDisplay.setPreferredSize(new Dimension(20, 20));
        c.gridx = 10;
        topPanel.add(colorDisplay, c);

        add(topPanel, BorderLayout.NORTH);

        // Main drawing area
        whiteBoard.setBackground(Color.WHITE);
        whiteBoard.setPreferredSize(new Dimension(800, 600));
        add(whiteBoard, BorderLayout.CENTER);

        // Right panel for user list and chat
        JPanel rightPanel = new JPanel(new GridBagLayout());
        rightPanel.setBackground(new Color(204, 204, 255));
        c = new GridBagConstraints();
        c.insets = new Insets(5, 5, 5, 5);

        chatting = new Chatting();
        JScrollPane chatScrollPane = new JScrollPane(chatting);
        chatting.setPreferredSize(new Dimension(200, 500));

        // Add userListLabel to the right panel
        c.gridx = 0;
        c.gridy = 0;
        c.gridwidth = 2;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 1.0;
        c.weighty = 0;
        rightPanel.add(userListLabel, c);
        c.gridy = 1;
        c.weighty = 1.0;
        rightPanel.add(chatScrollPane, c);

        // Add chat input and send button to the right panel
        chatInput = new JTextField();
        chatInput.setPreferredSize(new Dimension(150, 20));
        c.gridy = 2;
        c.gridwidth = 1;
        c.weightx = 0.8;
        c.weighty = 0;
        rightPanel.add(chatInput, c);

        sendButton = new JButton("Send");
        sendButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                try {
                    server.addChat(username + ": " + chatInput.getText());
                }
                catch (Exception e) {
                    JOptionPane.showMessageDialog(new JFrame(), e.getMessage(), "Dialog", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        c.gridx = 1;
        c.weightx = 0.2;
        rightPanel.add(sendButton, c);

        add(rightPanel, BorderLayout.EAST);

        // Add window listener to remove user from server when window is closed
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent we) {
                try {
                    server.removeUser(username);
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        resetButtonColors();
        pack();
        // Center the window
        setLocationRelativeTo(null);
        setVisible(true);
    }

    // Sync the client GUI
    public void sync() {
        try {
            if (server.isUserKickedOut(username)) {
                notifyKickedOut();
            }
            // Update the shape list, chat history, and user list
            whiteBoard.shapeList = server.getShapeList();
            chatting.setText(server.getChatHistory());
            updateUserList();
            whiteBoard.repaint();
        }
        // If the manager closed the server, show error and exit
        catch (RemoteException e) {
            JOptionPane.showMessageDialog(new JFrame(), "The manager closed the server. Exiting application.", "Connection Error", JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }
    }

    // Notify the user that they have been kicked out by the manager
    public void notifyKickedOut() {
        JOptionPane.showMessageDialog(new JFrame(), "You have been kicked out by the manager", "Kicked Out", JOptionPane.ERROR_MESSAGE);
        System.exit(0);
    }

    private void clearMouseListeners() {
        for (MouseListener mouselistener : whiteBoard.getMouseListeners()) {
            whiteBoard.removeMouseListener(mouselistener);
        }
    }

    class ShapeButtonListener implements ActionListener {
        private String shape;

        public ShapeButtonListener(String shape) {
            this.shape = shape;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            clearMouseListeners();
            resetButtonColors();
            JButton sourceButton = (JButton) e.getSource();
            sourceButton.setBackground(new Color(255, 204, 204));

            // Add the appropriate mouse listener based on the shape
            switch (shape) {
                case "Text":
                    String text = JOptionPane.showInputDialog(null, "Enter text:", "Text Input", JOptionPane.PLAIN_MESSAGE);
                    if (text != null && !text.isEmpty()) {
                        whiteBoard.addMouseListener(new DrawText(text));
                    }
                    break;
                case "Line":
                    whiteBoard.addMouseListener(new DrawLine());
                    break;
                case "Circle":
                case "Oval":
                case "Rectangle":
                    whiteBoard.addMouseListener(new DrawShape(shape));
                    break;
                case "FreeDraw":
                    FreeDraw freeDraw = new FreeDraw();
                    whiteBoard.addMouseListener(freeDraw);
                    whiteBoard.addMouseMotionListener(freeDraw);
                    break;
                case "Erase":
                    Erase erase = new Erase();
                    whiteBoard.addMouseListener(erase);
                    whiteBoard.addMouseMotionListener(erase);
                    break;
            }
        }
    }

    class TextSizeButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String input = JOptionPane.showInputDialog(null, "Enter text size:", "Text Size Input", JOptionPane.PLAIN_MESSAGE);
            // Text size must be positive integer
            try {
                textSize = Integer.parseInt(input);
                if (textSize <= 0) throw new NumberFormatException();
                textSizeButton.setText("Set Text Size (" + textSize + ")");
            }
            catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(null, "Please enter a valid positive integer.", "Invalid Input", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    class SizeButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String input = JOptionPane.showInputDialog(null, "Enter size:", "Size Input", JOptionPane.PLAIN_MESSAGE);
            // Size must be positive integer
            try {
                size = Integer.parseInt(input);
                if (size <= 0) throw new NumberFormatException();
                sizeButton.setText("Set Size (" + size + ")");
            }
            catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(null, "Please enter a valid positive integer.", "Invalid Input", JOptionPane.ERROR_MESSAGE);
            }
        }
    }


    class ColorButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            Color newColor = JColorChooser.showDialog(null, "Choose a color", selectedColor);
            if (newColor != null) {
                selectedColor = newColor;
                colorDisplay.setBackground(newColor);
            }
        }
    }

    // Mouse listeners for drawing line
    class DrawLine extends MouseAdapter {
        private double x1, y1, x2, y2;

        @Override
        public void mousePressed(MouseEvent e) {
            Point currentPoint = e.getPoint();
            x1 = currentPoint.getX();
            y1 = currentPoint.getY();
        }

        @Override
        public void mouseReleased(MouseEvent e) {
            Point currentPoint = e.getPoint();
            x2 = currentPoint.getX();
            y2 = currentPoint.getY();

            Shape line = new Shape("Line", x1, y1, x2, y2, selectedColor, size);
            try {
                server.addShape(line);
            }
            catch (Exception ex) {
                JOptionPane.showMessageDialog(new JFrame(), ex.getMessage(), "Dialog", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    // Mouse listeners for drawing shapes
    class DrawShape extends MouseAdapter {
        private String shape;
        private double x1, y1, x2, y2;

        public DrawShape(String shape) {
            this.shape = shape;
        }

        @Override
        public void mousePressed(MouseEvent e) {
            Point currentPoint = e.getPoint();
            x1 = currentPoint.getX();
            y1 = currentPoint.getY();
        }

        @Override
        public void mouseReleased(MouseEvent e) {
            Point currentPoint = e.getPoint();
            x2 = currentPoint.getX();
            y2 = currentPoint.getY();

            // For circle, oval, and rectangle, startX and startY are the top-left corner
            double startX = Math.min(x1, x2);
            double startY = Math.min(y1, y2);

            double width = Math.abs(x2 - x1);
            double height = Math.abs(y2 - y1);

            Shape shape1 = null;

            switch (shape) {
                case "Circle":
                    double diameter = Math.max(width, height);
                    shape1 = new Shape("Oval", startX, startY, diameter, diameter, selectedColor, size);
                    break;
                case "Oval":
                    shape1 = new Shape("Oval", startX, startY, width, height, selectedColor, size);
                    break;
                case "Rectangle":
                    shape1 = new Shape("Rectangle", startX, startY, width, height, selectedColor, size);
                    break;
            }

            // Add the shape to the server
            try {
                server.addShape(shape1);
            }
            catch (Exception ex) {
                JOptionPane.showMessageDialog(new JFrame(), ex.getMessage(), "Dialog", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    // Mouse listener for drawing text
    class DrawText extends MouseAdapter {
        private String text;
        private double x, y;

        public DrawText(String text) {
            this.text = text;
        }

        @Override
        public void mousePressed(MouseEvent e) {
            Point currentPoint = e.getPoint();
            x = currentPoint.getX();
            y = currentPoint.getY();

            Shape textShape = new Shape("Text", text, x, y, selectedColor, textSize);
            try {
                server.addShape(textShape);
            }
            catch (Exception ex) {
                JOptionPane.showMessageDialog(new JFrame(), ex.getMessage(), "Dialog", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    // Mouse listener for free draw
    class FreeDraw extends MouseAdapter implements MouseMotionListener {
        private Point prevPoint = null;

        // Store a free draw line as a list of lines
        private ArrayList<Shape> shapesToDraw = new ArrayList<>();

        @Override
        public void mousePressed(MouseEvent e) {
            prevPoint = e.getPoint();
        }

        @Override
        public void mouseDragged(MouseEvent e) {
            Point currentPoint = e.getPoint();
            if (prevPoint != null) {
                Shape freeDrawShape = new Shape("Line", prevPoint.x, prevPoint.y, currentPoint.x, currentPoint.y, selectedColor, size);
                shapesToDraw.add(freeDrawShape);
                whiteBoard.draw(freeDrawShape);
                prevPoint = currentPoint;
            }
        }

        @Override
        public void mouseReleased(MouseEvent e) {
            if (!shapesToDraw.isEmpty()) {
                try {
                    server.addShape(new Shape("FreeDraw", shapesToDraw, selectedColor, size));
                    shapesToDraw.clear();
                }
                catch (Exception ex) {
                    System.err.println("Error adding free draw shapes: " + ex.getMessage());
                    ex.printStackTrace();
                }
            }
            prevPoint = null;
        }
    }

    // Mouse listener for erasing
    class Erase extends MouseAdapter implements MouseMotionListener {
        private Point prevPoint = null;
        private ArrayList<Shape> shapesToErase = new ArrayList<>();

        @Override
        public void mousePressed(MouseEvent e) {
            prevPoint = e.getPoint();
        }

        @Override
        public void mouseDragged(MouseEvent e) {
            Point currentPoint = e.getPoint();
            if (prevPoint != null) {
                Shape eraseShape = new Shape("Line", prevPoint.x, prevPoint.y, currentPoint.x, currentPoint.y, Color.WHITE, size);
                shapesToErase.add(eraseShape);
                whiteBoard.draw(eraseShape);
                prevPoint = currentPoint;
            }
        }

        @Override
        public void mouseReleased(MouseEvent e) {
            if (!shapesToErase.isEmpty()) {
                try {
                    server.addShape(new Shape("Erase", shapesToErase, Color.WHITE, size));
                    shapesToErase.clear();
                } catch (Exception ex) {
                    System.err.println("Error adding erase shapes: " + ex.getMessage());
                    ex.printStackTrace();
                }
            }
            prevPoint = null;
        }
    }

    // For changing button colors, when a button is clicked, reset all other buttons to default color
    private void resetButtonColors() {
        Color buttonColor = new Color(196, 242, 232);
        lineButton.setBackground(buttonColor);
        circleButton.setBackground(buttonColor);
        ovalButton.setBackground(buttonColor);
        rectangleButton.setBackground(buttonColor);
        textButton.setBackground(buttonColor);
        textSizeButton.setBackground(buttonColor);
        freeDrawButton.setBackground(buttonColor);
        eraseButton.setBackground(buttonColor);
        sizeButton.setBackground(buttonColor);
        colorButton.setBackground(buttonColor);
    }

    // Chatting area
    class Chatting extends JTextArea {
        private static final long serialVersionUID = 1L;

        public Chatting() {
            super();
            setBackground(new Color(255, 255, 204));
            setEditable(false);
        }

        @Override
        public Dimension getPreferredSize() {
            return new Dimension(200, 600);
        }

        public void sendMessage(String username, String message) {
            append(username + ": " + message);
        }



    }

    // Update the user list
    private void updateUserList() {
        try {
            Map<Integer, String> userMap = server.getUserMap();
            StringBuilder userList = new StringBuilder("Connected Users: ");
            for (String user : userMap.values()) {
                userList.append(user).append(", ");
            }
            userListLabel.setText(userList.toString());
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }


}