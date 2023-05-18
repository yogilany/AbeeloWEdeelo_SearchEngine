import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Projections;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.javatuples.Pair;
import org.jsoup.Jsoup;

import java.io.IOException;
import java.util.*;

import org.jsoup.select.Elements;
import org.jsoup.nodes.Element;


//import javax.lang.model.element.Element;

public class Indexer implements Runnable {
    // Number of threads
    static int numThreads = 10;
    // Finished Threads
    static int numThreadsFinished = 0;
    // Mutex to lock with it
    static Object lock = new Object();

    private static double DOCUMENTS_COUNT = 0;
    public static MongoCollection<org.bson.Document> oneURL;
    public static MongoCollection<org.bson.Document> words;
    public static HashMap<String, Pair<Integer,Integer>> URL_Frequency = new HashMap<String, Pair<Integer,Integer>>();
    public static HashMap<String, HashMap<String, URL_DATA<Integer,Integer, String, String>>> invertedFile = new HashMap<String, HashMap<String, URL_DATA<Integer,Integer, String, String>>>();

    // Run function to be executed by each thread
    public void run() {
        try {
            index();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        synchronized (lock) {
            numThreadsFinished++;
        }
    }
    public static void main(String[] args, MongoDatabase db) throws IOException {
        Preprocessing.prepareStopWords();

        Indexer.oneURL = db.getCollection("visitedURL");
        Indexer.words = db.getCollection("tempWords");

        // Start threads
        Thread[] threads = new Thread[numThreads];
        for (int i = 0; i < numThreads; i++) {
            threads[i] = new Thread(new Indexer());
            threads[i].setName(String.valueOf(i));
            threads[i].start();
        }

        // Wait for all threads to finish their work
        for (int i = 0; i < numThreads; i++) {
            try {
                threads[i].join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

//        index();

        AddToDatabase();

    }

    private static void index() throws IOException {


        Bson projection = Projections.fields(Projections.include("url"), Projections.excludeId());
        FindIterable<Document> iterDoc = oneURL.find().projection(projection);


        Iterator<Document> it = iterDoc.iterator();

        org.jsoup.nodes.Document doc = null;

        // get the length of the oneURL collection
         DOCUMENTS_COUNT = (double) oneURL.countDocuments();

         // counter for the number of documents
            int docCounter = 0;
            // get time
            long startTime = System.currentTimeMillis();
        // for each document in the collection
        while (it.hasNext()) {
            System.out.println("Scanning Document number: " + docCounter++ + " -- Time elapsed: " + (System.currentTimeMillis() - startTime) / 1000 + " seconds");

            Document fileUrlObject = (Document) it.next();
            // get the text of the url and store it in a string
            doc = getDocText(fileUrlObject.get("url") + "");
//            System.out.println("Page title is: " + pageTitle);

            if(doc == null){
                continue;
            }
            String pageTitle = doc.title();


            Elements allElements = getElements(doc);
            Integer wordCountInDoc = 0;

            // for each element in the page
            for (final Element e : allElements) {
                String text = e.text();
                String tag = e.tagName();
                if (tag == "title" || tag == "h1" || tag == "h2" || tag == "h3" || tag == "h4" || tag == "h5" || tag == "h6" || tag == "p" || tag == "td" || tag == "li") {
                    String[] words = text.split(" ");
                    wordCountInDoc += words.length;
                    int priority = 0;

                    if (tag == "title") {
                        priority = 5;
                    } else if (tag == "h1" || tag == "h2" || tag == "h3" || tag == "h4" || tag == "h5" || tag == "h6") {
                        priority = 4;
                    } else if (tag == "p") {
                        priority = 3;
                    }

                    for (String word : words) {
                        String wordAfterStemming = Preprocessing.stemming(word);
//                        System.out.println("word is: " + word);
                        String textOfElement = null;
                        String textOfElement2 = null;






                        // if the word is not a stop word
                        if (!Objects.equals(wordAfterStemming, "")) {
                            if(tag == "p"){
                                textOfElement2 = String.valueOf(e.getElementsContainingOwnText(word));
                                // get a paragraph of at least 10 words that contains the word
                                textOfElement = Jsoup.parse(textOfElement2).text();



                            }

//                            System.out.println(word + " "+ wordAfterStemming + " Text of element is: " + textOfElement);

                            // check if the word property of the Word object is in the inverted file
                            if (invertedFile.get(wordAfterStemming) == null) { // word was not found in the inverted file
                                HashMap<String, URL_DATA<Integer,Integer, String, String>> wordMap = new HashMap<String, URL_DATA<Integer,Integer, String, String>>();
                                URL_DATA<Integer,Integer, String, String> urlMap = new URL_DATA<Integer,Integer, String, String>(1,priority, pageTitle, textOfElement);
                                wordMap.put(fileUrlObject.get("url") + "", urlMap);
                                invertedFile.put(wordAfterStemming, wordMap);
                            } else { // word was found in the inverted file
                                if (invertedFile.get(wordAfterStemming).containsKey(fileUrlObject.get("url") + "")) {
                                    // same url
                                    URL_DATA<Integer,Integer, String, String> urlMap = invertedFile.get(wordAfterStemming).get(fileUrlObject.get("url") + "");
                                    urlMap.setTermFrequency(urlMap.getTermFrequency() + 1);
                                    urlMap.setPriority(urlMap.getPriority() + priority );
                                    // check if the snippet in the word is null  then set the new snipet
                                    if(urlMap.getSnippet() == null){
                                        urlMap.setSnippet(textOfElement);
                                    }
                                    invertedFile.get(wordAfterStemming).put(fileUrlObject.get("url") + "", urlMap);
                                } else {
                                    // different url
                                    URL_DATA<Integer,Integer, String, String> urlMap = new URL_DATA<Integer,Integer, String, String>(1,priority, pageTitle, textOfElement);
                                    invertedFile.get(wordAfterStemming).put(fileUrlObject.get("url") + "", urlMap);
                                }
                            }
                        }
                    }
                }
            }

            // get the url frequency and add it to the url frequency hashmap
            if (!URL_Frequency.containsKey(fileUrlObject.get("url") + "")) {
                URL_Frequency.put(fileUrlObject.get("url") + "", new Pair<Integer, Integer>( 1, wordCountInDoc));
            } else {
                URL_Frequency.put(fileUrlObject.get("url") + "", new Pair<Integer,Integer>(URL_Frequency.get(fileUrlObject.get("url") + "").getValue0() + 1, wordCountInDoc));
            }
        }
    }
    private static org.jsoup.nodes.Document getDocText(String url) throws IOException {
        try {
            return Jsoup.connect(url).get();

        } catch (IOException e) {
            System.out.println("Error in reading file:  \nFound in db but not in file system");
            return null;
        }
    }
    private static Elements getElements(org.jsoup.nodes.Document doc ) throws IOException {
        return doc.getAllElements();
    }

    private static void AddToDatabase() {
        System.out.println("Adding to database \n");
        ArrayList<org.bson.Document> documentsToInsert = new ArrayList<org.bson.Document>();

        for (String word : invertedFile.keySet()) {
            Double totTFIDF = 0.0;

            HashMap<String, URL_DATA<Integer, Integer, String, String>> Occurances = invertedFile.get(word);
            org.bson.Document IndexerDocument = new org.bson.Document("word", word);
            ArrayList<org.bson.Document> referencedat = new ArrayList<org.bson.Document>();

            for (String URL : Occurances.keySet()) {
                URL_DATA<Integer, Integer, String, String> urlMap = Occurances.get(URL);
                Integer wordCountInDoc = URL_Frequency.get(URL).getValue1();
                Double TF = (double) urlMap.getTermFrequency() / (double) wordCountInDoc;
                Double IDF = Math.log(DOCUMENTS_COUNT / (double) Occurances.size());
                Double TFIDF = TF * IDF;
                totTFIDF += TFIDF;

                org.bson.Document ReferenceDocument = new org.bson.Document("url", URL);
                URL_DATA<Integer, Integer, String, String> OccurancesInfo = Occurances.get(URL);
                ReferenceDocument.append("TF", OccurancesInfo.getTermFrequency());
                ReferenceDocument.append("Priority", OccurancesInfo.getPriority());
                ReferenceDocument.append("Frequency", URL_Frequency.get(URL).getValue0());
                ReferenceDocument.append("Title", OccurancesInfo.getTitle());
                ReferenceDocument.append("Snippet", OccurancesInfo.getSnippet());

                referencedat.add(ReferenceDocument);
            }
            IndexerDocument.append("References", referencedat);
            IndexerDocument.append("IDF", Math.log(DOCUMENTS_COUNT / (double) Occurances.size()));
            documentsToInsert.add(IndexerDocument);
        }
        words.deleteMany(new Document());
        words.insertMany(documentsToInsert);
    }

}