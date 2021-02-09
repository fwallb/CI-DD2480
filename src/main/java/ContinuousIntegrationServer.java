import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.ServletException;

import java.io.IOException;
import java.io.*;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;

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

            System.out.println("Running git checkout.");//måste man checka ut till branch först?
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
        // JSONObject requestBodyJson = new JSONObject(requestBody);
        if (requestBodyJson.has("head_commit")) {
            String headCommitId = requestBodyJson.getJSONObject("head_commit").getString("id");
            String repoUrl = requestBodyJson.getJSONObject("repository").getString("clone_url");
            System.out.println("extracted commit and repo: " + headCommitId + " " + repoUrl);

            return cloneAndTest(repoUrl, headCommitId);
        }

        return "";
    }

    //add javadoc
    public static void sendEmail(JSONObject requestBodyJson, String webhookCommitResult){//use these or create a new one?
      if (!(requestBodyJson.has("head_commit"))) {
        System.out.println("no head_commit");
        return;
      }

      String headCommitId = requestBodyJson.getJSONObject("head_commit").getString("id");
      String repoUrl = requestBodyJson.getJSONObject("repository").getString("clone_url");
      String mailContent = cloneAndTest(repoUrl, headCommitId);

      String recipient = "sara.damne@gmail.com";//requestBodyJson.getJSONObject("author").getString("email");
      String recipientName = requestBodyJson.getJSONObject("author").getString("username");

      String host = "localhost";//??

      // Propterties properties = System.getProperties();
      // properties.setProperty("mail.smtp.host", host);
      // Session session = Session.getDefaultInstance(properties);

      Properties properties = new Properties();
      Session session = Session.getInstance(properties,null);

      try{
        MimeMessage message = new MimeMessage(session);
        message.setFrom(new InternetAddress("sara.damne@gmail.com"));//from who?
        message.addRecipient(Message.RecipientType.TO, new InternetAddress(recipient));
        message.setSubject("Status update");

        String currentBranch = "TODO";//requestBodyJson.getString("ref");
        if(webhookCommitResult.contains("BUILD SUCCESS")){
          message.setText("Your latest commit status on branch: " + currentBranch + " = Success");//is this enough?
        }else if(webhookCommitResult.contains("BUILD FAILURE")){//FAILED?
          message.setText("Your latest commit status on branch: " + currentBranch + " = Failure");
        }else{
          System.out.println("Something went wrong!");
        }

        Transport.send(message);
        System.out.println("Message sent successfully");
      }catch(MessagingException mex){
        mex.printStackTrace();
      }
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
        JSONObject requestBodyJson = new JSONObject(requestBody);
        String webhookCommitResult = processWebhookCommit(requestBodyJson);
        sendEmail(requestBodyJson, webhookCommitResult);

        response.getWriter().println("CI job done");
    }

    // used to start the CI server in command line
    public static void main(String[] args) throws Exception
    {
        Server server = new Server(8080);
        server.setHandler(new ContinuousIntegrationServer());
        server.start();
        server.join();
    }
}
