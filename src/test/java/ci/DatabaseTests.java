package ci;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import java.sql.*;
import org.dbunit.*;
import java.util.ArrayList;

import static org.junit.Assert.assertEquals;

class DatabaseTests {
  Database db;

  @BeforeEach
  public void setUp() throws Exception {
    db = new Database();
  }

  @Test
  @DisplayName("Check commitHash and commitDate extraction from database")
  void extractHistoryFromDatabaseCorrect() throws SQLException {
    String commitHash = "0123456789012345678901234567890123456789";
    int commitDate = 210207;
    String buildLog = "Test to check if data is extracted correctly";

    // Insert data to be extracted
    String insertData = "INSERT INTO builds (commitHash, commitDate, buildLog) VALUES ('" + commitHash + "', '" + commitDate + "', '" + buildLog + "' );";
    db.stmt.executeUpdate(insertData);

    // Extract data
    ArrayList<String[]> commitList = db.getHistory();

    // Assert that the data is as expected
    assertEquals(commitHash, commitList.get(commitList.size()-1)[0]);
    assertEquals(commitDate, Integer.parseInt(commitList.get(commitList.size()-1)[1]));

    // Remove from table
    String deleteData = "DELETE FROM builds WHERE commitHash = '" + commitHash + "';";
    db.stmt.executeUpdate(deleteData);
  }

  @Test
  @DisplayName("Check buildLog extraction from database")
  void extractBuildLogFromDatabaseCorrect() throws SQLException {
    String commitHash = "0123456789012345678901234567890123456789";
    int commitDate = 123456;
    String expectedBuildLog = "Test to check if buildLog is extracted correctly";

    // Insert data to be extracted
    String insertData = "INSERT INTO builds (commitHash, commitDate, buildLog) VALUES ('" + commitHash + "', '" + commitDate + "', '" + expectedBuildLog + "' );";
    db.stmt.executeUpdate(insertData);

    // Extract data
    String actualBuildLog = db.getBuildLog(commitHash);

    // Assert that the data is as expected
    assertEquals(expectedBuildLog, actualBuildLog);

    // Remove from table
    String deleteData = "DELETE FROM builds WHERE commitHash = '" + commitHash + "';";
    db.stmt.executeUpdate(deleteData);
  }

  @DisplayName("Check insertion into database")
  void insertIntoDatabaseCorrect() throws SQLException {
    String commitHash = "0123456789012345678901234567890123456789";
    int commitDate = 210209;
    String buildLog = "Test to check if data is inseted correctly";
    String[] commitList = new String[3];

    // Check number of rows before insertion
    ResultSet result = db.stmt.executeQuery("SELECT COUNT(*) FROM builds;");
    result.next();
    int rowsBefore = result.getInt(1);

    // Insert data
    db.insertIntoDatabase(commitHash, commitDate, buildLog);

    // Check number of rows after insertion
    result = db.stmt.executeQuery("SELECT COUNT(*) FROM builds;");
    result.next();
    int rowsAfter = result.getInt(1);

    // Extract the inseted data
    String getRow = "SELECT commitHash, commitDate, buildLog FROM builds WHERE commitHash = '" + commitHash + "';";
    ResultSet row = db.stmt.executeQuery(getRow);

    while(row.next()) {
      commitList[0] = row.getString("commitHash");
      commitList[1] = String.valueOf(row.getInt("commitDate"));
      commitList[2] = row.getString("buildLog");
    }

    // Assert that inserted data is equal to extracted data
    assertEquals(commitHash, commitList[0]);
    assertEquals(commitDate, Integer.parseInt(commitList[1]));
    assertEquals(buildLog, commitList[2]);

    // and that a row has been added to the table
    assertEquals(rowsBefore+1, rowsAfter);

    // Remove from table
    String deleteData = "DELETE FROM builds WHERE commitHash = '" + commitHash + "';";
    db.stmt.executeUpdate(deleteData);
  }
}
