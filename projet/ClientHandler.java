import java.io.*;
import java.net.*;
import java.util.*;


public class ClientHandler extends Thread {
  private Socket clientSocket;
  private String username;
  private BufferedReader in;
  private BufferedWriter out;
  private Map<Socket, ClientHandler> clientThreads;
  private Map<ClientHandler,ArrayList<ClientHandler>> jeFollow;
  private ArrayList<Client> meFollow;


  public ClientHandler(String username, Socket clientSocket, Map<Socket, ClientHandler> clientThreads) throws IOException {
    this.username = username;
    this.clientSocket = clientSocket;
    this.clientThreads = clientThreads;
    this.in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
    this.out = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream()));
    this.jeFollow = new HashMap<>();
    this.meFollow = new ArrayList<>();
  }

  public String getNomUtilisateur() {
    return this.username;
  }

  public void run() {
    try {
      while (true) {

        String message = this.in.readLine();
        System.out.println(this.username + " : " + message);

        if (message.startsWith("/")) {
          this.getCommand(message);
        }
        else if (message.startsWith("@")) {
          this.sendPrivateMessage(this.username, message);
        }
        else {
          for (ClientHandler thread : clientThreads.values()) {
            if (thread != this) {
              thread.sendMessage(this.username, message);
            }
          }
        }
      }
    } catch (IOException | NullPointerException e) {
      System.out.println("Client disconnected");
      this.clientThreads.remove(this.clientSocket);
    }
  }

  public String getUserName() {
    return this.username;
  }

  public void sendMessage(String userName, String message) {
    try {
      out.write(userName + " : " + message);
      out.newLine();
      out.flush();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public void sendCommandMessage(String message) {
    try {
      out.write(message);
      out.newLine();
      out.flush();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

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

    else if (command.equals("/suivre")) {
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





    else if (message.equals("/follow")) {
      this.sendCommandMessage("Vous suivez " + this.jeFollow.size() + " utilisateurs");
    }

    else if (message.equals("/followed")) {
      this.sendCommandMessage("Vous êtes suivi par " + this.meFollow.size() + " utilisateurs");
    }

    else if (message.equals("/followedlist")) {
      String users = "";
      for (Client c : this.meFollow) {
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

    else if (message.equals("/uptime")) {
      this.sendCommandMessage("Le serveur est en ligne depuis " + System.currentTimeMillis() + " ms");
    }


    else if (message.equals("/help")) {
      this.sendCommandMessage("Commands list: @<username> <message>, /quit, /nbusers, /users, /uptime, /help");
    }


    else {
      this.sendCommandMessage("Unknown command");
    }








  }

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
      this.sendCommandMessage("User not found");
    }
  }
}
