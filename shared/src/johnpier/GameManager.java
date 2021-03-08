package johnpier;

import java.rmi.*;

public interface GameManager extends Remote {
    void nextStep() throws RemoteException;
}
