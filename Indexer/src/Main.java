import com.mongodb.client.MongoDatabase;

import java.io.IOException;

// Press Shift twice to open the Search Everywhere dialog and type `show whitespaces`,
// then press Enter. You can now see whitespace characters in your code.
public class Main {
    public static void main(String[] args) throws IOException {
        // Press Opt+Enter with your caret at the highlighted text to see how
        // IntelliJ IDEA suggests fixing it.
        System.out.printf("Hello and welcome from Indexer!");

//        System.out.println("Started Indexing");
//        long start = System.currentTimeMillis();
//        Indexer.indexer(args, db);
//        long end = System.currentTimeMillis();
//        System.out.println("\nIndexing Finished in " + (end - start) / 60000.0 + "minutes");

        // catch the db object
        MongoDatabase db = MongoDB.getDB();

        Indexer indexer = new Indexer();
        System.out.println("Started Indexing\n");
        long start = System.currentTimeMillis();
        Indexer.main(args, db);
        long end = System.currentTimeMillis();

        System.out.println("\nIndexing Finished in " + (end - start) / 60000.0 + " minutes");
        // print the length of the inverted file
        System.out.println("The length of the inverted file is: " + indexer.invertedFile.size());

        //print the URL Frequencies





    }
}