public class Word {
    String word;
    double idf;

    public Word(String word, double idf) {
        this.word = word;
        this.idf = idf;
    }

    public Object getWord() {
        return word;
    }
}
