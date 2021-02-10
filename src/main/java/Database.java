import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;

public class Database {

  Statement stmt;
  Connection conn;
  private final String USER = "root";
  private final String PWD = "";

  public Database() {
   try {
     // Create a connection with a database
     this.conn = DriverManager.getConnection(
       "jdbc:mysql://localhost:3306/history", USER, PWD);

     // Create statement object
     this.stmt = this.conn.createStatement();

     // Create a new table history if it not exists
     this.stmt.executeUpdate("CREATE TABLE IF NOT EXISTS builds;");
   } catch(SQLException exep) {
     exep.printStackTrace();
   }
  }

  // Method to insert a row into database
  public void insertIntoDatabase(String commitHash, int commitDate, String buildLog) throws SQLException {
    String insertData = "INSERT INTO builds (commitHash, commitDate, buildLog) VALUES ('" + commitHash + "', '" + commitDate + "', '" + buildLog + "' )";
    this.stmt.executeUpdate(insertData);
  }

}
