package johnpier;

import java.rmi.*;
import java.util.List;

public interface AchievementsManager extends Remote {
    List<Achievement> getAchievementsList() throws RemoteException;
}
