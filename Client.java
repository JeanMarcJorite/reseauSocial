import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class Client{
    private PrintWriter writer;
    private Socket socket;
    private String nomUtilisateur;

    public Client(String host, int port){ 
        try {
            this.socket = new Socket(host, port);
            InputStreamReader stream = new InputStreamReader(socket.getInputStream());  
            writer = new PrintWriter(socket.getOutputStream());
            BufferedReader reader = new BufferedReader(stream);
            writer.println("Entrer un nom d'utilisateur ");
            nomUtilisateur = reader.readLine();
            System.out.println(nomUtilisateur + " est connecté");
            while (true) {
                ecrire();
            }
        }catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void ecrire(){
        
        try {
            InputStreamReader stream = new InputStreamReader(socket.getInputStream());  
            BufferedReader reader = new BufferedReader(stream);
            String message = reader.readLine();
            ecrireMessage(message);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void ecrireMessage(String message){
        try {
            PrintWriter writer = new PrintWriter(socket.getOutputStream());
            writer.println(message);
            writer.flush();
        } catch (IOException e) {
            e.printStackTrace();
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

    
}