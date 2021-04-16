package johnpier.untils;

import johnpier.*;

import java.io.*;
import java.rmi.RemoteException;
import java.util.*;

public class SnakeAchievementsManager implements AchievementsManager {

    public List<Achievement> achievementsList = new ArrayList<>();

    @Override
    public List<Achievement> getAchievementsList() throws RemoteException {
//        return achievementsList.sort((a,b) -> a.score ==);
        return achievementsList;
    }

    @Override
    public void saveAchievement(Achievement achievement) throws RemoteException {
        this.achievementsList.add(achievement);
    }
}
