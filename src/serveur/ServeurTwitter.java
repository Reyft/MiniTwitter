package serveur;

import org.apache.activemq.ActiveMQConnectionFactory;

import javax.jms.TopicConnectionFactory;
import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@SuppressWarnings("serial")
public class ServeurTwitter extends UnicastRemoteObject implements Serveur {
    private TopicConnectionFactory factory;
    private List<User> users;

    // La liste des noms de topic.
    private Set<String> listTopicName;

    public ServeurTwitter () throws RemoteException {
        super();
        factory = null;
        users = new ArrayList<User>();
        listTopicName = new HashSet<>();
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
            for (String topicName : listTopicName) {
                if (topicName.equals(name)) {
                    rep = true;
                }
            }
            if (rep) {
                for (String n : u.getAbo()) {
                    if (n.equals(name)) {
                        System.out.println("Vous êtes dejà abboné a cette personne ou topic");
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
        for (String nomTopic : listTopicName) {
            res += nomTopic + ", ";
        }
        return res.trim();
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

    /**
     * Permet d'ajouter le nom d'un topic à la liste des topics existants.
     *
     * @param name Le nom du topic
     * @throws RemoteException
     */
    @Override
    public void conservTopicName (String name) throws RemoteException {
        listTopicName.add(name);
    }

    /**
     * Permet de récupérer la liste des noms des topics.
     *
     * @return La liste des noms des topics.
     */
    @Override
    public Set<String> getListTopicName () {
        return listTopicName;
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