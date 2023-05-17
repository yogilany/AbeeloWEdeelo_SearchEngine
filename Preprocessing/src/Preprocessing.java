import java.io.*;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Scanner;
import opennlp.tools.stemmer.PorterStemmer;

public class Preprocessing{
    static HashSet<String> stopWords;


//    static String [] stopWords = new String[440];

    public static void main(String[] args) {
        System.out.print("Hello from Preprocessing!");


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
