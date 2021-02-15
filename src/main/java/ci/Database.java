package ci;

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

        // Create a new database history and a table builds if they do not exist
        this.stmt.executeUpdate("CREATE DATABASE IF NOT EXISTS history;");
        this.stmt.executeUpdate("USE history");
        this.stmt.executeUpdate("CREATE TABLE IF NOT EXISTS builds (commitHash VARCHAR(40) UNIQUE, commitDate INT(6), buildLog VARCHAR(6000));");

    } catch(SQLException exep) {
      exep.printStackTrace();
    }
  }

  /**
  * Extracts commit history, commit id and commit date from database
  * @return commitList an ArrayList consisting of string arrays of length 3 with commit id, commit date and build log
  */
  public ArrayList<String[]> getHistory() throws SQLException {
    ArrayList<String[]> commitList = new ArrayList<String[]>();

    // Construct and execute query
    String getHistoryQuery = "SELECT commitHash, commitDate, buildLog FROM builds";
    ResultSet history = this.stmt.executeQuery(getHistoryQuery);

    // Store commit id, commit date and build log from the resultset in an
    // array and add each array to an ArrayList
    while(history.next()) {
      String[] commit = new String[3];
      commit[0] = history.getString("commitHash");
      commit[1] = String.valueOf(history.getInt("commitDate"));
      commit[2] = history.getString("buildLog");
      commitList.add(commit);
    }
    return commitList;
  }

  /**
  * Extracts build log from database
  * @param commitId the commit id
  * @return buildLog the build log
  */
  public String getBuildLog(String commitId) throws SQLException {
    String result = "No build";

    // Construct and execute query
    String getBuildLogQuery = "SELECT buildLog FROM builds WHERE commitHash = " + "'" + commitId + "'";
    ResultSet buildLog = this.stmt.executeQuery(getBuildLogQuery);

    // Extract buildlog from the resultset
    while(buildLog.next()) {
      result = buildLog.getString("buildLog");
    }
    return result;
  }

  /**
  * Inserts commit id, commit date and build log into database
  * @param commitId the commit id
  * @param commitDate the commit date
  * @param buildLog the build log
  */
  public void insertIntoDatabase(String commitId, int commitDate, String buildLog) throws SQLException {
    // Create prepared statement
    String insertData = "INSERT INTO builds VALUES (?, ?, ?)";
    PreparedStatement prepstmt = conn.prepareStatement(insertData);
    prepstmt.setString(1, commitId);
    prepstmt.setInt(2, commitDate);
    prepstmt.setString(3, buildLog);

    //Execute query
    prepstmt.execute();
  }
}
