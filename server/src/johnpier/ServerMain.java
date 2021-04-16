package johnpier;

import johnpier.untils.*;

import java.rmi.*;
import java.rmi.registry.*;
import java.rmi.server.UnicastRemoteObject;

public class ServerMain {
    public static final String GAME_MANAGER_PATH_NAME = "com.johnpier.server.GameManager";
    public static final String ACHIEVEMENTS_MANAGER_PATH_NAME = "com.johnpier.server.AchievementsManager";
//    public static final String ACHIEVEMENTS_SETTER_PATH_NAME = "com.johnpier.server.AchievementsSetter";
    public static final int PORT = 1099;

    public static void main(String[] args) {
        System.out.println("RMI Server starting...");
        GameManager gameManager = new SnakeGameManager();
        AchievementsManager achievementsManager = new SnakeAchievementsManager();
        try {
            Registry registry = LocateRegistry.createRegistry(PORT);
            Remote stubGameManager = UnicastRemoteObject.exportObject(gameManager, 0);
            Remote stubAchievementsManager = UnicastRemoteObject.exportObject(achievementsManager, 0);
            registry.bind(GAME_MANAGER_PATH_NAME, stubGameManager);
            registry.bind(ACHIEVEMENTS_MANAGER_PATH_NAME, stubAchievementsManager);

            System.out.println("RMI Server for Snack App successfully started !");
        } catch (RemoteException | AlreadyBoundException e) {
            System.err.println("RMI Server exception: " + e.toString());
            e.printStackTrace();
        }
    }
}
