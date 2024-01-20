import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.*;



public class ClientHandler extends Thread {
  private Socket clientSocket;
  private String username;
  private BufferedReader in;
  private BufferedWriter out;
  private Map<Socket, ClientHandler> clientThreads;
  private Map<ClientHandler,ArrayList<ClientHandler>> jeFollow;
  private ArrayList<ClientHandler> meFollow;
  private boolean fil = false;



  /**
 * Constructeur de la classe ClientHandler.
 * @param username Le nom d'utilisateur du client.
 * @param clientSocket Le socket du client.
 * @param clientThreads La liste des threads clients.
 * @throws IOException Si une erreur d'entrée/sortie se produit.
 */
  public ClientHandler(String username, Socket clientSocket, Map<Socket, ClientHandler> clientThreads) throws IOException {
    this.username = username;
    this.clientSocket = clientSocket;
    this.clientThreads = clientThreads;
    this.in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
    this.out = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream()));
    this.jeFollow = new HashMap<>();
    this.meFollow = new ArrayList<>();
  }

/**
 * Récupère l'instance de ClientHandler pour l'utilisateur actuel.
 * @return L'instance de ClientHandler pour l'utilisateur actuel.
 */
  public ClientHandler monInstance() {
    ClientHandler moi = null; 
    for (ClientHandler thread : clientThreads.values()) {
        if (thread.getUserName().equals(this.username)) {
            moi = thread;
            break; 
        }
    }
    return moi;
}

/**
 * Ajoute un client à la liste des clients que l'utilisateur actuel suit.
 * @param cli Le client à suivre.
 */
  public void ajoutMeFollow(ClientHandler cli){
    this.meFollow.add(cli);
  }



/**
 * Récupère le nom d'utilisateur du client.
 * @return Le nom d'utilisateur du client.
 */
  public String getNomUtilisateur() {
    return this.username;
  }

/**
 * Exécute le thread du client.
 */
  public void run() {
    try {
      while (true) {

        String message = this.in.readLine();
        System.out.println(this.username + " : " + message);

        if (fil) {
          if (message.startsWith("/retour")) {
              fil = false;}
        }
          else{
            if (message.startsWith("/fil")) {
              fil = true;
          }
        }

        if (message.startsWith("/")) {
          this.getCommand(message);
        }
        else if (message.startsWith("@")) {
          this.sendPrivateMessage(this.username, message);
        }
        else {
          for (ClientHandler thread : clientThreads.values()) {
            if (thread != this && fil==false) {
              thread.sendMessage(this.username, message);
            }
          }
        }
      }
    } catch (IOException | NullPointerException e) {
      System.out.println("Client déconnecté");
      this.clientThreads.remove(this.clientSocket);
    }
  }




