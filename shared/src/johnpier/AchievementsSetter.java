package johnpier;

import java.rmi.*;

public interface AchievementsSetter extends Remote {
    void setNewAchievements(int score, String time) throws RemoteException;
}
