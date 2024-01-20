package src;

import java.util.ArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

public class ServeurData {
    public static ArrayList<Tweet> tweets = new ArrayList<>();
    public static ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
}