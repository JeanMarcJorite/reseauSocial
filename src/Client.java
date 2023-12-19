import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;


public class Client{

    private PrintWriter writer;
    private Socket socket;
    private String nomUtilisateur;
    private BufferedReader reader;

    public Client(Socket socket, String username){ 
        try {
            this.socket = socket; // se connecte au serveur
            this.writer = new PrintWriter(socket.getOutputStream()); // envoyer les données au serveur
            InputStreamReader stream = new InputStreamReader(socket.getInputStream()); // Lire les données
            this.reader = new BufferedReader(stream); // Lire les données du serveur
            this.nomUtilisateur = username;
        }catch (IOException e) {
            close(socket, reader, writer);  
        }
    }

    public String getNomUtilisateur(){
        return this.nomUtilisateur;
    }

    public void setNomUtilisateur(String newNom){
        this.nomUtilisateur = newNom;
    }

    public void ecrireMessageAuClient(){
        try{
            writer.println(this.nomUtilisateur);
            writer.flush();
            try (Scanner scanner = new Scanner(System.in)) {
                while (socket.isConnected()) {
                    String messageClient = scanner.nextLine();
                    writer.println(nomUtilisateur+ " : "+ messageClient);
                    writer.flush();
                }
            }
        }catch(Exception e){
            close(socket, reader, writer);
        }
    }   

    public void deconnexionClient(){
        try {
            socket.close();
            System.out.println(nomUtilisateur + " est deconnecter");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void messageAutreClient(){
        Thread thread = new Thread(new ClientMessageHandle(this, socket, reader, writer));
        thread.start();
    }

    public void close(Socket clientSocket, BufferedReader bufferedReader, PrintWriter writer){
        try {
            if (bufferedReader != null) {
                bufferedReader.close();
            }
            if (writer != null) {
                writer.close();
            }
            if (clientSocket != null) {
                clientSocket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

        
}