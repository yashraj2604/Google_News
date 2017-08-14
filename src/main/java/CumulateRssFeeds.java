import java.io.FileNotFoundException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.Map;

/**
 * Created by yash.raj on 11/08/17.
 */
public class CumulateRssFeeds {

    public static void main(String[] args) throws FileNotFoundException, InterruptedException, SQLException, ClassNotFoundException {
        String path = "/Users/yash.raj/Documents/Googlenewsdata/";

        String businesspath = path + "business.txt";
        String sportspath = path + "sports.txt";
        String politicpath = path + "politics.txt";

        Connection con = new DatabaseHandler().connecttodatabase();

        //Map<String,String> prevstore = new DatabaseHandler().retreivefromlatest(con);
       // Map<String,String> prevstore = new HashMap<String, String>();


        while(true) {
        //new RssExtractor(path + "business" + ".txt", "business", "!rnthakur123", con).run();

            ExecutorService executor = Executors.newFixedThreadPool(5);
            String paths[] = {"sports", "business", "politics"};


            //new RssExtractor()
            for (String p : paths) {

                RssExtractor rssExtractor = new RssExtractor(path + p + ".txt", p, "!rnthakur123", con);

                executor.execute(rssExtractor);
            }
            executor.shutdown();

           // System.out.println(prevstore.size());

            while (!executor.isTerminated()) {
            }

         //   new DatabaseHandler().createtablelatest(con);
          //  new DatabaseHandler().inserttolastest(prevstore,"latest",con);

            System.out.println("yo");
            Thread.sleep(10 * 1000 ); // 100 seconds
            System.out.println("helloagain");
        }

        //new DatabaseHandler().connectionclose(con);

    }
}
