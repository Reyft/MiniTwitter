package client;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.jms.Topic;
import javax.jms.TopicConnection;
import javax.jms.TopicConnectionFactory;
import javax.jms.TopicPublisher;
import javax.jms.TopicSession;
import javax.jms.TopicSubscriber;

import serveur.Serveur;
import serveur.User;

public class Admin {
	public static void main(String[] args) {
		Serveur serv = null;
		TopicConnectionFactory fact = null;
		TopicConnection con = null;
		TopicSession session = null;
		try {
			serv = (Serveur) Naming.lookup("rmi://localhost/MiniTwitter");
			fact = serv.getFactory();
			con = fact.createTopicConnection();			
			session = con.createTopicSession(false,Session.AUTO_ACKNOWLEDGE);
			String res = serv.gestionUti();
			System.out.println(res);
			session.close();
			con.close();
		} catch (MalformedURLException | RemoteException | NotBoundException | JMSException e) {
			e.printStackTrace();
		}
	}
}
