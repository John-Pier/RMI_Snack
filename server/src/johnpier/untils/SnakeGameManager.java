package johnpier.untils;

import johnpier.*;

import java.rmi.RemoteException;

public class SnakeGameManager implements GameManager {
    @Override
    public void nextStep(StepParams params) {
        System.out.println("Worked!");
    }

    @Override
    public GameConfig startGame() throws RemoteException {
        return null;
    }
}
