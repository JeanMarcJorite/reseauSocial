package src;


import java.io.*;


public class ReceiverClient implements Runnable {

  private boolean inFeedMode = false;
  
  private BufferedReader in;
/**
 * Constructeur de la classe ReceiverClient.
 * @param in Le BufferedReader pour lire les messages entrants.
 */
  public ReceiverClient(BufferedReader in) {
    this.in = in;
  }
  
/**
 * Exécute le thread du client récepteur.
 * Lit les messages entrants et les affiche à l'écran.
 * Gère également les commandes spéciales comme [MP], /fil et /retour.
 */
  public void run() {
    try {
      while (true) {
        String msg = this.in.readLine();
        if (msg == null) {
          break;
        }
        else {
          if (msg.startsWith("/fil")) {
            inFeedMode = true;
            continue;
          }
          if (msg.startsWith("/retour")) {
            inFeedMode = false;
            continue;
          }
          else {
            if(!inFeedMode)
            System.out.println(msg);
          }
        }
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}