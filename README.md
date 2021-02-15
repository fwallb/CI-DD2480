# DD2480 - Continuous Integration Server

## How To Use
Requirements:
- MySQL must be installed
- Java 8

Installation:
- Clone the repo and setup the database:
```
git clone git@github.com:fwallb/CI-DD2480.git
cd CI-DD2480
mysql -u root
# Now run this from the mysql prompt:
# source src/main/sql/createDatabase.sql
# Then, logout of mysql with Ctrl-D
```

- Create the java package
```
./mvnw package
java -jar target/CI-DD2480-0.1.0.jar
```

Running:
- When the server runs, MySQL must run in the background.
- Execute the java jar file to run the server:
```
java -jar target/CI-DD2480-0.1.0.jar
```

## Testing:
- Run maven tests:
```
./mvnw test
```

Testing has been conducted using Junit tests. The unit tests cover different cases using commitIds that have known results.
An example of a test case is to see whether the commit compiles and passes all tests. This uses a known commitId to ensure that the test case always should assertTrue. If it does not the unit test fails and therefore the commit has changed the compilation build.
These tests are found in ContinuousIntegrationServerTest.java.

The database methods are tested by inserting data into the database and thereafter extracting it. Tests assert that the correct data was inserted and extracted, also that the number of rows in the database increased. These tests can be found in DatabaseTests.java.

## build list URL
http://344bb6ddffb9.ngrok.io/CI-DD2480/

## Notification
Notification via email using gmail as the SMTP server. The notification email is sent to the author of a commit.
This is found in ContinuousIntegrationServer.java. The unit tests are found in ContinuousIntegrationServerTest.java.

## Database
A MySQL database is used to store commits between server runs. When the server runs, MySQL must run in the background. Script to create the database, createDatabase.sql, can be found in main/src/sql. Methods to insert and extract data from the database can be found in the database class. Tests of the methods can be found in the database-test file.

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
