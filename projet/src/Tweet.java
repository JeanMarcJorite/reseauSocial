import java.util.ArrayList;

public class Tweet {
    private int id;
    private String text;
    private String username;
    private int likes;


/**
 * Constructeur de la classe Tweet.
 * @param id L'identifiant du tweet.
 * @param text Le texte du tweet.
 * @param username Le nom d'utilisateur de l'auteur du tweet.
 */
    public Tweet(int id, String text, String username) {
        this.id = id;
        this.text = text;
        this.username = username;
        this.likes = 0;
    }

/**
 * Récupère l'identifiant du tweet.
 * @return L'identifiant du tweet.
 */
    public int getId() {
        return this.id;
    }


/**
 * Récupère le texte du tweet.
 * @return Le texte du tweet.
 */
    public String getText() {
        return this.text;
    }

/**
 * Récupère le nom d'utilisateur de l'auteur du tweet.
 * @return Le nom d'utilisateur de l'auteur du tweet.
 */  
    public String getUsername() {
        return this.username;
    }

/**
 * Récupère le nombre de likes du tweet.
 * @return Le nombre de likes du tweet.
 */
    public int getLikes() {
        return this.likes;
    }

/**
 * Ajoute un like au tweet.
 */
    public void addLike() {
        this.likes++;
    }
/**
 * Supprime un like du tweet.
 */
    public void removeLike() {
        if (this.likes > 0) {
            this.likes--;
        }
    }
/**
 * Récupère l'identifiant maximum parmi une liste de tweets.
 * @param tweets La liste de tweets.
 * @return L'identifiant maximum + 1.
 */
    public int maxId(ArrayList<Tweet> tweets) {
        int max = 0;
        for (Tweet tweet : tweets) {
            if (tweet.getId() > max) {
                max = tweet.getId();
            }
        }
        return max+1;
    }

    

    
}