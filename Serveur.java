import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Serveur {
  
    private ServerSocket serverSocket;
    private Socket socketClient;

    public Serveur(int port){
        try {
            this.serverSocket = new ServerSocket(port);
            this.socketClient = this.serverSocket.accept();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void estLancer(){
        if(this.socketClient.isConnected()){
            System.out.println("Le client est connectée ");
        }
    }
}
