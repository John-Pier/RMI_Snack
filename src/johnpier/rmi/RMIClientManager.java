package johnpier.rmi;

import johnpier.*;

import java.rmi.*;
import java.rmi.registry.*;

public class RMIClientManager {
    public static final String GAME_MANAGER_PATH_NAME = "com.johnpier.server.GameManager";
    public static final String ACHIEVEMENTS_MANAGER_PATH_NAME = "com.johnpier.server.AchievementsManager";
    public static final int PORT = 1099;

    public static GameManager getGameManager() {
        try {
            Registry registry = LocateRegistry.getRegistry(PORT);
            return (GameManager)registry.lookup(GAME_MANAGER_PATH_NAME);
        } catch (RemoteException | NotBoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static AchievementsManager getAchievementsManager() {
        try {
            Registry registry = LocateRegistry.getRegistry(PORT);
            return (AchievementsManager)registry.lookup(ACHIEVEMENTS_MANAGER_PATH_NAME);
        } catch (RemoteException | NotBoundException e) {
            e.printStackTrace();
        }
        return null;
    }
}
