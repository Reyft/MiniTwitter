# projetRMI
# Dupanloup Rémy & Rahbari Guillaume 

############ Comment lancer le projet ? ############

Ouvrir une console et se rendre dans le dossier "bin" du projet et lancer ici la commande "rmiregistry".
Ensuite il faut lancer activemq.
Puis il suffit de se rendre dans le package "serveur" et de lancer le main de la classe
"ServeurTwitter", puis ensuite de se rendre dans le package "client" et de dans le main de la classe
"client" (autant de fois que l'on souhaite de clients).
La classe "admin" permet de visualiser le contenu du serveur, les utilisateurs et les topics auxquels ils sont abonnés.

############ Fonctionnalités du projet ############

- Un client est capable de se logger (en renseignant un nom d'utilisateur et un mot de passe).
Si le compte n'existe pas alors il est créé, sinon il se connecte (il récupère et se réabonne aux topics auxquels il était déjà abonné).
Un topic à son nom est alors créé automatiquement (cela faisant office de page personnelle).
- Le client peut alors lire les messages des topics auxquels il est abonné.
- Le client peut écrire un message dans un topic (même s'il n'est pas abonné à celui-ci).
- Le client peut également créer un nouveau topic.
- La liste des hashtags (des topics) connus par le système est maintenu côté serveur.
- Chaque hashtag est équivalent à un topic en JMS.
- Persistence des topics et des messages qu'ils contiennent.

############ Fonctionnalités non implémentées ############

- Pas de topic permettant de diffuser l'information quand à l'existence d'un nouvel hashtag.
- Notification des messages publiés dans un topic auquel l'utilisateur est abonné lorsque l'utilisateur n'est pas connecté.
- Le désabonnement à un topic. (L'abonnement étant durable, la déconnexion ne suffit pas pour se désabonner).

############ Difficultés rencontrés ############

Au vu de l'interface console que nous avons, nous avons choisi d'implémenter la méthode de lecture des messages comme un appel
du client. Si le message était afficher à l'écoute d'un évènement, (envoyé lors de la publication d'un message sur le topic), il "poperait" en plein milieu du menu et ferait sauter la boucle while qui nous permet de naviguer à l'intérieur de celui-ci.