import java.sql.Connection;
import java.util.Vector;

// This class is used to continuously extract RSS feeds from
public class RssExtractor implements Runnable {

    private String path;
    private String category;
    private Connection con;

    public RssExtractor(String path,String category, Connection con) {
        this.category = category;
        this.path = path;
        this.con = con;
    }
    public void run() {
        System.out.println("RSS being extracted for " + category + " category.");
        // This will extract all the RSS url from text files.
        Vector<String> rssFeeds = FileExtractor.extractRSSFeeds(path);
        try {
            for (String rss : rssFeeds) {
                String[] tok = rss.split("/");
                System.out.println("Taking RSS feed from " + tok[2] + " in " + category + " category.");
                Vector<NewsArticle> newsArticles = XmlExtractor.execute(rss, category);
                for (int i = newsArticles.size() - 1; i >= 0; i--) {
                    // insert into database logic here;
                    if (!DatabaseHandler.doesUrlExistsInDatabase(category, newsArticles.get(i).getUrl(), con)) {
                        DatabaseHandler.insertToTable(category, newsArticles.get(i), con);
                    }
                }
            }
            System.out.println("Process completed for " + category + " category.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
