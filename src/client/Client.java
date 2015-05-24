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
    private UserInterface me = null;
    private TopicConnectionFactory fact = null;
    private TopicConnection con = null;
    private TopicSession session = null;
    private TopicPublisher tp = null;
    private List<TopicSubscriber> ts = new ArrayList<TopicSubscriber>();
    private Topic t = null;
    private static Scanner scan = new Scanner(System.in);

    public Client () {
    }

    public void setFact (TopicConnectionFactory factory) {
        fact = factory;
    }

    public UserInterface getMe () {
        return me;
    }

    public void setMe (UserInterface me) {
        this.me = me;
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

    public static void main (String[] args) {
        Client c = new Client();
        c.run();
    }

    private void run () {
        Serveur serv;
        String name = null;
        String password = null;
        try {
            serv = (Serveur) Naming.lookup("rmi://localhost/MiniTwitter");
            System.out.println("Name + password ?");
            name = nextString();
            password = nextString();
            setMe(serv.Connection(name, password));
            if (getMe() == null) {
                System.out.println("Pseudo deja pris");
            }
            setFact(serv.getFactory());
            setCon(getFact().createTopicConnection());
            setSession(getCon().createTopicSession(false, Session.AUTO_ACKNOWLEDGE));
            getCon().start();
            setT(getSession().createTopic(name));
            miseAJourAbo();
            setTp(getSession().createPublisher(getT()));
            int choix = 0;
            while (choix != 4) {
                System.out.println("1 : Envoyer message\n2 : Voir mes messages\n3 : S'abonner\n4 : Quitter");
                choix = nextInt();
                nextString(); // On vide la ligne
                switch (choix) {
                    case 1:
                        System.out.println("votre message ?");
                        String mes = nextString();
                        TextMessage m = getSession().createTextMessage(mes);
                        getTp().publish(m);
                        break;
                    case 2:
                        afficheMessage();
                        break;
                    case 3:
                        System.out.println("Choississez parmis :");
                        System.out.println(serv.affichage());
                        String res = nextString();
                        serv.abonnement(getMe(), res);
                        miseAJourAbo();
                        break;
                    default:
                        break;
                }
            }
            close();
            System.out.println("Au revoir");
        } catch (MalformedURLException | RemoteException | NotBoundException
                | JMSException e) {
            e.printStackTrace();
        }
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

    private void miseAJourAbo () {
        try {
            for (TopicSubscriber s : ts) {
                s.close();
            }
            ts.clear();
            for (String s : me.getAbo()) {
                ts.add(getSession().createSubscriber(getSession().createTopic(s)));
            }
        } catch (JMSException | RemoteException e) {
            e.printStackTrace();
        }

    }

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

    private static String nextString () {
        String res;
        res = scan.nextLine();
        return res;
    }

    private static int nextInt () {
        int res;
        res = scan.nextInt();
        return res;
    }
}