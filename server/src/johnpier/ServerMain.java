package johnpier;

import johnpier.untils.SnakeGameManager;

import java.rmi.*;
import java.rmi.registry.*;
import java.rmi.server.UnicastRemoteObject;

public class ServerMain {
    public static final String PATH_NAME = "com.johnpier.server.GameManager";
    public static final int PORT = 1099;

    public static void main(String[] args) {
        System.out.println("RMI Server starting...");
        GameManager gameManager = new SnakeGameManager();
        try {
            Registry registry = LocateRegistry.createRegistry(PORT);
            Remote stubObj = UnicastRemoteObject.exportObject(gameManager, 0);
            registry.bind(PATH_NAME, stubObj);

            System.out.println("RMI Server for Snack App successfully started !");
        } catch (RemoteException | AlreadyBoundException e) {
            System.err.println("RMI Server exception: " + e.toString());
            e.printStackTrace();
        }
    }
}
