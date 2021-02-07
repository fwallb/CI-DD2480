import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;

public class Database {

  private Statement stmt;
  private Connection conn;
  private final String USER = "root";
  private final String PWD = "";

  public Database() {
    try {
      // Create a connection with a database
      conn = DriverManager.getConnection(
        "jdbc:mysql://localhost:3306/history", USER, PWD);

      // Create statement object
      this.stmt = conn.createStatement();
    } catch(SQLException exep) {
      exep.printStackTrace();
    }
  }

  // Method to insert a row into database
  public void insertIntoDatabase(String commitHash, int commitDate, String buildLog) throws SQLException {
    String insertData = "INSERT INTO builds (commitHash, commitDate, buildLog) VALUES ('" + commitHash + "', '" + commitDate + "', '" + buildLog + "' )";
    stmt.executeUpdate(insertData);
  }

}
