package serveur;

import java.rmi.Remote;
import java.rmi.RemoteException;

import javax.jms.Message;
import javax.jms.Topic;
import javax.jms.TopicConnection;
import javax.jms.TopicConnectionFactory;
import javax.jms.TopicPublisher;
import javax.jms.TopicSession;
import javax.jms.TopicSubscriber;

public interface Serveur extends Remote{
	public void addAbo(User u, String n) throws RemoteException;
	public UserInterface Connection(String name, String password) throws RemoteException;
	public TopicConnectionFactory getFactory() throws RemoteException;
	public String affichage() throws RemoteException;
	public void abonnement(UserInterface u, String name) throws RemoteException;
	public String gestionUti() throws RemoteException;
}