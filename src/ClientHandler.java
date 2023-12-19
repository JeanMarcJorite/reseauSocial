import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;



public class ClientHandler implements Runnable {
    private Socket clientSocket;
    private PrintWriter writer; 
    private BufferedReader bufferedReader; 
    private InputStreamReader stream;
    private String nomUtilisateur;
    private static ArrayList<ClientHandler> listeClients= new ArrayList<>();;

    public ClientHandler(Socket clientSocket) {
        try {
            this.clientSocket = clientSocket; 
            this.stream = new InputStreamReader(this.clientSocket.getInputStream());
            this.bufferedReader = new BufferedReader(stream);
            this.writer = new PrintWriter(clientSocket.getOutputStream());
            this.nomUtilisateur = bufferedReader.readLine();
            listeClients.add(this);
            sendMessageToClient("Serveur : " + this.nomUtilisateur + " est entré dans le chat");
        } catch (IOException e) {
            close(clientSocket, bufferedReader, writer);
        }     
    }
           
    public void sendMessageToClient(String messageAEnvoyer) throws IOException {
        for(ClientHandler client : listeClients){
            if(!client.nomUtilisateur.equals(nomUtilisateur)){
                client.writer.println(messageAEnvoyer);
                client.writer.flush();
            }
       }
    }

    public void close(Socket clientSocket, BufferedReader bufferedReader, PrintWriter writer){
        removeClient();
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

    public void removeClient(){
        listeClients.remove(this);
        try {
            sendMessageToClient("Serveur : " + nomUtilisateur + " a quitté le chat");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    @Override
    public void run() {
        String messageClient;   

        while (clientSocket.isConnected()) {
            try {
                messageClient = bufferedReader.readLine();
                sendMessageToClient(messageClient);
            } catch (IOException e) {
                close(clientSocket, bufferedReader, writer);
                break; 
            }
        }
    }

}