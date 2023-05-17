import javax.swing.text.Document;
import java.util.*;

public class Ranker {


    public class word_info {
        // Document frequency of the word
        public String word;
        public int DF;
        // Inverse document frequency
        public double IDF;
        // Mapping of each URL and the TF of the word in each URL
        // Key: URL, Value: Term frequency (TF)
        // Ex. {"www.google.com" : 5 }
        public HashMap<String, Integer> URL_data;
    }

    private HashMap<String, word_info> get_word_info(String[] words) {

        Document[] docs = new Document[words.length];

        HashMap<String, word_info> resultmap = new HashMap<>();
        for (Document doc: docs){
            word_info info = new word_info();
            info.word = doc.get("word");
            info.IDF = doc.get("IDF");
            info.URL_data = new HashMap<>();
            for (String url: doc.get("URLs")){
                info.URL_data.put(url, doc.get("TF"));
            }
            resultmap.put(info.word, info);
        }

        return resultmap;
    }


    public HashMap<String, Double> calculate_relevance(HashMap<String, Double> query_vector, String[] query_words) throws InterruptedException {

        HashMap<String, HashMap<String, Double>> documents_vector = new HashMap<>();

        // Get information about the query from the database
        HashMap<String, word_info> query_documents = get_word_info(query_vector.keySet().toArray(new String[0]));

        for (String word : query_documents.keySet()) {
            // get idf for the word
            double IDF = query_documents.get(word).IDF;
            int DF = query_documents.get(word).DF;

            // get count of words in vector
            int word_count_in_query = Collections.frequency(Arrays.stream(query_words).toList(), word);
            query_vector.put(word, IDF * word_count_in_query);

            // get the urls for this word
            HashMap<String, Integer> urls =
                    query_documents.get(word).URL_data;
            double query_word_tfidf = 0d;
            double document_word_tfidf = 0d;


            // calculate tf-idf for each url's words
            for (String url : urls.keySet()) {

                // Term frequency in this URL
                int tf = urls.get(url);
                // Calculate TF-IDF score for this document (URL)
                document_word_tfidf = (tf * IDF);
                // If this document does not exist in the documents vector, create it
                if (documents_vector.get(url) == null) {
                    documents_vector.put(url, new HashMap<>());
                    documents_vector.get(url).put(word, document_word_tfidf);
                } else {

                    documents_vector.get(url).put(word, document_word_tfidf);
                }
            }
        }
        // calculate relevance score for each page using cosine-similarity
        HashMap<String, Double> relevantDocuments = new HashMap<>();
        for (String doc : documents_vector.keySet()) {
            double relevance_score = 0d;
            int dotproduct = 0;
            for (String word : query_vector.keySet()) {
                if (documents_vector.get(doc).containsKey(word)) {
                    dotproduct += documents_vector.get(doc).get(word) * query_vector.get(word);
                }
            }
            double norm_document = 0;
            for(double val : documents_vector.get(doc).values()) {
                norm_document += val * val;
            }
            double norm_query = 0;
            for(double val : query_vector.values()) {
                norm_query += val * val;
            }
            double norm_sqrt = Math.sqrt(norm_document * norm_query);
            relevance_score = dotproduct / norm_sqrt;
            relevantDocuments.put(doc, relevance_score);
        }
        return relevantDocuments;
    }

}
