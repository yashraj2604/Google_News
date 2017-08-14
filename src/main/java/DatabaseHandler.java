import javax.print.DocFlavor;
import javax.swing.text.html.HTMLDocument;
import java.io.FileNotFoundException;
import java.sql.*;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Iterator;
import javax.xml.transform.*;
import java.io.BufferedReader;
import java.io.FileReader;

/**
 * Created by yash.raj on 10/08/17.
 */
// You need to change database name, table name, password
public class DatabaseHandler {

    public Connection connecttodatabase() throws SQLException, ClassNotFoundException {
        Class.forName("com.mysql.jdbc.Driver");
        Connection con= DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/googlenews","root","!rnthakur123");

        return con;
    }

    public void connectionclose(Connection con) throws SQLException {
        con.close();
    }

    public void insertotable(String tablename, String database, String password, NewsArticle newsArticle, Connection con) {
        try{

            java.sql.Timestamp date = new java.sql.Timestamp(new java.util.Date().getTime());
            String query = " insert into " + tablename +" (title, url, pubdate, inserttime, category)"
                    + " values (?, ?, ?, ?, ?)";

            PreparedStatement preparedStmt = con.prepareStatement(query);
            preparedStmt.setString (1, newsArticle.getTitle());
            preparedStmt.setString (2, newsArticle.getUrl());
            preparedStmt.setString   (3, newsArticle.getPubDate());
            preparedStmt.setTimestamp(4, date);
            preparedStmt.setString    (5, newsArticle.getCategory());
            preparedStmt.execute();
        }catch(Exception e){ System.out.println(e);}
    }
    /*
    public void inserttolastest(Map<String,String> prevstore,String tablename, Connection con) {
        try {
            Iterator it = prevstore.entrySet().iterator();
            String query = " insert into " + tablename +" (rsssource, url, inserttime)"
                    + " values (?, ?, ?)";

            while(it.hasNext()) {
                Map.Entry pair = (Map.Entry)it.next();
                java.sql.Timestamp date = new java.sql.Timestamp(new java.util.Date().getTime());

                PreparedStatement preparedStmt = con.prepareStatement(query);
               // System.out.println((String)pair.getKey() + " + " + (String)pair.getValue());

                preparedStmt.setString(1,(String)pair.getKey());
                preparedStmt.setString(2,(String)pair.getValue());
                preparedStmt.setTimestamp(3,date);
                preparedStmt.execute();

              //  it.remove(); // avoids a ConcurrentModificationException
            }

        } catch(Exception e) {
            e.printStackTrace();
        }
    }
*/
    /*
    public Map<String,String> retreivefromlatest(Connection con) throws SQLException {
        Map<String,String > prev = new HashMap<String, String>();

        Statement stmt = con.createStatement();

        ResultSet rs = stmt.executeQuery("Select rsssource,url from latest");

        while(rs.next()) {
            prev.put(rs.getString(1),rs.getString(2));
        }

        return prev;
    }
    // path here refers to the sql scripts which needs to be run
   /* public void createtable(String path,Connection con) throws SQLException, FileNotFoundException {
        ScriptRunner runner = new ScriptRunner(con, false, false);
        runner.runScript(new BufferedReader(new FileReader("test.sql")));
    }
    */
/*
   public void createtablelatest(Connection con) throws SQLException {
       Statement stmt = con.createStatement();
       String query = "Drop table latest";
       stmt.executeUpdate(query);
       query = "create table latest (\n" +
               "id int NOT NULL AUTO_INCREMENT primary key,\n" +
               "rsssource varchar(1023),\n" +
               "url varchar(1023),\n" +
               "inserttime datetime\n" +
               ");";

        stmt.executeUpdate(query);
   }
*/
   public int checkifexists(Connection con, String tablename, String val) throws SQLException {
      /* Statement stmt = con.createStatement();
       ResultSet rs = stmt.executeQuery("SELECT * from latest  WHERE url = \"jk\"");

       while(rs.next()) {
           System.out.println(rs.getString(3));
       }
       */

       final String queryCheck = "SELECT * from " + tablename +" WHERE url = ?";
       final PreparedStatement ps = con.prepareStatement(queryCheck);
       ps.setString(1, val);
       final ResultSet resultSet = ps.executeQuery();
       if(resultSet.next()) {
           return 1;
       }
       return 0;
   }
}
