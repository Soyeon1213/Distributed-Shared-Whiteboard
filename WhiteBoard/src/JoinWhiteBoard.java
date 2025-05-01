import javax.swing.*;
import java.rmi.ConnectException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class JoinWhiteBoard {
    public JoinWhiteBoard(String serverIP, int serverPort, String username) throws Exception {
        try {
            Registry registry = LocateRegistry.getRegistry(serverIP, serverPort);
            RemoteInterface server = (RemoteInterface) registry.lookup("WhiteBoard");

            // Check if the whiteboard has been created
            if (!server.isBoardCreated()) {
                showErrorAndExit("Whiteboard has not been created yet. Please try again later.");
            }

            // Request to join the whiteboard
            boolean joinAccepted = server.requestToJoin(username);
            if (joinAccepted) {
                System.out.println("User " + username + " joined the whiteboard.");

                // Create the client GUI
                ClientGUI clientGUI = new ClientGUI(server, username);
                clientGUI.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                clientGUI.pack();
                clientGUI.setVisible(true);

                // Sync the client GUI
                while (true) {
                    Thread.sleep(100);
                    clientGUI.sync();
                }
            }
            // If join request was denied, show error and exit
            else {
                showErrorAndExit("Join request was denied.");
            }
        }
        // If server connection is lost, show error and exit
        catch (ConnectException e) {
            showErrorAndExit("Server connection lost. Please make sure the server is running.");
        }
        // If server is not found, show error and exit
        catch (RemoteException e) {
            showErrorAndExit("Cannot connect to the server. Please make sure the server is running.");
        }
        catch (Exception e) {
            showErrorAndExit(e.getMessage());
        }
    }

    protected void showErrorAndExit(String message) {
        System.out.println(message);
        JOptionPane.showMessageDialog(null, message, "Connection Error", JOptionPane.ERROR_MESSAGE);
        System.exit(1);
    }

    public static void main(String[] args) {
        if (args.length < 3) {
            System.out.println("Usage: JoinWhiteBoard <serverIPAddress> <serverPort> <username>");
            return;
        }

        String IPAddress = args[0];
        int port = Integer.parseInt(args[1]);
        String username = args[2];

        try {
            JoinWhiteBoard client = new JoinWhiteBoard(IPAddress, port, username);
        }
        catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }
}