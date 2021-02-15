package ci;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.ServletException;

import java.io.IOException;
import java.io.*;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;
import org.eclipse.jetty.server.handler.HandlerCollection;
import org.eclipse.jetty.webapp.WebAppContext;

import java.util.stream.Collectors;
import java.util.Date;
import java.text.*;
import org.json.*;
import java.nio.file.*;
import java.io.File;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.sql.*;

import java.util.*;
import javax.mail.*;
import javax.mail.internet.*;
import javax.activation.*;

/**
 Skeleton of a ContinuousIntegrationServer which acts as webhook
 See the Jetty documentation for API documentation of those classes.
*/
public class ContinuousIntegrationServer extends AbstractHandler
{
    /**
    * Clones the specified repo, checks out the specified commit, and runs
    * tests using maven.
    * @param {String} repoUrl the URL of the repo that will be cloned
    * @param {String} commitId the ID of the commit to checkout for that repo
    * @return The output from running maven, if all three steps completed
    * without an Exception occurring. Otherwise, an empty string.
    */
    public static String cloneAndTest(String repoUrl, String commitId) {
        try {
            Path tempDir = Files.createTempDirectory("repo");
            String pathToTempDir = tempDir.toAbsolutePath().toString();
            System.out.println("path to temp dir: " + pathToTempDir);

            System.out.println("Running git clone.");
            Process process = Runtime.getRuntime().exec("git clone " + repoUrl + " " + pathToTempDir);
            process.waitFor();

            System.out.println("Running git checkout.");
            String[] dummyEnvs = new String[0];
            process = Runtime.getRuntime().exec("git checkout " + commitId, dummyEnvs, new File(pathToTempDir));
            process.waitFor();

            System.out.println("Running maven test.");
            process = Runtime.getRuntime().exec("./mvnw test", dummyEnvs, new File(pathToTempDir));
            process.waitFor();

            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String outputFromTests = "";
            String line = "";
            while ((line = reader.readLine()) != null) {
                outputFromTests = outputFromTests + line;
            }

            System.out.println("Ran maven tests.");

            return outputFromTests;
        } catch (Exception e) {
            System.out.println("Exception happened.");
            return "";
        }
    }

    /**
    * Processes the webhook commit specified in the given JSON object.
    * @param {JSONObject} requestBodyJson The body of the request, as a JSON object
    * @return The output from cloning and testing the repo and commit specified
    * in that JSON object, if a commit is specified, otherwise an empty string.
    */
    public static String processWebhookCommit(JSONObject requestBodyJson) {
        if (requestBodyJson.has("head_commit")) {
            String headCommitId = requestBodyJson.getJSONObject("head_commit").getString("id");
            String repoUrl = requestBodyJson.getJSONObject("repository").getString("clone_url");
            System.out.println("extracted commit and repo: " + headCommitId + " " + repoUrl);

            return cloneAndTest(repoUrl, headCommitId);
        }

        return "";
    }

    /**
    * Sends email to the author of a commit. Status depends on the results from processWebhookCommit().
    * @param {JSONObject} requestBodyJson the JSONObject for this commit
    * @param {String} webhookCommitResult contains output from the tests.
    * @return True if an email was sent successfully, false otherwise
    */
    public static boolean sendGmail(JSONObject requestBodyJson, String webhookCommitResult) {
            //check if JSONObject exist
            if (!(requestBodyJson.has("head_commit"))) {
              System.out.println("no head_commit");
              return false;
            }

            //Change here to set sending email!
            final String username = "group20cidd2480@gmail.com";
            final String password = "Password1234!";

            //set properties for server
            Properties prop = new Properties();
            prop.put("mail.smtp.host", "smtp.gmail.com");
            prop.put("mail.smtp.port", "587");
            prop.put("mail.smtp.auth", "true");
            prop.put("mail.smtp.starttls.enable", "true"); //TLS

            //add authentication
            Session session = Session.getInstance(prop,
                    new javax.mail.Authenticator() {
                        protected PasswordAuthentication getPasswordAuthentication() {
                            return new PasswordAuthentication(username, password);
                        }
                    });

            //Build the email
            try {
                String headCommitId = requestBodyJson.getJSONObject("head_commit").getString("id");
                String repoUrl = requestBodyJson.getJSONObject("repository").getString("clone_url");
                String mailContent = cloneAndTest(repoUrl, headCommitId);

                String recipient = requestBodyJson.getJSONObject("head_commit").getJSONObject("committer").getString("email");
                Message message = new MimeMessage(session);

                //Change here to change sender email!
                message.setFrom(new InternetAddress("group20cidd2480@gmail.com"));
                message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipient));
                message.setSubject("Status update");

