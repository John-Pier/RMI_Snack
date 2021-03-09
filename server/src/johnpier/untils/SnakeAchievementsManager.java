package johnpier.untils;

import johnpier.*;

import java.rmi.RemoteException;
import java.util.*;

public class SnakeAchievementsManager implements AchievementsManager, AchievementsSetter {

    public List<Achievement> achievementsList = new ArrayList<>();

    @Override
    public List<Achievement> getAchievementsList() throws RemoteException {
        return new ArrayList<>();
    }

    @Override
    public void setNewAchievements(int score, String time) {
        this.achievementsList.add(new Achievement());
    }
}
