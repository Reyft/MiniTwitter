package serveur;

import javax.jms.TopicConnectionFactory;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;
import java.util.Set;

public interface Serveur extends Remote {
    public void addAbo (User u, String n) throws RemoteException;

    public UserInterface Connection (String name, String password) throws RemoteException;

    public TopicConnectionFactory getFactory () throws RemoteException;

    public String affichage () throws RemoteException;

    public void abonnement (UserInterface u, String name) throws RemoteException;

    public String gestionUti () throws RemoteException;

    /**
     * Permet d'ajouter le nom d'un topic à la liste des topics existants.
     * @param name
     * @throws RemoteException
     */
    void conservTopicName (String name) throws RemoteException;

    /**
     * Permet de récupérer la liste des noms des topics.
     * @return La liste des noms des topics.
     */
    Set<String> getListTopicName () throws RemoteException;
}