                if(webhookCommitResult.contains("BUILD SUCCESS")){
                  message.setText("Your latest commit: SUCCESS \n" + mailContent);
                }else if(webhookCommitResult.contains("BUILD FAIL") || webhookCommitResult.contains("COMPILAITON ERROR")){
                  message.setText("Your latest commit: FAILURE \n" + mailContent);
                }else{
                  message.setText("Something went wrong: No status found.");
                }
                //Send email
                Transport.send(message);
                System.out.println("Message sent successfully");
                return true;

            } catch (MessagingException e) {
                e.printStackTrace();
            }
            return false;
        }

    /**
    * Adds commit id, commit date and build log to database
    * @param {String} commit id of the commit
    * @param {String} build log of the commit
    */
    public void addToDatabase(String commitId, String buildLog) {
      // Get today's date
      DateFormat dateFormat = new SimpleDateFormat("YYMMdd");
      Date dateDate = new Date();
      String strDate = dateFormat.format(dateDate);
      int date = Integer.parseInt(strDate);

        // Insert data into database
        try {
          Database db = new Database();
          db.insertIntoDatabase(commitId, date, buildLog);
        } catch(SQLException exep) {
          exep.printStackTrace();
        }
    }

    /**
     * Creates JSONObject and calls methods to start proccessing the webhook commit.
     * Calls method to send notification email. 
     * Calls method to add commit results to database.
     *
     * @param {String} target
     * @param {Request} baseRequest
     * @param {HttpServletRequest} request
     * @param {HttpServletResponse} response
     * @throws IOException
     * @throws ServletException
     */
    public void handle(String target,
                       Request baseRequest,
                       HttpServletRequest request,
                       HttpServletResponse response)
        throws IOException, ServletException
    {
        response.setContentType("text/html;charset=utf-8");
        response.setStatus(HttpServletResponse.SC_OK);
        baseRequest.setHandled(true);

        System.out.println(target);
        //Get JSONObject
        String requestBody = request.getReader().lines().collect(Collectors.joining(System.lineSeparator()));
        JSONObject requestBodyJson = new JSONObject(requestBody);
        //Start processing the webhook commit
        String webhookCommitResult = processWebhookCommit(requestBodyJson);
        sendGmail(requestBodyJson, webhookCommitResult);

        // Add commitId, date and buildlog to database
        String headCommitId = "";
        if (requestBodyJson.has("head_commit")) {
            headCommitId = requestBodyJson.getJSONObject("head_commit").getString("id");
        }
        addToDatabase(headCommitId, webhookCommitResult);
        System.out.println("Added to database.");

        response.getWriter().println("CI job done");
    }

    /*
      Sets up and starts the CI server.
     */
    public static void main(String[] args) throws Exception
    {
        Server server = new Server(8080);

        // Creating the WebAppContext for the created content
        WebAppContext ctx = new WebAppContext();
        ctx.setResourceBase("src/main/webapp");
        ctx.setContextPath("/CI-DD2480");

        // Including the JSTL jars for the webapp.
        ctx.setAttribute("org.eclipse.jetty.server.webapp.ContainerIncludeJarPattern",".*/[^/]*jstl.*\\.jar$");

        // Enabling the Annotation based configuration
        org.eclipse.jetty.webapp.Configuration.ClassList classlist = org.eclipse.jetty.webapp.Configuration.ClassList.setServerDefault(server);
        classlist.addAfter("org.eclipse.jetty.webapp.FragmentConfiguration", "org.eclipse.jetty.plus.webapp.EnvConfiguration", "org.eclipse.jetty.plus.webapp.PlusConfiguration");
        classlist.addBefore("org.eclipse.jetty.webapp.JettyWebXmlConfiguration", "org.eclipse.jetty.annotations.AnnotationConfiguration");

        // Setting the handlers and starting the Server
        HandlerCollection handlerCollection = new HandlerCollection();
        handlerCollection.addHandler(ctx); // Important that ctx is added first
        handlerCollection.addHandler(new ContinuousIntegrationServer());
        server.setHandler(handlerCollection);

        server.start();
        server.join();
    }
}
