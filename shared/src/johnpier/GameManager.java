package johnpier;

import java.rmi.*;

public interface GameManager extends Remote {
    GameState nextStep(StepParams params) throws RemoteException;
    GameState startGame(GameConfig initialConfig) throws RemoteException;
}
