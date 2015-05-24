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
import java.util.Set;

public class Client {
    // L'utilisateur.
    private UserInterface utilisateur = null;

    private TopicConnectionFactory fact = null;
    private TopicConnection con = null;

    // Notre session.
    private TopicSession session = null;

    private List<TopicSubscriber> ts = new ArrayList<TopicSubscriber>();
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
            System.out.println("0 : Quitter\n1 : Envoyer message\n2 : Voir mes messages\n3 : S'abonner\n4 : Créer un topic");
            choix = scan.nextLine().trim();
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
                case "4":
                    creerTopic();
                    break;
            }
        } while (!choix.equals("0"));
    }

    private void creerTopic () throws JMSException, RemoteException {
        System.out.println("Veuillez choisir un nom de topic : ");
        String nom = scan.nextLine().trim();
        getSession().createTopic(nom);
        serv.conservTopicName(nom);
    }

    /**
     * Permet de s'abonner. On peut s'abboner uniquement à un utilisateur.
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
    private void publishMessage () throws JMSException, RemoteException {
        TopicPublisher topicPublisher = choixTopicPublish();
        System.out.println("votre message ?");
        String mes = scan.nextLine().trim();
        TextMessage m = getSession().createTextMessage(mes);
        topicPublisher.publish(m);
        topicPublisher.close();
    }

    /**
     * On choisi sur quel topic on publie.
     * @return Le topic sur lequel on choisit de publier.
     * @throws JMSException
     * @throws RemoteException
     */
    private TopicPublisher choixTopicPublish () throws JMSException, RemoteException {
        System.out.println("Veuillez choisir un topic sur lequel publié");
        Set<String> listTopicName = serv.getListTopicName();
        for (String nom : listTopicName) {
            System.out.println(" - " + nom);
        }
        String nomChoisi = scan.nextLine().trim();

        // Vérification si le topic existe (pour éviter de le créer sinon).
        if (!listTopicName.contains(nomChoisi)) {
            System.out.println("Veuillez choisir un topic existant");
            choixTopicPublish();
        }
        return getSession().createPublisher(getSession().createTopic(nomChoisi));
    }

    /**
     * Initialise plein de variables.
     *
     * @throws RemoteException
     * @throws JMSException
     */
    private void init () throws RemoteException, JMSException {
        setFact(serv.getFactory());
        setCon(getFact().createTopicConnection(name, password));
        setSession(getCon().createTopicSession(false, Session.AUTO_ACKNOWLEDGE));
        getCon().start();
        // On créé le topic de l'utilisateur (sa page perso).
        getSession().createTopic(name);
        // On conserve le nom du topic.
        serv.conservTopicName(name);
        miseAJourAbo();
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