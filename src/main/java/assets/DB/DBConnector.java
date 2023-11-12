package assets.DB;

import java.sql.*;


public class DBConnector {

    String jdbcUrl = null;
    //get the name from the dev.env
    String username = null;
    String password = null;

    Statement stmt;
     Connection conn;
     ResultSet rs;


    public DBConnector() throws SQLException {
        conn = DriverManager.getConnection(jdbcUrl, username, password);
        stmt = conn.createStatement();
    }

    public Connection getConnection() throws SQLException {
        return DriverManager.getConnection(jdbcUrl, username, password);
    }

    public void closeConnection() throws SQLException {
        conn.close();
    }

    public static boolean searchForPerson(int id) throws SQLException {
        String query = "SELECT * FROM person WHERE id = " + id;
        DBConnector db = new DBConnector();
        ResultSet rs = db.stmt.executeQuery(query);
        return rs.next();
    }


    public static void main(String[] args) throws SQLException {



//        boolean found = searchForPerson(2100);
//        System.out.println(found);







    }
}
