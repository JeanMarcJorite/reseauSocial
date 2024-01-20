package src;

import java.io.*;
import java.net.*;
import java.util.Scanner;


public class Client{
    private String nomUtilisateur;
    private  Socket socket;
    private BufferedReader in;
    private BufferedWriter out;

/**
 * Constructeur de la classe Client.
 * @param nomUtilisateur Le nom d'utilisateur du client.
 * @throws IOException Si une erreur d'entrée/sortie se produit.
 */
    public Client(String nomUtilisateur) throws IOException {
        this.nomUtilisateur = nomUtilisateur;

    }

/**
 * Récupère le nom d'utilisateur du client.
 * @return Le nom d'utilisateur du client.
 */
    public String getNomUtilisateur() {
        return this.nomUtilisateur;
    }

/**
 * Connecte le client au serveur.
 * @param serverAdress L'adresse du serveur.
 * @param port Le port du serveur.
 * @param scanner Le scanner pour lire l'entrée de l'utilisateur.
 * @throws UnknownHostException Si l'adresse du serveur est inconnue.
 * @throws IOException Si une erreur d'entrée/sortie se produit.
 */
    public void connectionServer(String serverAdress, int port, Scanner scanner) throws UnknownHostException, IOException {
        this.socket = new Socket(serverAdress, port);
        this.in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        this.out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
        this.nomUtilisateur = this.verifName(scanner);
    }



/**
 * Déconnecte le client du serveur.
 * @throws IOException Si une erreur d'entrée/sortie se produit.
 */
    public void quitServer() throws IOException{
        this.socket.close();
    }

/**
 * Vérifie le nom d'utilisateur du client.
 * @param sc Le scanner pour lire l'entrée de l'utilisateur.
 * @throws IOException Si une erreur d'entrée/sortie se produit.
 * @return Le nom d'utilisateur vérifié.
 */
    public String verifName(Scanner sc) throws IOException {
        String nomUtil = this.nomUtilisateur;
        boolean nameVerified = false;
        while (!nameVerified) {
            System.out.println("Entrer votre pseudo : ");
            nomUtil = sc.nextLine();
            this.out.write(nomUtil);
            this.out.newLine();
            this.out.flush();
            String msg = this.in.readLine();
            System.out.println(msg);
            if (!msg.equals("Ce pseudo est deja pris, veuillez en choisir un autre")) {
            nameVerified = true;
            }
        }
        return nomUtil;
    }

    
/**
 * Envoie un message au serveur.
 * @param message Le message à envoyer.
 * @throws IOException Si une erreur d'entrée/sortie se produit.
 */
    public void write(String message) throws IOException{
        PrintWriter writer = new PrintWriter(this.socket.getOutputStream());
        writer.println(message);
        writer.flush();
    }
/**
 * Lit un message du serveur.
 * @throws IOException Si une erreur d'entrée/sortie se produit.
 */
    public void read() throws IOException{
        InputStreamReader stream  = new InputStreamReader(this.socket.getInputStream());
        BufferedReader reader = new BufferedReader(stream);
        String message = reader.readLine();
        System.out.println(message);
    }
/**
 * Démarre le client.
 * @param sc Le scanner pour lire l'entrée de l'utilisateur.
 */
    public void start(Scanner sc) {
        
        try {
            new Thread(new ReceiverClient(this.in)).start();
            while(true){
                String mess = sc.nextLine();
                this.out.write(mess);
                this.out.newLine();
                this.out.flush();
                if (mess.equals("/quit")) {
                    break;
                }
            }

        } catch (Exception e) {
            e.getStackTrace();
        }
    }

/**
 * Le point d'entrée de l'application.
 * @param args Les arguments de la ligne de commande.
 * @throws IOException Si une erreur d'entrée/sortie se produit.
 */
    public static void main(String[] args) throws IOException {
        Scanner sc = new Scanner(System.in);
        Client client = new Client("New user");
        client.connectionServer("127.0.0.1", 5555, sc);
        client.start(sc);
    }



}
