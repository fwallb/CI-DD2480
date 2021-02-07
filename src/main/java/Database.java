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
}
