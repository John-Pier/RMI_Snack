package johnpier.untils;

import johnpier.*;
import java.rmi.RemoteException;
import java.util.*;
import java.util.stream.Collectors;

public class SnakeAchievementsManager implements AchievementsManager {

    public List<Achievement> achievementsList = new ArrayList<>();

    @Override
    public List<Achievement> getAchievementsList() throws RemoteException {
        return achievementsList;
    }

    @Override
    public void saveAchievement(Achievement achievement) throws RemoteException {
        this.achievementsList.add(achievement);
        achievementsList.sort(Comparator.comparingInt(o -> -o.getScore()));
        achievementsList = achievementsList.stream().dropWhile(value -> achievementsList.size() > 9).collect(Collectors.toList());
    }
}
