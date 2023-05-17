public class URL_DATA<I extends Number, I1 extends Number, S, S1> {
    private Integer termFrequency;
    private Integer priority;
    private String title;
    private String snippet;



    public URL_DATA(Integer tf, Integer priority, String title, String snippet) {
        this.termFrequency = tf;
        this.priority = priority;
        this.title = title;
        this.snippet = snippet;
    }

    public Integer getTermFrequency() {
        return termFrequency;
    }

    public Integer getPriority() {
        return priority;
    }

    public String getTitle() {
        return title;
    }

    public String getSnippet() {
        return snippet;
    }

    public void setTermFrequency(Integer tf) {
        this.termFrequency = tf;
    }

    public void setPriority(Integer priority) {
        this.priority = priority;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setSnippet(String snippet) {
        this.snippet = snippet;
    }

}
