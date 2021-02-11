# DD2480 - Continuous Integration Server

## How To Use

Server:
```
git clone git@github.com:fwallb/CI-DD2480.git
cd CI-DD2480
./mvnw package
java -jar target/CI-DD2480-0.1.0.jar
```

Testing:


The database methods are tested by inserting data into the database and thereafter extracting it. Tests assert that the correct data was inserted and extracted, also that the number of rows in the database increased. These tests can be found in DatabaseTests.java.

Database:
A MySQL database is used to store commits between server runs. When the server runs, MySQL must run in the background. Script to create the database, createDatabase.sql, can be found in main/src/sql. Methods to insert and extract data from the database can be found in the database class. Tests of the methods can be found in the database-test file.

## Notification
Notification via email using gmail as the SMTP server. The notification email is sent to the author of a commit.
This is found in ContinuousIntegrationServer.java. The unit tests are found in ContinuousIntegrationServerTest.java.

## Contributions

- Fredrik Norlin
  - Created website
  - Connected database and website
  - Code review
  - Documentation

- Frida Wallberg
  - Created database
  - Implemented database class and corresponding tests
  - Implemented addToDatabase method in server class
  - Code review
  - Documentation

- George Bassilious
  - Implemented tests to check compilation and testing
  - Code review
  - Documentation

- Michaela Sahlgren
  - Implemented method to get JSON object from git
  - Implemented compilation of code and testing
  - Implemented testing of code and testing
  - Code review
  - Documentation

- Sara Damne
  - Implemented send email method and testing
  - Code review
  - Documentation
