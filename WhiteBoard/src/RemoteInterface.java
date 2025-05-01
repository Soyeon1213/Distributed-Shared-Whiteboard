import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Map;

public interface RemoteInterface extends Remote {
    boolean isBoardCreated() throws RemoteException;

    boolean requestToJoin(String username) throws RemoteException;

    public void removeUser(String username) throws RemoteException;
    public void kickoutUser(String username) throws RemoteException;
    boolean isUserKickedOut(String username) throws RemoteException;

    public Map<Integer, String> getUserMap() throws RemoteException;

    public void addShape(Shape shape) throws RemoteException;
    public ArrayList<Shape> getShapeList() throws RemoteException;
    public void setShapeList(ArrayList<Shape> shapeList) throws RemoteException;

    public void addChat(String chat) throws RemoteException;
    public String getChatHistory() throws RemoteException;

}