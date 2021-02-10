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
      conn = DriverManager.getConnection(
        "jdbc:mysql://localhost:3306/history", USER, PWD);

        // Create statement object
        this.stmt = conn.createStatement();

        // Create a new table history if it not exists
        this.stmt.executeUpdate("CREATE TABLE IF NOT EXISTS builds;");
    } catch(SQLException exep) {
      exep.printStackTrace();
    }
  }

  // Method to extract commit history, commit hash and commit date
  public ArrayList<String[]> getHistory() throws SQLException {
    ArrayList<String[]> commitList = new ArrayList<String[]>();
    String[] commit = new String[2];

    String getHistoryQuery = "SELECT commitHash, commitDate FROM builds";
    ResultSet history = this.stmt.executeQuery(getHistoryQuery);

    while(history.next()) {
      commit[0] = history.getString("commitHash");
      commit[1] = String.valueOf(history.getInt("commitDate"));
      commitList.add(commit);
    }
    return commitList;
  }

  // Method to extract buildLog based on a commitHash
  public String getBuildLog(String commitHash) throws SQLException {
    String result = "No build";

    String getBuildLogQuery = "SELECT buildLog FROM builds WHERE commitHash = " + "'" + commitHash + "'";
    ResultSet buildLog = this.stmt.executeQuery(getBuildLogQuery);

    while(buildLog.next()) {
      result = buildLog.getString("buildLog");
    }
    return result;
  }

  // Method to insert a row into database
  public void insertIntoDatabase(String commitHash, int commitDate, String buildLog) throws SQLException {
    String insertData = "INSERT INTO builds (commitHash, commitDate, buildLog) VALUES ('" + commitHash + "', '" + commitDate + "', '" + buildLog + "' )";
    this.stmt.executeUpdate(insertData);
  }
}
