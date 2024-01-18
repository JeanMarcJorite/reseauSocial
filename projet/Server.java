import java.io.*;
import java.net.*;
import java.util.*;

public class Server {
    private Map<Socket, ClientHandler> clientThreads;
    

    public Server() {
      this.clientThreads = new HashMap<>();
      
    }

    public ServerSocket start(int port) throws IOException {
      ServerSocket serverSocket = new ServerSocket(port);
      System.out.println("Serveur lancer sur le port : " + port);
      return serverSocket;
    }

    public static void main(String[] args) {
        Server server = new Server();
        try {
            ServerSocket serverSocket = server.start(5555);
            while (true) {
                Socket clientSocket = serverSocket.accept();

                // get the name of the client
                BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                String name = in.readLine();
                BufferedWriter out = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream()));

                // verify the name
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
                out.newLine();
                out.flush();

                System.out.println("Nouveau client connecter: " + name);
                ClientHandler thread = new ClientHandler(name, clientSocket, server.clientThreads);
                thread.start();

                server.clientThreads.put(clientSocket, thread);
                System.out.println("L'utilisateur " + name + " est ajoute a la liste des utilisateurs connectes");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static boolean verificationName(String name, Socket clientSocket, Map<Socket, ClientHandler> clientThreads) throws IOException {
      for (ClientHandler c : clientThreads.values()) {
        if (name.equals(c.getUserName())) {
          return false;
        }
      }
      return true;
    }
}
