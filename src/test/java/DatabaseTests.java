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
