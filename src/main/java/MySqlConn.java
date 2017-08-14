/**
 * Created by yash.raj on 10/08/17.
 */
import java.sql.*;

public class MySqlConn {
    public static void main(String args[]){
        try{
            Class.forName("com.mysql.jdbc.Driver");
            Connection con=DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/googlenews","root","!rnthakur123");
//here sonoo is database name, root is username and password
            Statement stmt=con.createStatement();
          //  ResultSet rs=stmt.executeQuery("insert into article values ('ruchir')");
          /*  while(rs.next())
               // System.out.println("hee");
                System.out.println(rs.getString(1));
                */
          String var = "yash";

            int result=stmt.executeUpdate("insert into article values ('" + var + "')");
            System.out.println(result+" records affected");
            con.close();
        }catch(Exception e){ System.out.println(e);}
    }

}
