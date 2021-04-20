package johnpier.untils;

import johnpier.*;

import java.rmi.RemoteException;
import java.util.*;

public class SnakeGameManager implements GameManager {
    public Boolean gameStarted = false;
    private GameState currentState;
    private final Random random = new Random();

    @Override
    public GameState startGame(GameConfig initialConfig) throws RemoteException {
        gameStarted = true;
        this.currentState = new GameState(initialConfig.gridWidth, initialConfig.gridHeight, initialConfig.speed);
        return this.currentState;
    }

    @Override
    public GameState nextStep(StepParams params) throws RemoteException {
        if (params.isExit) {
            currentState.setGameOver(true);
            return currentState;
        }

        var snakeHeadCoordinate = currentState.getSnake().get(0);
        int y, oldY = y = snakeHeadCoordinate.getY();
        int x, oldX = x = snakeHeadCoordinate.getX();

        switch (params.nextDirection) {
            case UP -> {
                y--;
                if (y < 0) {
                    currentState.setGameOver(true);
                }
                snakeHeadCoordinate.setY(y);
            }
            case DOWN -> {
                y++;
                if (y >= currentState.getFieldHeight()) {
                    currentState.setGameOver(true);
                }
                snakeHeadCoordinate.setY(y);
            }
            case LEFT -> {
                x--;
                if (x < 0) {
                    currentState.setGameOver(true);
                }
                snakeHeadCoordinate.setX(x);
            }
            case RIGHT -> {
                x++;
                if (x >= currentState.getFieldWidth()) {
                    currentState.setGameOver(true);
                }
                snakeHeadCoordinate.setX(x);
            }
        }

        if (currentState.getFoodElement().getX() == snakeHeadCoordinate.getX() &&
                currentState.getFoodElement().getY() == snakeHeadCoordinate.getY()) {
            var snake = currentState.getSnake();
            snake.add(1,new Coordinate(oldX, oldY));
            currentState.setFoodElement(createFoodElement(currentState.getSnake(), currentState.getFieldWidth(), currentState.getFieldHeight()));
            currentState.setScore(currentState.getScore() + 1);
        } else {
            for (int i = currentState.getSnake().size() - 1; i > 1; i--) {
                currentState.getSnake().get(i).setX(currentState.getSnake().get(i - 1).getX());
                currentState.getSnake().get(i).setY(currentState.getSnake().get(i - 1).getY());
            }
            currentState.getSnake().get(1).setX(oldX);
            currentState.getSnake().get(1).setY(oldY);
        }

        if(checkHeadCrossing(currentState.getSnake())) {
            currentState.setGameOver(true);
        }

        return currentState;
    }

    private boolean checkHeadCrossing(ArrayList<Coordinate> snake) {
        var snakeHeadCoordinate = snake.get(0);
        for (int i = 1; i < snake.size(); i++) {
            if (snakeHeadCoordinate.getX() == snake.get(i).getX() &&
                    snakeHeadCoordinate.getY() == snake.get(i).getY()) {
                return true;
            }
        }
        return false;
    }

    private Coordinate createFoodElement(ArrayList<Coordinate> snake, int width, int height) {
        while (true) {
            int x = random.nextInt(width);
            int y = random.nextInt(height);

            for (Coordinate c : snake) {
                if (c.getX() != x && c.getY() != y) {
                    return new Coordinate(x, y);
                }
            }
        }
    }
}
