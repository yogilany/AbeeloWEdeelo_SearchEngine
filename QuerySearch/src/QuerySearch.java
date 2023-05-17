import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

import javax.swing.text.Document;
import java.io.IOException;
import java.util.HashSet;
import static com.mongodb.client.model.Filters.eq;

import static java.util.Collections.eq;

public class QuerySearch {
    public static MongoCollection<org.bson.Document> words;


    public void QuerySearch(String Query, MongoDatabase db) throws IOException {
        System.out.println("Entered the query processor");
        Preprocessing.prepareStopWords();
        words = db.getCollection("words");
        String wordAfterStemming = Preprocessing.stemming(Query);
        org.bson.Document result = words.find(eq("word", wordAfterStemming)).first();
        if (result == null) {
            System.out.println("Word not found");
        } else {
            System.out.println("Word found");
            HashSet<String> urls = (HashSet<String>) result.get("References");
            System.out.println(urls);
        }


    }

}