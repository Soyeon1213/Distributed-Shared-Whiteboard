import javax.swing.*;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class RemoteInterfaceImpl extends UnicastRemoteObject implements RemoteInterface {
    private static final long serialVersionUID = 1L;

    private boolean boardCreated = false;

    private int userCount = 0;
    private ConcurrentHashMap<Integer, String> userMap = new ConcurrentHashMap<>();
    private ConcurrentHashMap<String, Boolean> kickedOutUsers = new ConcurrentHashMap<>();

    private ArrayList<Shape> shapeList = new ArrayList<>();
    private String chatHistory = "";

    protected RemoteInterfaceImpl() throws RemoteException {
        super();
    }

    // Create a new user and return the user ID
    public int newUser(String username) {
        int userID = userCount;
        userMap.put(userID, username);
        userCount++;
        return userID;
    }

    // When the board is created, the server will set the boardCreated flag to true
    public void setBoardCreated(boolean boardCreated) throws RemoteException {
        this.boardCreated = boardCreated;
    }

    // Check if the board is created before joining
    @Override
    public boolean isBoardCreated() throws RemoteException {
        return boardCreated;
    }

    // Request to join the whiteboard
    @Override
    public synchronized boolean requestToJoin(String username) throws RemoteException {
        int response = JOptionPane.showConfirmDialog(null, "User " + username + " someone wants to share your whiteboard. Do you accept?",
                "Join Request", JOptionPane.YES_NO_OPTION);
        if (response == JOptionPane.YES_OPTION) {
            // Check if the user is already in the whiteboard
            if (userMap.containsValue(username)) {
                JOptionPane.showMessageDialog(null, "User " + username + " is already in the whiteboard.");
                return false;
            }
            // Join the user to the whiteboard
            else {
                newUser(username);
                return true;
            }
        }
        return false;
    }

    // Kick out a user from the whiteboard. When kicked out, the user will be removed from the userMap.
    @Override
    public void removeUser(String username) throws RemoteException {
        for (int key : userMap.keySet()) {
            if (userMap.get(key).equals(username)) {
                userMap.remove(key);
                break;
            }
        }
    }

    @Override
    public void kickoutUser(String username) throws RemoteException {
        kickedOutUsers.put(username, true);
    }


    // Check if the user is kicked out
    @Override
    public boolean isUserKickedOut(String username) throws RemoteException {
        return kickedOutUsers.getOrDefault(username, false);
    }

    // Get the user map
    public Map<Integer, String> getUserMap() throws RemoteException {
        return userMap;
    }

    // Get the shape list
    public synchronized ArrayList<Shape> getShapeList() throws RemoteException {
        return shapeList;
    }

    // Add a shape to the shape list
    public synchronized void addShape(Shape shape) throws RemoteException {
        if (shape.type.equals("FreeDraw") || shape.type.equals("Erase")) {
            shapeList.addAll(shape.freeDrawShapes);
        }
        else {
            shapeList.add(shape);
        }
    }

    // Set the shape list
    public synchronized void setShapeList(ArrayList<Shape> shapeList) throws RemoteException {
        this.shapeList = shapeList;
    }

    // Add a chat message to the chat history
    public synchronized void addChat(String chat) throws RemoteException {
        chatHistory += chat  + "\n";
    }

    // Get the chat history
    public synchronized String getChatHistory() throws RemoteException {
        return chatHistory;
    }
}
