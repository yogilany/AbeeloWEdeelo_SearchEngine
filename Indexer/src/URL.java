public class URL {
    String url;
    Integer frequency;
    Integer priority;
    Integer tf;

    public URL(String url, Integer priority, Integer tf, Integer frequency) {
        this.url = url;
        this.priority = priority;
        this.tf = tf;
        this.frequency = frequency;
    }
}
