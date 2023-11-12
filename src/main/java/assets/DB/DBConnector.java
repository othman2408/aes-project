package assets.DB;

import java.sql.*;

public class DBConnector {

    String jdbcUrl = null;
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

    public static boolean handleRegister(String fname, String lname, String email, String pass, String date,
            String phone, String position) throws SQLException {

        try {
            String query = "INSERT INTO person (fname, lname, email, password, birthdate, phoneNO, position) VALUES ('" + fname + "', '"
                    + lname + "', '" + email + "', '" + pass + "', '" + date + "', '" + phone + "', '" + position + "')";

            DBConnector db = new DBConnector();
            db.stmt.executeUpdate(query);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

    }

    public static void main(String[] args) throws SQLException {

        // boolean found = searchForPerson(2100);
        // System.out.println(found);

//        boolean Register1 = handleRegister("othman", "alibrahim", "othman@gmail.com", "123456", "1999-12-12",
//                "0599999999", "student");



        System.out.println(handleRegister("ali", "murad", "ali@gmail.com", "336622", "2003-08-17", "12236262", "student"));

    }
}
