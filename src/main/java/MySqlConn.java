import java.sql.*;

public class MySqlConn {
    public static void main(String args[]) throws ClassNotFoundException,SQLException{
        String databaseName = "newsv2";

        String newCategories[]={"sports","business","politics","science","health","technology","entertainment"};
        for (int i = 0; i < newCategories.length; i++) {
            new TableCreator(databaseName, newCategories[i]);
        }

    }

}
