package johnpier;

import java.rmi.*;
import java.util.List;

public interface AchievementsManager extends Remote, AchievementsSetter {
    List<Achievement> getAchievementsList() throws RemoteException;
}
