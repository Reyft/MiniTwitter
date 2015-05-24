package client;

import serveur.Serveur;
import serveur.UserInterface;

import javax.jms.*;
import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Client {
    // L'utilisateur.
    private UserInterface utilisateur = null;

    private TopicConnectionFactory fact = null;
    private TopicConnection con = null;
    private TopicSession session = null;
    private TopicPublisher tp = null;
    private List<TopicSubscriber> ts = new ArrayList<TopicSubscriber>();
    private Topic t = null;
    private Scanner scan;

    // Le serveur Twitter.
    private Serveur serv;

    // Le nom et mdp du client.
    private String name, password;

    // Méthode d'initialisation du client.
    public Client () {
        scan = new Scanner(System.in);
        serv = null;
        name = password = "";
    }

    public void setFact (TopicConnectionFactory factory) {
        fact = factory;
    }

    public UserInterface getUtilisateur () {
        return utilisateur;
    }

    public void setUtilisateur (UserInterface utilisateur) {
        this.utilisateur = utilisateur;
    }

    public TopicConnection getCon () {
        return con;
    }

    public void setCon (TopicConnection con) {
        this.con = con;
    }

    public TopicSession getSession () {
        return session;
    }

    public void setSession (TopicSession session) {
        this.session = session;
    }

    public TopicPublisher getTp () {
        return tp;
    }

    public void setTp (TopicPublisher tp) {
        this.tp = tp;
    }

    public Topic getT () {
        return t;
    }

    public void setT (Topic t) {
        this.t = t;
    }

    public TopicConnectionFactory getFact () {
        return fact;
    }

    /**
     * Lancement de tout ce que doit faire un client.
     */
    private void run () throws RemoteException, NotBoundException, MalformedURLException, JMSException {
        // Recherche du serveur.
        serv = (Serveur) Naming.lookup("rmi://localhost/MiniTwitter");

        // Choix du nom d'utilisateur et du mdp.
        inscription();

        // Initialisation des variables.
        init();

        menuPrincipal();

        close();
        System.out.println("Au revoir");
    }

    /**
     * On monte les choix du menu principal.
     *
     * @throws RemoteException
     * @throws JMSException
     */
    private void menuPrincipal () throws JMSException, RemoteException {
        String choix;
        do {
            System.out.println("1 : Envoyer message\n2 : Voir mes messages\n3 : S'abonner\n4 : Quitter");
            choix = scan.nextLine();
            switch (choix) {
                case "1":
                    publishMessage();
                    break;
                case "2":
                    afficheMessage();
                    break;
                case "3":
                    abonnement();
                    break;
            }
        } while (!choix.equals("4"));
    }

    /**
     * Permet de s'abonner.
     *
     * @throws RemoteException
     */
    private void abonnement () throws RemoteException {
        System.out.println("Choississez parmis :");
        System.out.println(serv.affichage());
        String res = scan.nextLine().trim();
        serv.abonnement(getUtilisateur(), res);
        miseAJourAbo();
    }

    /**
     * Publier un message.
     *
     * @throws JMSException
     */
    private void publishMessage () throws JMSException {
        System.out.println("votre message ?");
        String mes = scan.nextLine();
        TextMessage m = getSession().createTextMessage(mes);
        getTp().publish(m);
    }

    /**
     * Initialise plein de variables.
     *
     * @throws RemoteException
     * @throws JMSException
     */
    private void init () throws RemoteException, JMSException {
        setFact(serv.getFactory());
        setCon(getFact().createTopicConnection());
        setSession(getCon().createTopicSession(false, Session.AUTO_ACKNOWLEDGE));
        getCon().start();
        setT(getSession().createTopic(name));
        miseAJourAbo();
        setTp(getSession().createPublisher(getT()));
    }

    /**
     * Permet au client de choisir un nom d'utilisateur et un mdp.
     *
     * @throws RemoteException
     */
    private void inscription () throws RemoteException {
        do {
            System.out.println("######### Inscription #########");
            System.out.println("Veuillez choisir un nom d'utilisateur :");
            name = scan.nextLine().trim();
            System.out.println("Veuillez choisir un mot de passe :");
            password = scan.nextLine().trim();

            // Création de l'utilisateur.
            setUtilisateur(serv.Connection(name, password));
        } while (getUtilisateur() == null);
    }

    private void afficheMessage () {
        try {
            for (TopicSubscriber sub : ts) {
                TextMessage tm;
                tm = (TextMessage) sub.receive(500);
                System.out.println("-----------------");
                if (tm != null) {
                    do {
                        System.out.println(sub.getTopic().getTopicName() + " :");
                        System.out.println(tm.getText());
                        System.out.println("-----------------");
                        tm = (TextMessage) sub.receive(500);
                    } while (tm != null);
                }
            }
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }

    /**
     * On met à jour les abonnements.
     */
    private void miseAJourAbo () {
        try {
            for (TopicSubscriber s : ts) {
                s.close();
            }
            ts.clear();
            for (String s : utilisateur.getAbo()) {
                ts.add(getSession().createSubscriber(getSession().createTopic(s)));
            }
        } catch (JMSException | RemoteException e) {
            e.printStackTrace();
        }

    }

    /**
     * Fermeture propre des du topicPublisher, des topics Subscribers, de la session,
     * de la topic Connection, et du scanner.
     */
    private void close () {
        try {
            tp.close();
            for (TopicSubscriber s : ts) {
                s.close();
            }
            session.close();
            con.close();
            scan.close();
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }

    /**
     * Lancement du client.
     *
     * @param args
     */
    public static void main (String[] args) {
        try {
            new Client().run();
        } catch (RemoteException | NotBoundException | MalformedURLException | JMSException e) {
            e.printStackTrace();
        }
    }
}