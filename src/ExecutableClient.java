import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;

public class ExecutableClient {
    public static void main(String[] args) {
        try (Scanner scanner = new Scanner(System.in)) {
            System.out.println("Entrer votre nom d'utilisateur : ");
            String nomUtilisateur = scanner.nextLine();
            try {
                Socket socketClient = new Socket("localhost", 5555);
                Client client = new Client(socketClient, nomUtilisateur);
                client.messageAutreClient();
                client.ecrireMessageAuClient();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
      
    }
}
