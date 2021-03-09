package johnpier;

import java.rmi.*;

public interface GameManager extends Remote {
    void nextStep(StepParams params) throws RemoteException;
    GameConfig startGame() throws RemoteException;
}