/**
 * Récupère le nom d'utilisateur du client.
 * @return Le nom d'utilisateur du client.
 */
  public String getUserName() {
    return this.username;
  }

  /**
 * Envoie un message à l'utilisateur.
 * @param userName Le nom d'utilisateur qui envoie le message.
 * @param message Le message à envoyer.
 */
  public void sendMessage(String userName, String message) {
    if(this.fil==false){
      try {
        out.write(userName + " : " + message);
        out.newLine();
        out.flush();
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
  }


  /**
 * Envoie un message de commande à l'utilisateur.
 * @param message Le message de commande à envoyer.
 */
  public void sendCommandMessage(String message) {
    try {
      out.write(message);
      out.newLine();
      out.flush();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  /**
 * Traite une commande reçue.
 * @param message La commande à traiter.
 */
  public void getCommand(String message) {
    String[] parts = message.split(" ");
    String command = parts[0];


    if (message.equals("/quit")) {
      try {
        this.clientSocket.close();
      } catch (IOException e) {
        e.printStackTrace();
      }
    }

    else if (command.equals("/follow")) {
      if (parts.length > 1) {
          String userToFollow = parts[1];
          for (ClientHandler thread : clientThreads.values()) {

            

              if (thread.getUserName().equals(userToFollow)) {
                  ArrayList<ClientHandler> following = jeFollow.get(this);
                  if (following == null) {
                      following = new ArrayList<>();
                  }
                  following.add(thread);
                  jeFollow.put(this, following);
                  thread.ajoutMeFollow(monInstance());
                  this.sendCommandMessage("Vous suivez maintenant " + userToFollow);

                  return;
              }
          }
          this.sendCommandMessage("Utilisateur non trouvé");
      }
  }
      
  










    else if (message.equals("/nbusers")) {
      this.sendCommandMessage("Il y a " + this.clientThreads.size() + " utilisateurs connectés");
    }





    else if (message.equals("/users")) {
      String users = "";
      for (ClientHandler thread : clientThreads.values()) {
        users += thread.getUserName() + " ";
      }
      this.sendCommandMessage("Liste des utilisateurs connectés: " + users);
    }

    else if(command.equals("/unfollow")){
      if (parts.length > 1) {
        String userToUnfollow = parts[1];
        for (ClientHandler thread : clientThreads.values()) {
            if (thread.getUserName().equals(userToUnfollow)) {
                ArrayList<ClientHandler> following = jeFollow.get(this);
                if (following != null) {
                    following.remove(thread);
                    thread.meFollow.remove(monInstance());
                    this.sendCommandMessage("Vous ne suivez plus " + userToUnfollow);
                    return;
                }
            }
        }
        this.sendCommandMessage("Utilisateur non trouvé dans vos abonnements");

      }

    }





    else if (message.equals("/nbfollow")) {
      this.sendCommandMessage("Vous suivez " + this.jeFollow.size() + " utilisateurs");
    }

    else if (message.equals("/nbfollowed")) {
      this.sendCommandMessage("Vous êtes suivi par " + this.meFollow.size() + " utilisateurs");
    }

    else if (message.equals("/followedlist")) {
      String users = "";
      for (ClientHandler c : this.meFollow) {
        users += c.getNomUtilisateur() + " ";
      }
      this.sendCommandMessage("Liste des utilisateurs qui vous suivent: " + users);
    }

    else if (message.equals("/followlist")) {
    String users = "";
    ArrayList<ClientHandler> following = this.jeFollow.get(this);
    if (following != null) {
        for (ClientHandler user : following) {
            users += user.getNomUtilisateur() + ", ";
        }
    }
    this.sendCommandMessage("Vous suivez : " + users);
}


    else if (message.equals("/help")) {
      this.sendCommandMessage("Liste des commandes et leur utilitée: \n @<pseudo> <message> : Envoyer un message privée à un utilisateur \n /follow pseudo : Pour s'abonner à quelqu'un  \n /nbusers : Pour voir le nombre d'utilisateurs connectées \n /users : Pour voir le nom des utilisateurs connectés  \n /followlist : Pour voir les utilisateurs que je follow \n /followedlist : Pour voir les utilisateurs qui me follow \n /nbfollowed : Pour voir le nombre d'utilisateurs qui me follow \n /nbfollow : Pour voir le nombre d'utilisateurs que je follow \n /unfollow pseudo : Pour ne plus suivre quelqu'un \n /fil : Pour acceder aux tweets \n /retour : Pour quitter l'espace tweet \n /jaime id : pour aimer un tweet \n /jaimepas id : pour retirer le j'aime d'un tweet \n /delete id : pour supprimer son tweet \n /quit : Se déconnecter  \n /help : Pour revoir les commandes \n");
    }









    ////////////////////////////////////////////////////

    else if (message.startsWith("/tweet")) {
      int maxId = 0;
      for(Tweet tweet : ServeurData.tweets) {
        if (tweet.getId() > maxId) {
            maxId = tweet.getId();
        }
        maxId++;
      }

      String tweetContent = message.substring(7);
      Tweet newTweet = new Tweet(maxId, tweetContent,this.username);
      ServeurData.tweets.add(newTweet);
  }



  else if (message.equals("/fil")) {
    this.fil= true;
    this.sendCommandMessage("Vous êtes maintenant dans le fil d'actualité, vous avez quitter le chat générale  \n");
    ScheduledFuture<?> refresh = ServeurData.executor.scheduleAtFixedRate(new Runnable() {
    private int lastTweetIndex = 0;

        @Override
        public void run() {
            if (message.equals("/fil")) {
                String feed = "";
                for (int i = lastTweetIndex; i < ServeurData.tweets.size(); i++) {
                    Tweet tweet = ServeurData.tweets.get(i);
                    feed += "id : " +tweet.getId() + "\npseudo : " + tweet.getUsername() + " :  \n" + tweet.getText() + " (" + tweet.getLikes() + " likes)";
                    feed+= "\n -----------------------------------\n";
                }
                if (!feed.isEmpty()) {
                    sendCommandMessage(feed);
                }
                lastTweetIndex = ServeurData.tweets.size();
            }
        }
    }, 0, 2, TimeUnit.SECONDS);
  }

  else if (command.equals("/jaime")) {
    if (parts.length > 1) {
        try {
            int tweetId = Integer.parseInt(parts[1]);
            for (Tweet tweet : ServeurData.tweets) {
                if (tweet.getId() == tweetId) {
                    tweet.addLike();
                    this.sendCommandMessage("Vous avez aimé le tweet " + tweetId);
                    return;
                }
            }
            this.sendCommandMessage("Tweet non trouvé");
        } catch (NumberFormatException e) {
            this.sendCommandMessage("ID de tweet invalide");
        }
    }
  }

  else if (command.equals("/jaimepas")) {
    if (parts.length > 1) {
        try {
            int tweetId = Integer.parseInt(parts[1]);
            for (Tweet tweet : ServeurData.tweets) {
                if (tweet.getId() == tweetId) {
                    tweet.removeLike();
                    this.sendCommandMessage("Vous avez enlevé votre 'like' du tweet " + tweetId);
                    return;
                }
            }
            this.sendCommandMessage("Tweet non trouvé");
        } catch (NumberFormatException e) {
            this.sendCommandMessage("ID de tweet invalide");
        }
    }
  }
  else if (command.equals("/delete")) {
    if (parts.length > 1) {
        try {
            int tweetId = Integer.parseInt(parts[1]);
            Iterator<Tweet> iterator = ServeurData.tweets.iterator();
            while (iterator.hasNext()) {
                Tweet tweet = iterator.next();
                if (tweet.getId() == tweetId && tweet.getUsername().equals(this.username) || this.username.equals("admin")) {
                    iterator.remove();
                    this.sendCommandMessage("Vous avez supprimé le tweet " + tweetId);
                    return;
                }
            }
            this.sendCommandMessage("Tweet non trouvé ou vous n'êtes pas l'auteur du tweet");
        } catch (NumberFormatException e) {
            this.sendCommandMessage("ID de tweet invalide");
        }
    }
  }
  else if (command.equals("/retour")) {
    this.fil = false;
    this.sendCommandMessage("Vous avez quitté le fil d'actualité ");
    this.sendCommandMessage("\n Bienvenue sur le chat Général, endroit ou tu peux discuter avec tout le monde \n ");
  }
  else if (command.startsWith("/remove")) {
    String username = command.substring(6);
    if(this.username.equals("admin")) {
        Iterator<Map.Entry<Socket, ClientHandler>> iterator = clientThreads.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<Socket, ClientHandler> entry = iterator.next();
            if (entry.getValue().getUserName().equals(username)) {
                iterator.remove();
                System.out.println("L'utilisateur " + username + " a ete supprime de la liste des utilisateurs connectes");
                return;
            }
        }
    }
}


  







    else {
      this.sendCommandMessage("Commande inconnue");
    }











  }

  /**
 * Envoie un message privé à un autre utilisateur.
 * @param userName Le nom d'utilisateur qui envoie le message.
 * @param message Le message à envoyer.
 */
  public void sendPrivateMessage(String userName, String message) {
    String[] parts = message.split(" ");
    String user = parts[0].substring(1);
    String msg = "[MP] ";
    for (int i = 1; i < parts.length; i++) {
      msg += parts[i] + " ";
    }
    int cpt = 0;
    for (ClientHandler thread : clientThreads.values()) {
      if (thread.getUserName().equals(user)) {
        cpt ++;
        thread.sendMessage(userName, msg);
      }
    }
    if (cpt == 0) {
      this.sendCommandMessage("Utilisateur non trouvé");
    }
  }



  
}
