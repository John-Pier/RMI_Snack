package johnpier.logic;

import java.io.Serializable;

public class Player implements Serializable {

	private String playerName = "Default";
	private int score;

	public Player(int score) {
		this.score = score;
	}

	public Player(String name, int score) {
		this.playerName = name;
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
