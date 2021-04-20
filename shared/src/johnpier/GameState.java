package johnpier;

import java.io.Serializable;
import java.util.ArrayList;

public class GameState implements Serializable {
    private ArrayList<Coordinate> snake;
    private Coordinate foodElement;
    private Direction direction;
    private int fieldWidth;
    private int fieldHeight;
    private boolean isGameOver = false;
    private int score = 0;
    private int speed;

    public GameState(int fieldWidth, int fieldHeight, int speed) {
        this.direction = Direction.UP;
        var firstX = fieldWidth / 2;
        var firstY = fieldHeight / 2;

        this.foodElement = new Coordinate(firstX - 2, firstY - 1);
        this.snake = new ArrayList<>();
        this.snake.add(new Coordinate(firstX, firstY));
        this.snake.add(new Coordinate(firstX-1, firstY));
        this.speed = speed;
        this.fieldWidth = fieldWidth;
        this.fieldHeight = fieldHeight;
    }

    public ArrayList<Coordinate> getSnake() {
        return snake;
    }

    public void setSnake(ArrayList<Coordinate> snake) {
        this.snake = snake;
    }

    public Coordinate getFoodElement() {
        return foodElement;
    }

    public void setFoodElement(Coordinate foodElement) {
        this.foodElement = foodElement;
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
