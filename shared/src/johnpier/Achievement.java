package johnpier;

import java.io.Serializable;

public class Achievement implements Serializable {
    private String playerName = "Default";
    private int score;

    public Achievement(String playerName, int score) {
        this.playerName = playerName;
        this.score = score;
    }

    public Achievement(int score) {
        this.score = score;
    }

    public String getPlayerName() {
        return playerName;
    }

    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }
}
