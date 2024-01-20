import java.io.*;
import java.net.*;
import java.util.*;

public class Server {
    private Map<Socket, ClientHandler> clientThreads;
    //private ServerData data;
    
/**
 * Constructeur de la classe Server.
 * Initialise la map des threads clients.
 */
    public Server() {
      this.clientThreads = new HashMap<>();
    }
 
/**
 * Démarre le serveur sur un port spécifique.
 * @param port Le port sur lequel le serveur doit être démarré.
 * @return Le socket du serveur.
 * @throws IOException Si une erreur d'entrée/sortie se produit.
 */
    public ServerSocket start(int port) throws IOException {
      ServerSocket serverSocket = new ServerSocket(port);
      System.out.println("Serveur lancer sur le port : " + port);
      return serverSocket;
    }
/**
 * Le point d'entrée de l'application.
 * @param args Les arguments de la ligne de commande.
 */
    public static void main(String[] args) {
        Server server = new Server();
        try {
            ServerSocket serverSocket = server.start(5555);
            while (true) {
              if (System.in.available() > 0) {

            }

                Socket clientSocket = serverSocket.accept();

                
                BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                String name = in.readLine();
                BufferedWriter out = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream()));

                boolean nameVerified = verificationName(name, clientSocket, server.clientThreads);
                System.out.println("pseudo vérifie: " + nameVerified);
                if (!nameVerified) {
                  out.write("Ce pseudo est deja pris, veuillez en choisir un autre");
                  out.newLine();
                  out.flush();
                }
                
                while (!nameVerified) {
                  name = in.readLine();
                  nameVerified = verificationName(name, clientSocket, server.clientThreads);
                  if (!nameVerified) {
                    out.write("Ce pseudo est deja pris, veuillez en choisir un autre");
                    out.newLine();
                    out.flush();
                  }
                  else {
                    nameVerified = true;
                  }
                }

                out.write("Tu es connecte en tant que " + name);
                out.write(" Si tu souhaite voir les commandes disponibles, tape /help");
                out.newLine();
                out.flush();

                System.out.println("Nouveau client connecter: " + name);
                ClientHandler thread = new ClientHandler(name, clientSocket, server.clientThreads);
                thread.start();

                server.clientThreads.put(clientSocket, thread);
                System.out.println("L'utilisateur " + name + " est ajoute a la liste des utilisateurs connectes");
                //data.saveToJson("data.json");
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    
/**
 * Vérifie si un nom d'utilisateur est déjà utilisé par un autre client.
 * @param name Le nom d'utilisateur à vérifier.
 * @param clientSocket Le socket du client.
 * @param clientThreads La map des threads clients.
 * @return true si le nom d'utilisateur n'est pas déjà utilisé, false sinon.
 * @throws IOException Si une erreur d'entrée/sortie se produit.
 */
    public static boolean verificationName(String name, Socket clientSocket, Map<Socket, ClientHandler> clientThreads) throws IOException {
      for (ClientHandler c : clientThreads.values()) {
        if (name.equals(c.getUserName())) {
          return false;
        }
      }
      return true;
    }



    //public ServerData getData() {
    //    return data;
    //}
}
