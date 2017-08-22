import java.sql.Timestamp;

public class NewsArticle {
    private String title;
    private String description;
    private String url;
    private Timestamp pubDate;
    private String category;

    //constructor
    public NewsArticle(String title, String description, String url, java.sql.Timestamp pubDate, String category) {
        this.title = title;
        this.description = description;
        this.url = url;
        this.pubDate = pubDate;
        this.category = category;
    }


    //getter
    public String getTitle() { return title; }

    public String getDescription() { return description; }

    public String getUrl() { return url; }

    public Timestamp getPubDate() { return pubDate; }

    public String getCategory() { return category; }

}
