package johnpier;

import java.rmi.*;

public interface AchievementsSetter extends Remote {
    void saveAchievement(Achievement achievement) throws RemoteException;
}
