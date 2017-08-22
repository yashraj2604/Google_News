import java.sql.*;

public class MySqlConn {
    public static void main(String args[]) throws ClassNotFoundException,SQLException{
        String databaseName = "newsv6";
        Class.forName("com.mysql.jdbc.Driver");
        Connection con= DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/"+databaseName,"root","!rnthakur123");
        String newCategories[]={"sports","business","politics","science","health","technology","entertainment"};
        for (int i = 0; i < newCategories.length; i++) {
            new TableCreator(databaseName, newCategories[i],con);
        }

        PreparedStatement ps;
        String query;
        query="use "+databaseName;
        ps=con.prepareStatement(query);
        ps.execute();
        query="CREATE TABLE "+"popular"+"(\n" +
                "id int NOT NULL AUTO_INCREMENT primary key, " +
                "title nvarchar(1023), " +
                "url nvarchar(1023), "+
                "imageUrl nvarchar(1023), "+
                "pubdate nvarchar(255), " +
                "inserttime datetime, " +
                "category nvarchar(255), " +
                "description nvarchar(1023), " +
                "articleScore long " +
                ")";
        ps=con.prepareStatement(query);
        ps.execute();
        con.close();
    }

}
