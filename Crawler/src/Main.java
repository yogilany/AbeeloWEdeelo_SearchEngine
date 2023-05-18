import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

// Press Shift twice to open the Search Everywhere dialog and type `show whitespaces`,
// then press Enter. You can now see whitespace characters in your code.
public class Main {
    static BlockingQueue<String> linksQueue = new LinkedBlockingQueue<>(10000);
    static ArrayList<String> visited = new ArrayList<>();
    static volatile Integer processed_links = 0;
    static FileWriter outputfile;


    static int max_number_of_links = 6000;


    static inc numberoflinks;


    static {
        try {
            outputfile = new FileWriter("C:\\Users\\Hp\\IdeaProjects\\crawler\\out.txt", true);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void main(String[] args) throws IOException {


        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter number of threads: ");

        int number_of_threads = scanner.nextInt();


        String path = "C:\\Users\\Hp\\IdeaProjects\\crawler\\out.txt";
        try (BufferedReader br2 = new BufferedReader(new FileReader(path))) {
            String line2;
            // Read the file line by line
            while ((line2 = br2.readLine()) != null) {
                linksQueue.add(line2);
            }
        }


        max_number_of_links -= linksQueue.size();
        inc numberoflinks = new inc(linksQueue.size());

        if (max_number_of_links <= 5990) {
            path = "C:\\Users\\Hp\\IdeaProjects\\crawler\\out.txt";
            try (BufferedReader reader = new BufferedReader(new FileReader(path))) {
                String line;
                String previousLine = null;

                while ((line = reader.readLine()) != null) {
                    if (previousLine != null) {
                        // Process the previous line here
                        visited.add(line); //add all except last line
                    }
                    previousLine = line;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }


        scanner.close();


        Thread[] threads = new Thread[number_of_threads];

        for (int i = 0; i < number_of_threads; i++) {

            threads[i] = new Thread(new crawler(linksQueue, visited, numberoflinks, outputfile, max_number_of_links));
            threads[i].start();
        }


        for (int i = 0; i < number_of_threads; i++) {
            try {
                threads[i].join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }


        MongoClient client = MongoClients.create("mongodb+srv://yogilany:7kkmGoukZJIOVIyK@abeelowedeelo.vduhnjn.mongodb.net/?retryWrites=true&w=majority");
        MongoDatabase db = client.getDatabase("Dev");
        MongoCollection col = db.getCollection("visitedURL");


        List<org.bson.Document> d = new ArrayList<>();

        for (int i = 0; i < visited.size(); i++) {
            org.bson.Document doc = new org.bson.Document();
            doc.put("url", visited.get(i));
            d.add(doc);
        }

        col.insertMany(d);


        try {
            outputfile = new FileWriter("C:\\Users\\Hp\\IdeaProjects\\crawler\\out.txt"); // earase the file
        } catch (IOException e) {
            throw new RuntimeException(e);
        }


    }
}