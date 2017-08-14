/**
 * Created by yash.raj on 09/08/17.
 */
public class NewsArticle {
    private String title;
    private String desc;
    private String url;
    private String pubDate;
    private String category;

    //constructor
    public NewsArticle(String title, String desc, String url, String pubDate, String category) {
        this.title = title;
        this.desc = desc;
        this.url = url;
        this.pubDate = pubDate;
        this.category = category;
    }

    public NewsArticle(String title, String url, String pubDate,String category) {
        this.title = title;
        this.url = url;
        this.pubDate = pubDate;
        this.desc = null;
        this.category = category;
    }

    //getter
    public String getTitle() { return title; }

    public String getDesc() { return desc; }

    public String getUrl() { return url; }

    public String getPubDate() { return pubDate; }

    public String getCategory() { return category; }

    //setter
    public void setTitle(String title) { this.title = title; }

    public void setDesc(String desc) { this.desc = desc; }

    public void setUrl(String url) { this.url = url; }

    public void setPubDate(String pubDate) { this.pubDate = pubDate; }

    public void setCategory(String category) { this.category = category; }
}
