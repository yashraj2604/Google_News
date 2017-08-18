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
                "jdbc:mysql://localhost:3306/newsv2","root","qwerty123");

        while(true) {
            ExecutorService executor = Executors.newFixedThreadPool(1);
            String fileNames[] = {"sports","business","politics","science","health","technology","entertainment"};
            //new RssExtractor()
            for (String p : fileNames) {
                RssExtractor rssExtractor = new RssExtractor(path + p + ".txt", p, con);
                executor.execute(rssExtractor);
            }
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
