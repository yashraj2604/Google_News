import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class TableCreator {
    public TableCreator(String databaseName, String categoryName, Connection con)throws ClassNotFoundException,SQLException{
        PreparedStatement ps;
        String query;
        query="use "+databaseName;
        ps=con.prepareStatement(query);
        ps.execute();
        query="CREATE TABLE "+categoryName+"(\n" +
                "id int NOT NULL AUTO_INCREMENT primary key, " +
                "title nvarchar(1023), " +
                "url nvarchar(1023), "+
                "imageUrl nvarchar(1023), "+
                "pubdate nvarchar(255), " +
                "inserttime datetime, " +
                "category varchar(255), " +
                "description nvarchar(1023), " +
                "articleScore long " +
                ")";
        ps=con.prepareStatement(query);
        ps.execute();
        query="CREATE TABLE "+categoryName +
                "_content ("+
                "id INT,"+
                "content TEXT" +
                ")";
        ps=con.prepareStatement(query);
        ps.execute();
        query="CREATE TABLE "+categoryName +
                "_words ("+
                "word VARCHAR(31),"+
                "documentFrequency INT" +
                ")";
        ps=con.prepareStatement(query);
        ps.execute();
        query="CREATE TABLE "+categoryName +
                "_cluster ("+
                "id INT,"+
                "articleScore LONG," +
                "clusterID INT"+
                ")";
        ps=con.prepareStatement(query);
        ps.execute();
        query="CREATE TABLE "+categoryName +
                "_cluster_score ("+
                "clusterID INT auto_increment primary key,"+
                "clusterScore LONG," +
                "numberOfArticles INT" +
                ")";
        ps=con.prepareStatement(query);
        ps.execute();
    }
}
