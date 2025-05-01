import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;

public class ManagerGUI extends ClientGUI {
    private static final long serialVersionUID = 1L;

    // Manager buttons
    private JButton newButton = new JButton("New");
    private JButton openButton = new JButton("Open");
    private JButton saveButton = new JButton("Save");
    private JButton saveAsButton = new JButton("Save As");
    private JButton closeButton = new JButton("Close");
    private JButton kickoutButton = new JButton("Kick Out");

    public ManagerGUI(RemoteInterface server, String username) {
        super(server, username);
        setTitle("WhiteBoard - Manager - " + username);

        JPanel managerPanel = new JPanel();
        managerPanel.setLayout(new GridLayout(1, 6, 10, 10));
        managerPanel.add(newButton);
        managerPanel.add(openButton);
        managerPanel.add(saveButton);
        managerPanel.add(saveAsButton);
        managerPanel.add(closeButton);
        managerPanel.add(kickoutButton);

        // Open a new whiteboard
        newButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                try {
                    whiteBoard.newWhiteBoard();
                    server.setShapeList(whiteBoard.shapeList);
                }
                catch (Exception e) {
                    JOptionPane.showMessageDialog(new JFrame(), e.getMessage(), "Dialog", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        // Open a whiteboard from a file
        openButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                try {
                    JFileChooser openFile = new JFileChooser();
                    int userSelection = openFile.showOpenDialog(null);
                    if (userSelection == JFileChooser.APPROVE_OPTION) {
                        whiteBoard.file = openFile.getSelectedFile();
                        whiteBoard.openWhiteBoard();
                        server.setShapeList(whiteBoard.shapeList);
                    }
                }
                catch (Exception e) {
                    JOptionPane.showMessageDialog(new JFrame(), e.getMessage(), "Dialog", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        // Save the whiteboard to the file
        saveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                try {
                    if (whiteBoard.file != null) {
                        whiteBoard.saveWhiteBoard();
                    }
                    else {
                        JFileChooser saveFile = new JFileChooser();
                        int userSelection = saveFile.showSaveDialog(null);
                        if (userSelection == JFileChooser.APPROVE_OPTION) {
                            whiteBoard.file = saveFile.getSelectedFile();
                            whiteBoard.saveWhiteBoard();
                        }
                    }
                }
                catch (Exception e) {
                    JOptionPane.showMessageDialog(new JFrame(), e.getMessage(), "Dialog", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        // Save the whiteboard to a new file
        saveAsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                try {
                    JFileChooser saveFile = new JFileChooser();
                    int userSelection = saveFile.showSaveDialog(null);
                    if (userSelection == JFileChooser.APPROVE_OPTION) {
                        whiteBoard.file = saveFile.getSelectedFile();
                        whiteBoard.saveWhiteBoard();
                    }
                }
                catch (Exception e) {
                    JOptionPane.showMessageDialog(new JFrame(), e.getMessage(), "Dialog", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        // Close the whiteboard
        closeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                System.exit(0);
            }
        });

        // Kick out a user
        kickoutButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                try {
                    String[] userList = getUserList();
                    String selectedUser = (String) JOptionPane.showInputDialog(null, "Please select the user to kick out.",
                            null, JOptionPane.QUESTION_MESSAGE, null, userList, null);
                    if (selectedUser != null) {
                        server.removeUser(selectedUser);
                        server.kickoutUser(selectedUser);
                    }
                }
                catch (Exception e) {
                    JOptionPane.showMessageDialog(new JFrame(), e.getMessage(), "Dialog", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        // Adding the panel to the south of the main frame
        add(managerPanel, BorderLayout.SOUTH);
        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    // Get the list of users for the kick out dialog
    private String[] getUserList() throws Exception {
        ArrayList<String> userList = new ArrayList<>();
        Map<Integer, String> userMap = server.getUserMap();
        for (Map.Entry<Integer, String> user : userMap.entrySet()) {
            if (user.getKey() > 0) {
                userList.add(user.getValue());
            }
        }
        return userList.toArray(new String[0]);
    }

}
