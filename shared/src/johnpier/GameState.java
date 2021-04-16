package johnpier;

import java.io.Serializable;
import java.util.ArrayList;

public class GameState implements Serializable {
    private ArrayList<Coordinate> snake;
    private Coordinate food;
    private Direction direction;
    private int fieldWidth;
    private int fieldHeight;
    private boolean isGameOver;
    private int score;
    private int lvl;
    private int speed;

    public GameState(int fieldWidth, int fieldHeight) {
        this.direction = Direction.LEFT;
        this.food = new Coordinate(1, 1);
        this.snake = new ArrayList<>();
        this.snake.add(new Coordinate(10, 10));
        this.snake.add(new Coordinate(9, 10));
        this.snake.add(new Coordinate(8,10));
        this.score = 0;
        this.speed = 5;
        this.isGameOver = false;
        this.fieldWidth = fieldWidth;
        this.fieldHeight = fieldHeight;
    }

    public ArrayList<Coordinate> getSnake() {
        return snake;
    }

    public void setSnake(ArrayList<Coordinate> snake) {
        this.snake = snake;
    }

    public Coordinate getFood() {
        return food;
    }

    public void setFood(Coordinate food) {
        this.food = food;
    }

    public Direction getDirection() {
        return direction;
    }

    public void setDirection(Direction direction) {
        this.direction = direction;
    }

    public int getFieldWidth() {
        return fieldWidth;
    }

    public void setFieldWidth(int fieldWidth) {
        this.fieldWidth = fieldWidth;
    }

    public int getFieldHeight() {
        return fieldHeight;
    }

    public void setFieldHeight(int fieldHeight) {
        this.fieldHeight = fieldHeight;
    }

    public boolean isGameOver() {
        return isGameOver;
    }

    public void setGameOver(boolean gameOver) {
        isGameOver = gameOver;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public int getSpeed() {
        return speed;
    }

    public void setSpeed(int speed) {
        this.speed = speed;
    }
}
