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

## Notification
Notification via email using gmail as the SMTP server. The notification email is sent to the author of a commit.
This is found in ContinuousIntegrationServer.java. The unit tests are found in ContinuousIntegrationServerTest.java.
