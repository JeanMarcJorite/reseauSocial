import java.io.BufferedReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ClientMessageHandle implements Runnable{
    private Socket socket;
    private BufferedReader reader;
    private PrintWriter writer;
    private Client client;

    public ClientMessageHandle(Client client, Socket socket, BufferedReader reader, PrintWriter writer) {
        this.socket = socket;
        this.reader = reader;
        this.writer = writer;
        this.client = client;
    }

    

    @Override
    public void run() {
        String messageGroupe;

        while (socket.isConnected()) {
            try {
                messageGroupe = reader.readLine();
                System.out.println(messageGroupe);
            } catch (Exception e) {
                this.client.close(socket, reader, writer);
            }
        }
    }
}