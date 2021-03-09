package johnpier;

import java.rmi.Remote;
import java.util.List;

public interface AchievementsManager extends Remote {
    List<Achievement> getAchievementsList();
}
