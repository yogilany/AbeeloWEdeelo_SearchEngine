package com.example.api.service;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import opennlp.tools.stemmer.PorterStemmer;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;

import static com.mongodb.client.model.Filters.eq;

public class QuerySearch {
    public static MongoCollection<org.bson.Document> words;
    static HashSet<String> stopWords;
    private java.util.ArrayList ArrayList;

    // create an object for the result to be returned
    public static class Result {
        public Object[] urls;
        public double idf;

        public Result() {
        }

        // print the result
        public boolean print() {
            System.out.println("The urls are: ");
            for (Object url : urls) {
                System.out.println(url);
            }
            System.out.println("The idf is: " + idf);
            return false;
        }



    }




    public static Result Search(String Query, MongoDatabase db) throws IOException {
        System.out.println("Entered the query processor");
        System.out.println("Query is: " + Query);

        prepareStopWords();
        words = db.getCollection("words");
        String wordAfterStemming = stemming(Query);
        System.out.println("wordAfterStemming is: " + wordAfterStemming);

        // get the word in the query form words collection
        org.bson.Document result = words.find(eq("word", wordAfterStemming)).first();
        if (result == null) {
            System.out.println("Word not found");
        } else {
            System.out.println("Word found");
            // get the array of urls
            Object[] array = ((List<?>) result.get("References")).toArray();
            // get the IDF from the result
            double idf = (double) result.get("IDF");

            // return the array of urls and the idf
            Result res = new Result();
            res.urls = (Object[]) array;
            res.idf = idf;
            return res;











        }


        return null;
    }

    public static void prepareStopWords() throws IOException {

        File inputFile = new File("./Indexer/src/stopwords.txt");


        // check if file exists
        if (!inputFile.exists()) {
            System.out.println("File not found");
        }

        stopWords = new HashSet<String>();


        BufferedReader myReader = new BufferedReader(new FileReader(inputFile));
        String word;
        while ((word = myReader.readLine()) != null) {
            stopWords.add(word);

        }

        myReader.close();

    }


    public static boolean isStopWord(String word) {
        boolean result = false;

        if (stopWords.contains(word)){
//           System.out.println("word is isStopWord: " + word);
            result = true;
        }
        return result;
    }

    public static String stemming(String str) throws IOException {
        PorterStemmer porterStemmer = new PorterStemmer();

        str = str.replaceAll(" ","");
        str = str.toLowerCase();
        str = str.replaceAll("[^a-zA-Z]", "");
        String afterPunc = str.replaceAll("\\p{Punct}","").replaceAll("[0-9]*","");

        if(!isStopWord(afterPunc)) {
            if(afterPunc.equals("the")){
                System.out.println("the in stemming");
            }

            return porterStemmer.stem(afterPunc);
        }
        return "";
    }

}