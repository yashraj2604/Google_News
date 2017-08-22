import com.gargoylesoftware.htmlunit.WebClient;

import java.sql.Connection;
import java.util.Vector;

public class PopularFeed {
    public static void addToDatabase(Connection con, WebClient webClient){
        String category = "popular";
        Vector<String> rssFeeds = FileExtractor.extractRSSFeeds("popular.txt");
        try {
            for (String rss : rssFeeds) {
                String[] tok = rss.split("/");
                System.out.println("Taking RSS feed from " + tok[2] + " in " + category + " category.");
                Vector<NewsArticle> newsArticles = XmlExtractor.execute(rss, category);
                for (int i = newsArticles.size() - 1; i >= 0; i--) {
                    // insert into database logic here;
                    if (!DatabaseHandler.doesUrlExistsInDatabase(category, newsArticles.get(i).getUrl(), con)) {
                        DatabaseHandler.insertDetailsToDatabase(newsArticles.get(i), category, con, webClient);
                    }
                }
            }
            System.out.println("Process completed for " + category + " category.");
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
