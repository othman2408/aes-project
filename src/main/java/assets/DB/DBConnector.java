package assets.DB;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * This class provides methods for connecting to a database, executing queries, and handling data.
 */
public class DBConnector {

    // Database connection parameters
    String jdbcUrl = null;
    String username = null;
    String password = null;

    // SQL objects for executing queries
    Statement stmt;
    Connection conn;
    ResultSet rs;

    /**
     * Constructor for DBConnector. Establishes a connection to the database and creates a Statement object.
     * @throws SQLException if a database access error occurs
     */
    public DBConnector() throws SQLException {
        conn = DriverManager.getConnection(jdbcUrl, username, password);
        stmt = conn.createStatement();
    }

    /**
     * Returns a Connection object for the database.
     * @return a Connection object for the database
     * @throws SQLException if a database access error occurs
     */
    public Connection getConnection() throws SQLException {
        return DriverManager.getConnection(jdbcUrl, username, password);
    }

    /**
     * Closes the database connection.
     * @throws SQLException if a database access error occurs
     */
    public void closeConnection() throws SQLException {
        conn.close();
    }

    /**
     * Searches for a person in the database by their ID.
     * @param id the ID of the person to search for
     * @return true if the person is found, false otherwise
     * @throws SQLException if a database access error occurs
     */
    public static boolean searchForPerson(int id) throws SQLException {
        String query = "SELECT * FROM person WHERE id = " + id;
        DBConnector db = new DBConnector();
        ResultSet rs = db.stmt.executeQuery(query);
        return rs.next();
    }

    /**
     * Handles the registration of a new person in the database.
     * @param fname the first name of the person
     * @param lname the last name of the person
     * @param email the email of the person
     * @param pass the password of the person
     * @param date the birthdate of the person
     * @param phone the phone number of the person
     * @param position the job position of the person
     * @return true if the registration is successful, false otherwise
     * @throws SQLException if a database access error occurs
     */
    public static boolean handleRegistration(String fname, String lname, String email, String pass, String date,
                                             String phone, String position) throws SQLException {

        try {
            String query = "INSERT INTO person (fname, lname, email, password, birthdate, phoneNO, position) VALUES ('"
                    + fname + "', '"
                    + lname + "', '" + email + "', '" + pass + "', '" + date + "', '" + phone + "', '" + position
                    + "')";

            DBConnector db = new DBConnector();
            db.stmt.executeUpdate(query);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

    }

    /**
     * Retrieves a list of all persons from the database.
     * @return a List of Person objects
     * @throws SQLException if a database access error occurs
     */
    public static List<Person> getPersons() throws SQLException {
        String query = "SELECT * FROM person";
        DBConnector db = new DBConnector();
        ResultSet rs = db.stmt.executeQuery(query);
        List<Person> persons = new ArrayList<>();
        while (rs.next()) {
            persons.add(new Person(rs.getString("fname"), rs.getString("lname"), rs.getString("email"),
                    rs.getString("password"), rs.getString("birthdate"), rs.getString("phoneNO"),
                    rs.getString("position")));
        }
        return persons;
    }

    /**
     * Main method for testing purposes.
     * @param args command line arguments
     * @throws SQLException if a database access error occurs
     */
    public static void main(String[] args) throws SQLException {

        // For testing purposes only.

        // boolean found = searchForPerson(2100);
        // System.out.println(found);

        // boolean Register1 = handleRegister("othman", "alibrahim", "othman@gmail.com",
        // "123456", "1999-12-12",
        // "0599999999", "student");

//        System.out.println(
//                handleRegistration("ali", "murad", "ali@gmail.com", "336622", "2003-08-17", "12236262", "student"));

        List<Person> persons = getPersons();
        for (Person person : persons) {
            System.out.println(person.getPassword());
        }
    }
}