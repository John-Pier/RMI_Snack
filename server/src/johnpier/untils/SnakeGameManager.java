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
        // gameState.setFood(getFood(gameState.getSnake(), gameState.getFieldWidth(), gameState.getFieldHeight()));
        this.currentState = gameState;

        return this.currentState;
    }

    @Override
    public GameState nextStep(StepParams params) throws RemoteException {
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
                y--;
                snakeHeadCoordinate.setY(y);
                if (snakeHeadCoordinate.getY() < 0) {
                    currentState.setGameOver(true); //or set y = fieldHeight
                }
            }
            case DOWN -> {
                y++;
                snakeHeadCoordinate.setY(y);
                if (snakeHeadCoordinate.getY() > currentState.getFieldHeight()) {
                    currentState.setGameOver(true); //or set y = 0
                }
            }
            case LEFT -> {
                x--;
                snakeHeadCoordinate.setX(x);
                if (snakeHeadCoordinate.getX() < 0) {
                    currentState.setGameOver(true); //or set x = fieldWidth;
                }
            }
            case RIGHT -> {
                x++;
                snakeHeadCoordinate.setX(x);
                if (snakeHeadCoordinate.getX() > currentState.getFieldWidth()) {
                    currentState.setGameOver(true);  //or set x = 0;
                }
            }
        }


        if (currentState.getFood().getX() == currentState.getSnake().get(0).getX() &&
                currentState.getFood().getY() == currentState.getSnake().get(0).getY()) {

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
//        // eat food
//        if (currentState.getFood().getX() == snakeHeadCoordinate.getX()
//                && currentState.getFood().getY() == snakeHeadCoordinate.getY()) {
//
//            var newSnake = currentState.getSnake();
//            newSnake.add(new Coordinate(-1, -1));
//            currentState.setSnake(newSnake);
//            currentState.setFood(getFood(currentState.getSnake(), currentState.getFieldWidth(), currentState.getFieldHeight()));
//            currentState.setScore(currentState.getScore() + 1);
//        }

        // self destroy
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
