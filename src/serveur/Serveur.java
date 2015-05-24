package serveur;

import javax.jms.TopicConnectionFactory;
import java.rmi.Remote;
import java.rmi.RemoteException;

public interface Serveur extends Remote {
    public void addAbo (User u, String n) throws RemoteException;

    public UserInterface Connection (String name, String password) throws RemoteException;

    public TopicConnectionFactory getFactory () throws RemoteException;

    public String affichage () throws RemoteException;

    public void abonnement (UserInterface u, String name) throws RemoteException;

    public String gestionUti () throws RemoteException;
}