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

Database:

## build list URL

http://344bb6ddffb9.ngrok.io/CI-DD2480/

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
