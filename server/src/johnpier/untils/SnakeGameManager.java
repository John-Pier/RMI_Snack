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
        GameState gameState = new GameState(initialConfig.gridWidth, initialConfig.gridHeight, initialConfig.speed);
        this.currentState = gameState;

        return this.currentState;
    }

    @Override
    public GameState nextStep(StepParams params) throws RemoteException {
        if (params.isExit) {
            currentState.setGameOver(true);
            return currentState;
        }
        for (int i = currentState.getSnake().size() - 1; i >= 1; i--) {
            currentState.getSnake().get(i).setX(currentState.getSnake().get(i - 1).getX());
            currentState.getSnake().get(i).setY(currentState.getSnake().get(i - 1).getY());
        }
        int y, x;
        var snakeHeadCoordinate = currentState.getSnake().get(0);
        y = snakeHeadCoordinate.getY();
        x = snakeHeadCoordinate.getX();

        switch (params.nextDirection) {
            case UP -> {
                if (!(currentState.getSnake().get(1).getX() == x && y == currentState.getSnake().get(1).getY() - 1)) {
                    y--;
                    snakeHeadCoordinate.setY(y);
                    if (snakeHeadCoordinate.getY() < 0) {
                        currentState.setGameOver(true);
                    }
                }
            }
            case DOWN -> {
                if (!(currentState.getSnake().get(1).getX() == x && y == currentState.getSnake().get(1).getY() + 1)) {
                    y++;
                    snakeHeadCoordinate.setY(y);
                    if (snakeHeadCoordinate.getY() > currentState.getFieldHeight()) {
                        currentState.setGameOver(true);
                    }
                }
            }
            case LEFT -> {
                if (!(currentState.getSnake().get(1).getX() - 1 == x && y == currentState.getSnake().get(1).getY())) {
                    x--;
                    snakeHeadCoordinate.setX(x);
                    if (snakeHeadCoordinate.getX() < 0) {
                        currentState.setGameOver(true);
                    }
                }
            }
            case RIGHT -> {
                if (!(currentState.getSnake().get(1).getX() + 1 == x && y == currentState.getSnake().get(1).getY())) {
                    x++;
                    snakeHeadCoordinate.setX(x);
                    if (snakeHeadCoordinate.getX() > currentState.getFieldWidth()) {
                        currentState.setGameOver(true);
                    }
                }
            }
        }

        if (currentState.getFood().getX() == snakeHeadCoordinate.getX() &&
                currentState.getFood().getY() == snakeHeadCoordinate.getY()) {

            ArrayList<Coordinate> newSnake =  currentState.getSnake();
            int blockX = newSnake.get(newSnake.size() -1).getX();
            int blockY = newSnake.get(newSnake.size() -1).getY();

            switch (currentState.getDirection()) {
                case UP -> blockY--;
                case DOWN -> blockY++;
                case LEFT -> blockX--;
                case RIGHT -> blockX++;
            }
            newSnake.add(new Coordinate(blockX, blockY));
            currentState.setSnake(newSnake);
            currentState.setFood(getFood(currentState.getSnake(), currentState.getFieldWidth(), currentState.getFieldHeight()));
            currentState.setScore(currentState.getScore() + 1);
        }

        for (int i = 1; i < currentState.getSnake().size(); i++) {
            if (snakeHeadCoordinate.getX() == currentState.getSnake().get(i).getX()
                    && snakeHeadCoordinate.getY() == currentState.getSnake().get(i).getY()) {
                currentState.setGameOver(true);
            }
        }

        return currentState;
    }

    private Coordinate getFood(ArrayList<Coordinate> snake, int width, int height) {
        Coordinate food;
        int foodX, foodY;

        start:
        while (true) {
            foodX = random.nextInt(width);
            foodY = random.nextInt(height);

            for (Coordinate c : snake) {
                if (c.getX() == foodX && c.getY() == foodY) {
                    continue start;
                }
            }
            break;

        }
        food = new Coordinate(foodX, foodY);
        return food;
    }
}
