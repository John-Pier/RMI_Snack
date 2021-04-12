package johnpier.rmi;

import java.rmi.*;
import java.rmi.registry.*;

public class ClientMain {
    public static final String PATH_NAME = "com.johnpier.server.GameManager";
    public static final int PORT = 1099;

    public static void main(String[] args) {
        try {
            Registry registry = LocateRegistry.getRegistry(PORT);
            var test = registry.lookup(PATH_NAME);
        } catch (RemoteException | NotBoundException e) {
            e.printStackTrace();
        }
    }
}
