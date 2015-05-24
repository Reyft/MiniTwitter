# projetRMI

############ Comment lancer le projet ? ############

Ouvrir une console et se rendre dans le dossier "bin" du projet et lancer ici la commande "rmiregistry".
Ensuite il faut lancer activemq.
Puis il suffit de se rendre dans le package "serveur" et de lancer le main de la classe
"Serveur", puis ensuite de se rendre dans le package "client" et de dans le main de la classe
"client" (autant de fois que l'on souhaite de clients).

############ Fonctionnalités du projet ############

- Un client est capable de se logger (en renseignant un nom d'utilisateur et un mot de passe).
Si le compte n'existe pas alors il est créé, sinon il se connecte.
Un topic à son nom est alors créé automatiquement (cela faisant office de page personnelle).
- Le client peut alors lire les messages des topics auxquels il est abonné.
- Le client peut écrire un message dans un topic (même s'il n'est pas abonné à celui-ci).
- Le client peut également créer un nouveau topic.
- La liste des hashtags (des topics) connus par le système est maintenu côté serveur.
- Chaque hashtag est équivalent à un topic en JMS.

############ Fonctionnalités non implémentées ############

- Pas de topic permettant de diffuser l'information quand à l'existence d'un nouvel hashtag.
- Persistence des topics et des messages qu'ils contiennent.
- Notification des messages publiés dans un topic auquel l'utilisateur est abonné lorsque l'utilisateur n'est pas connecté.

############ Difficultés rencontrés ############

Malgré nos recherches nous n'avons pas réussi à rendre persistent les topics et les messages qu'ils contiennent.