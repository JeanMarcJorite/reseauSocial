import java.io.IOException;
import java.net.ServerSocket;

public class ExecutableServeur {
    
    public static void main(String[] args) {
        try {
            ServerSocket serverSocket = new ServerSocket(5555);
            Serveur serveur = new Serveur(serverSocket);
            serveur.lancerServeur();
        } catch (IOException e) {
            e.printStackTrace();
        }
        
    }
}