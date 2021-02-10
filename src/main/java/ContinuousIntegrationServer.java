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
import org.json.*;
import java.nio.file.*;
import java.io.File;
import java.io.BufferedReader;
import java.io.InputStreamReader;

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
    public static String cloneAndTest(String repoUrl, String commitId) {
        try {
            Path tempDir = Files.createTempDirectory("repo");
            String pathToTempDir = tempDir.toAbsolutePath().toString();
            System.out.println("path to temp dir: " + pathToTempDir);

            System.out.println("Running git clone.");
            Process process = Runtime.getRuntime().exec("git clone " + repoUrl + " " + pathToTempDir);
            process.waitFor();

            System.out.println("Running git checkout.");//checkout to branch first?
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

    public static String processWebhookCommit(JSONObject requestBodyJson) {
        if (requestBodyJson.has("head_commit")) {
            String headCommitId = requestBodyJson.getJSONObject("head_commit").getString("id");
            String repoUrl = requestBodyJson.getJSONObject("repository").getString("clone_url");
            System.out.println("extracted commit and repo: " + headCommitId + " " + repoUrl);

            return cloneAndTest(repoUrl, headCommitId);
        }

        return "";
    }

    /*
    * Sends email to the author of a commit. Status depends on the results from processWebhookCommit().
    * @param {JSONObject} requestBodyJson the JSONObject for this commit
    * @param {String} webhookCommitResult contains output from the tests.
    */
    public static boolean sendGmail(JSONObject requestBodyJson, String webhookCommitResult) {
            if (!(requestBodyJson.has("head_commit"))) {
              System.out.println("no head_commit");
              return false;
            }

            final String username = "group20cidd2480@gmail.com";
            final String password = "Password1234!";

            Properties prop = new Properties();
            prop.put("mail.smtp.host", "smtp.gmail.com");
            prop.put("mail.smtp.port", "587");
            prop.put("mail.smtp.auth", "true");
            prop.put("mail.smtp.starttls.enable", "true"); //TLS

            Session session = Session.getInstance(prop,
                    new javax.mail.Authenticator() {
                        protected PasswordAuthentication getPasswordAuthentication() {
                            return new PasswordAuthentication(username, password);
                        }
                    });

            try {
                String headCommitId = requestBodyJson.getJSONObject("head_commit").getString("id");
                String repoUrl = requestBodyJson.getJSONObject("repository").getString("clone_url");
                String mailContent = cloneAndTest(repoUrl, headCommitId);

                String recipient = requestBodyJson.getJSONObject("author").getString("email");

                Message message = new MimeMessage(session);
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

                Transport.send(message);
                System.out.println("Message sent successfully");
                // System.out.println(requestBodyJson.toString());
                return true;

            } catch (MessagingException e) {
                e.printStackTrace();
            }
            return false;
        }


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
        String requestBody = request.getReader().lines().collect(Collectors.joining(System.lineSeparator()));
        JSONObject requestBodyJson = new JSONObject(requestBody);//messes up the test cases
        String webhookCommitResult = processWebhookCommit(requestBodyJson);
        sendGmail(requestBodyJson, webhookCommitResult);

        response.getWriter().println("CI job done");
    }

    // used to start the CI server in command line
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
