import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Projections;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.jsoup.Jsoup;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;

import opennlp.tools.stemmer.PorterStemmer;
import org.jsoup.select.Elements;
import org.jsoup.nodes.Element;


//import javax.lang.model.element.Element;


public class Indexer {

    public static MongoCollection<org.bson.Document> visitedURLs;
    public static MongoCollection<org.bson.Document> words;

    static HashMap<String, Integer> stopWords;

    public static void main(String[] args, MongoDatabase db) throws IOException {

        prepareStopWords();
        Indexer.visitedURLs = db.getCollection("visitedURLs");
        MongoCollection<Document> words = db.getCollection("words");
        index();



    }

    private static void index() throws IOException {
        Bson projection = Projections.fields(Projections.include("url"), Projections.excludeId());
        FindIterable<org.bson.Document> iterDoc = visitedURLs.find().projection(projection);
        Iterator<Document> it = iterDoc.iterator();
        Document doc = null;

        // for each document in the collection
        while (it.hasNext()) {
            org.bson.Document fileUrlObject = (org.bson.Document) it.next();
            // get the text of the url and store it in a string
            String allWords = getDocText(fileUrlObject.get("url") + "");
            Elements allElements = getElements(fileUrlObject.get("url") + "");
            Integer wordCount = 0;


            // for each element in the page
            for (final Element e : allElements) {
                String text = e.text();
                String tag = e.tagName();
                // print
                // Do not index the tags that are not text
                if (tag == "script" || tag == "style" || tag == "noscript" || tag == "head" || tag == "meta"
                        || tag == "link"
                        || tag == "input" || tag == "button" || tag == "select" || tag == "option" || tag == "form") {
                    continue;
                }
                String[] words = text.split(" ");
                wordCount += words.length;
                System.out.println("tag: " + tag + " text: " + text );

            }

                // if found , print the url and the text
            if (allWords != null) {
                System.out.println(fileUrlObject.get("url"));
                System.out.println(allWords);
            }





//            org.jsoup.nodes.Document doc = Jsoup.connect(fileUrlObject.get("url")).get();
//            String allWords =  doc.text();


        }

    }

    private static String getDocText(String url) throws IOException {
        try {
            org.jsoup.nodes.Document doc = Jsoup.connect(url).get();
            Elements all = doc.getAllElements();

            String allWords =  doc.text();
            return allWords;
        } catch (IOException e) {
            System.out.println("Error in reading file:  \nFound in db but not in file system");
            return null;
        }

    }

    private static Elements getElements(String url) throws IOException {
        try {
            org.jsoup.nodes.Document doc = Jsoup.connect(url).get();
            Elements all = doc.getAllElements();

//            String allWords =  doc.text();
            return all;
        } catch (IOException e) {
            System.out.println("Error in reading file:  \nFound in db but not in file system");
            return null;
        }

    }

    public static void prepareStopWords() throws IOException {

        File inputFile = new File("./Indexer/src/stopwords.txt");


        // check if file exists
        if (!inputFile.exists()) {
            System.out.println("File not found");
        }

        stopWords = new HashMap<String, Integer>();


        BufferedReader myReader = new BufferedReader(new FileReader(inputFile));
        String word;
        while ((word = myReader.readLine()) != null) {
            if (!word.equals("") && !stopWords.containsKey(word))
                stopWords.put(word, 1);

        }

        myReader.close();

    }

    public static boolean isStopWord(String word) {
        boolean result = false;
        if (!word.equals("") && stopWords.containsKey(word))
            result = true;
        return result;
    }

    public static String stemWord(String word) {
        PorterStemmer stemmer = new PorterStemmer();

        return stemmer.stem(word);
    }

}