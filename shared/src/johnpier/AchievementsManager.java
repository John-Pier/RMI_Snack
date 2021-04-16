package johnpier;

import java.rmi.*;
import java.util.*;

public interface AchievementsManager extends AchievementsSetter {
    List<Achievement> getAchievementsList() throws RemoteException;
}
