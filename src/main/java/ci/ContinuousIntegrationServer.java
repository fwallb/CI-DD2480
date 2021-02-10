package ci;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.ServletException;

import java.io.IOException;

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

    public static String processWebhookCommit(String requestBody) {
        JSONObject requestBodyJson = new JSONObject(requestBody);
        if (requestBodyJson.has("head_commit")) {
            String headCommitId = requestBodyJson.getJSONObject("head_commit").getString("id");
            String repoUrl = requestBodyJson.getJSONObject("repository").getString("clone_url");
            System.out.println("extracted commit and repo: " + headCommitId + " " + repoUrl);

            return cloneAndTest(repoUrl, headCommitId);
        }

        return "";
    }

    public void addToDatabase(String commitId, String buildLog) {
        // DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        // Date date = new Date();
        // date = dateFormat.format(date);
        int date = 112233;

        try {
          Database db = new Database();
          db.insertIntoDatabase(commitId, date, buildLog);
        } catch(SQLException exep) {
          exep.printStackTrace();
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
        String webhookCommitResult = processWebhookCommit(requestBody);

        // Add commitId, date and buildlog to database
        JSONObject requestBodyJson = new JSONObject(requestBody);
        String headCommitId = "";
        if (requestBodyJson.has("head_commit")) {
            headCommitId = requestBodyJson.getJSONObject("head_commit").getString("id");
        }
        addToDatabase(headCommitId, webhookCommitResult);
        System.out.println("Added to database.");

        // here you do all the continuous integration tasks
        // for example
        // 1st clone your repository
        // 2nd compile the code

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
