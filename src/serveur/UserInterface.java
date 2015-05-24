package serveur;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

public interface UserInterface extends Remote {
    public String getPassword () throws RemoteException;

    public void setPassword (String password) throws RemoteException;

    public String getName () throws RemoteException;

    public List<String> getAbo () throws RemoteException;

    public void setName (String name) throws RemoteException;

    public void addAbo (String name) throws RemoteException;
}
