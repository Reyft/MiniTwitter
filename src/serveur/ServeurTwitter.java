package serveur;

import org.apache.activemq.ActiveMQConnectionFactory;

import javax.jms.TopicConnectionFactory;
import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("serial")
public class ServeurTwitter extends UnicastRemoteObject implements Serveur {
    private TopicConnectionFactory factory;
    private List<User> users;

    public ServeurTwitter () throws RemoteException {
        super();
        factory = null;
        users = new ArrayList<User>();
    }

    @Override
    public TopicConnectionFactory getFactory () {
        return factory;
    }

    @Override
    public UserInterface Connection (String name, String password) {
        try {
            for (User u : users) {
                if (u.getName().equals(name)) {
                    System.out.println(u.getName());
                    if (u.getPassword().equals(password)) {
                        return u;
                    } else {
                        return null;
                    }
                }
            }
            User us = new User(name, password);
            users.add(us);
            return us;
        } catch (RemoteException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public void abonnement (UserInterface u, String name) {
        boolean rep = false;
        boolean rep2 = true;
        try {
            for (User us : users) {
                if (us.getName().equals(name)) {
                    rep = true;
                }
            }
            if (rep) {
                for (String n : u.getAbo()) {
                    if (n.equals(name)) {
                        System.out.println("Vous êtes dejà abboné a cette personne");
                        rep2 = false;
                    }
                }
                if (rep2) {
                    u.addAbo(name);
                }
                System.out.println(u.getAbo());
            } else {
                System.out.println("Cette personne n'éxiste pas");
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String affichage () {
        String res = "";
        for (User u : users) {
            res += u.getName() + ", ";
        }
        return res;
    }

    @Override
    public String gestionUti () {
        String res = "";
        for (User u : users) {
            res += u.getName() + " :\n";
            for (String n : u.getAbo()) {
                res += "\t- " + n + "\n";
            }
            res += "\n";
        }
        return res;
    }

    @Override
    public void addAbo (User u, String n) {
        boolean b = true;
        for (User us : users) {
            if (us.getName().equals(n)) {
                b = false;
            }
            if (b) {
                u.getAbo().add(n);
            }
        }
    }

    private void initialiser () {
        factory = new ActiveMQConnectionFactory("user", "password", "tcp://localhost:61616");
    }

    public static void main (String[] args) {
        try {
            ServeurTwitter st = new ServeurTwitter();
            st.initialiser();
            String name = "MiniTwitter";
            Naming.rebind(name, st);
            System.out.println("ComputeEngine bound");
        } catch (RemoteException | MalformedURLException e) {
            e.printStackTrace();
        }
    }
}