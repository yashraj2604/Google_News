import com.gargoylesoftware.htmlunit.WebClient;

import java.io.FileNotFoundException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class CumulateRssFeeds {

    public static void main(String[] args) throws FileNotFoundException, InterruptedException, SQLException, ClassNotFoundException {
        String path = "";
        Class.forName("com.mysql.jdbc.Driver");
        Connection con= DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/newsv6","root","!rnthakur123");

        WebClient webClient = new WebClient();
        webClient.getOptions().setCssEnabled(false);
        webClient.getOptions().setJavaScriptEnabled(false);

        while(true) {
            String fileNames[] = {"sports","business","politics","science","health","technology","entertainment"};
           // String fileNames[] = {"business"};
            //new RssExtractor()

            ExecutorService executor = Executors.newFixedThreadPool(Math.min(fileNames.length,1));
            for (String p : fileNames) {
                RssExtractor rssExtractor = new RssExtractor(path + p + ".txt", p, con, webClient);
                executor.execute(rssExtractor);
            }
            PopularFeed.addToDatabase(con,webClient);
            executor.shutdown();
            while (!executor.isTerminated()) {
            }
            System.out.println("Run ended");
//            Thread.sleep(300 * 1000 ); // 300 seconds
            System.out.println("Running Again");
            break;
        }
//        con.close();

    }
}
