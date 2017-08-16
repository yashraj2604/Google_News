import javax.print.DocFlavor;
import javax.swing.text.html.HTMLDocument;
import java.io.FileNotFoundException;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.*;
import javax.xml.transform.*;
import java.io.BufferedReader;
import java.io.FileReader;

/**
 * Created by yash.raj on 10/08/17.
 */
// You need to change database name, table name, password
public class DatabaseHandler {

//    public Connection connecttodatabase() throws SQLException, ClassNotFoundException {
//
//        return con;
//    }
//
//    public void connectionclose(Connection con) throws SQLException {
//        con.close();
//    }

    public void insertToTable(String category, NewsArticle newsArticle, Connection con) {
        try{

            java.sql.Timestamp date = new java.sql.Timestamp(new java.util.Date().getTime());

            String query = " insert into " + category +" (title, url, pubdate, inserttime, category, articleScore)"
                    + " values (?, ?, ?, ?, ?, ?)";
            SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            PreparedStatement preparedStmt = con.prepareStatement(query);
            preparedStmt.setString (1, newsArticle.getTitle());
            preparedStmt.setString (2, newsArticle.getUrl());
            preparedStmt.setString   (3, newsArticle.getPubDate());
            preparedStmt.setTimestamp(4, date);
            preparedStmt.setString    (5, newsArticle.getCategory());
            preparedStmt.setLong(6,sdf.parse(newsArticle.getPubDate()).getTime());
            preparedStmt.execute();
            int article_id = -1;
            query = "SELECT id FROM "+category+" where url = '"+newsArticle.getUrl()+"'";
            preparedStmt=con.prepareStatement(query);
            ResultSet resultSet = preparedStmt.executeQuery();
            if(resultSet.next()){
                article_id = resultSet.getInt("id");
            }
            String content =BoilerPipe.getContent(newsArticle.getUrl());
            query = "insert into "+category+"_content (id, content) values(?, ?)";
            preparedStmt = con.prepareStatement(query);
            preparedStmt.setInt(1,article_id);
            preparedStmt.setString(2, content);
            preparedStmt.execute();
            HashMap<String,Double> hm=getHashmap(content);

            add(category,"#",con);
            for(Map.Entry<String,Double> e:hm.entrySet()){
                add(category,e.getKey(),con);
            }
            new Cluster().addToCluster(article_id, hm, category, con, sdf.parse(newsArticle.getPubDate()).getTime());

        }catch(Exception e){ e.printStackTrace();}
    }

   public int checkIfExists(Connection con, String category, String val) throws SQLException {
      /* Statement stmt = con.createStatement();
       ResultSet rs = stmt.executeQuery("SELECT * from latest  WHERE url = \"jk\"");

       while(rs.next()) {
           System.out.println(rs.getString(3));
       }
       */

       final String queryCheck = "SELECT * from " + category +" WHERE url = ?";
       final PreparedStatement ps = con.prepareStatement(queryCheck);
       ps.setString(1, val);
       final ResultSet resultSet = ps.executeQuery();
       if(resultSet.next()) {
           return 1;
       }
       return 0;
   }

    private void add(String category, String word, Connection con )throws SQLException{
        PreparedStatement ps;
        ps=con.prepareStatement("select * from "+category+"_words where word='"+word+"'");
        ResultSet result=ps.executeQuery();
        if(result.next()){
            ps=con.prepareStatement("update "+category+"_words set documentFrequency = documentFrequency + 1 where word = '"+word+"'");
            ps.executeUpdate();
        }else{
            try {
                ps = con.prepareStatement("INSERT INTO "+category+"_words (word , documentFrequency) values('" + word + "',1)");
                ps.executeUpdate();
            }catch (Exception e){
                System.out.println("Word too big "+word);
            }
        }
    }
    private HashMap<String,Double> getHashmap(String content){
        HashMap<String,Double> hm=new HashMap<>();
        Scanner sc=new Scanner(content);
        while(sc.hasNext()){
            String s=sc.next();
            if(s.length()>31){
                continue;
            }
            if(hm.get(s)==null){

                hm.put(s,1.0);
            }else{
                hm.put(s,hm.get(s)+1);
            }
        }
        sc.close();
        return hm;
    }
     /*
    public void inserttolastest(Map<String,String> prevstore,String category, Connection con) {
        try {
            Iterator it = prevstore.entrySet().iterator();
            String query = " insert into " + category +" (rsssource, url, inserttime)"
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

}
