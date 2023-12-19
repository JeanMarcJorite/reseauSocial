import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Serveur {
    private ServerSocket serverSocket;
    
    public Serveur(ServerSocket serverSocket){
        this.serverSocket = serverSocket;
    }

    public void lancerServeur(){
        try {
            while (!serverSocket.isClosed()) {
                Socket socket = serverSocket.accept();
                System.out.println("connexion d'un client ");
                ClientHandler clientHandler = new ClientHandler(socket);
                Thread t = new Thread(clientHandler);
                t.start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}