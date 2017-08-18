import java.sql.*;
import java.util.*;

public class DatabaseHandler {


    public static void insertToTable(String category, NewsArticle newsArticle, Connection con) {

        try{

            String content =BoilerPipe.getContent(newsArticle.getUrl());

            if(!hasContent(content)){
                return ;
            }

            insertDetailsToDatabase(newsArticle, category, con);

            int article_id=getArticleID(newsArticle, category, con);

            insertContentToDatabase(article_id, content, category, con);

            HashMap<String,Double> hm= getHashMap(content);
            addWordToDatabase("#", category, con);
            for(Map.Entry<String,Double> e:hm.entrySet()){
                addWordToDatabase(e.getKey(), category, con);
            }

            new Cluster().addToCluster(article_id, hm, category, con, newsArticle.getPubDate().getTime());

        }catch(Exception e){
            e.printStackTrace();
        }
    }

   public static boolean doesUrlExistsInDatabase(String category, String url, Connection con) throws SQLException {
       final String queryCheck = "SELECT * FROM " + category +" WHERE url = ?";
       final PreparedStatement ps = con.prepareStatement(queryCheck);
       ps.setString(1, url);
       final ResultSet resultSet = ps.executeQuery();
       return resultSet.next();
   }

    private static void addWordToDatabase(String word, String category, Connection con )throws SQLException{
        PreparedStatement ps;
        ps=con.prepareStatement("SELECT * FROM " + category + "_words WHERE word='" + word + "'");
        ResultSet result=ps.executeQuery();
        if(result.next()){
            ps=con.prepareStatement("UPDATE " + category + "_words SET documentFrequency = documentFrequency + 1 WHERE word = '" + word + "'");
            ps.executeUpdate();
        }else{
            ps = con.prepareStatement("INSERT INTO " + category + "_words (word , documentFrequency) VALUES('" + word + "',1)");
            ps.executeUpdate();
        }
    }

    private static HashMap<String,Double> getHashMap(String content){
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

    private static boolean hasContent(String content){
        int words=0;
        int chars=0;
        int numerals=0;
        for(int i=0;i<content.length();i++){
            char c=content.charAt(i);
            if(c==' '||c=='\n'){
                words++;
            }else if(c>='a'&&c<='z'){
                chars++;
            }else if(c>='0'&&c<='9'){
                numerals++;
            }
        }
        return content.length() >= 200 && words >= 100 && numerals * 7 <= chars;
    }

    private static void insertDetailsToDatabase(NewsArticle newsArticle, String category, Connection con) throws SQLException{
        String query = " insert into " + category +" (title, url, pubdate, inserttime, category, articleScore, description)"
                + " values (?, ?, ?, ?, ?, ?,?)";
        PreparedStatement preparedStmt = con.prepareStatement(query);
        preparedStmt.setString (1, newsArticle.getTitle());
        preparedStmt.setString (2, newsArticle.getUrl());
        preparedStmt.setTimestamp   (3, newsArticle.getPubDate());
        preparedStmt.setTimestamp(4, new Timestamp(new java.util.Date().getTime()));
        preparedStmt.setString    (5, newsArticle.getCategory());
        preparedStmt.setLong(6, newsArticle.getPubDate().getTime());
        preparedStmt.setString(7, newsArticle.getDescription());
        preparedStmt.execute();
    }

    private static void insertContentToDatabase(int article_id, String content, String category, Connection con) throws SQLException{
        String query = "insert into " + category + "_content (id, content) values(?, ?)";
        PreparedStatement preparedStmt = con.prepareStatement(query);
        preparedStmt.setInt(1,article_id);
        preparedStmt.setString(2, content);
        preparedStmt.execute();

    }

    private static int getArticleID(NewsArticle newsArticle, String category, Connection con) throws SQLException {
        String query = "SELECT id FROM " + category + " where url = '" + newsArticle.getUrl() + "'";
        PreparedStatement preparedStmt = con.prepareStatement(query);
        ResultSet resultSet = preparedStmt.executeQuery();
        resultSet.next();
        return resultSet.getInt("id");
    }
}
