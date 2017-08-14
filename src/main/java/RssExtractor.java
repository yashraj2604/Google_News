import java.io.DataOutputStream;
import java.io.FileNotFoundException;
import java.sql.Connection;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

/**
 * Created by yash.raj on 09/08/17.
 */
// This class is used to continously extract rss feeds from
public class RssExtractor implements Runnable {

    String path;
    String tablename;
    String pass;
    String category;
    Connection con;
   // Map<String,String> prevstore;

    public RssExtractor(String path,String tablename,String pass,Connection con) {
        this.category = tablename;
        this.pass = pass;
        this.path = path;
        this.tablename = tablename;
        this.con = con;
        //this.prevstore = prevstore;
    }
    public void run() {
        String database = "googlenews";
            System.out.println("rss being extracted from category: " + category);
            FileExtractor fl = new FileExtractor();

            // This will extract all the rss url from txt file
            Vector<String> rss = fl.run(path);
        //System.out.println(category + " " + rss.size());
            XmlExtractor xmlExtractor = new XmlExtractor();

            DatabaseHandler databaseHandler = new DatabaseHandler();

            try {
                while (true) {
                    //  System.out.println("Process started again");
                    for(int j=0;j<rss.size();j++) {
                        String[] tok = rss.get(j).split("/");
                        System.out.println("Source being used is : " +tok[2] );
                        Vector<NewsArticle> newsArticle = xmlExtractor.execute(rss.get(j),category);

                        for(int i=0;i<newsArticle.size();i++) {
                            // insert into database logic here;
                            int check = databaseHandler.checkifexists(con,tablename,newsArticle.get(i).getUrl());

                            if (check==0) {
                                new DatabaseHandler().insertotable(tablename, database, pass, newsArticle.get(i),con);
                            } else {
                                break;
                            }
                        }

                    }
                    System.out.println("Process completed for : " + category);
                    break;
                }

            } catch (Exception e) {
                e.printStackTrace();
            }

        }

}
