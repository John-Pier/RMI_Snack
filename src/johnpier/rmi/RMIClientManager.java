package johnpier.rmi;

import johnpier.GameManager;

import java.rmi.*;
import java.rmi.registry.*;

public class RMIClientManager {
    public static final String PATH_NAME = "com.johnpier.server.GameManager";
    public static final int PORT = 1099;

    public static GameManager getGameManager() {
        try {
            Registry registry = LocateRegistry.getRegistry(PORT);
            return (GameManager)registry.lookup(PATH_NAME);
        } catch (RemoteException | NotBoundException e) {
            e.printStackTrace();
        }
        return null;
    }
}
