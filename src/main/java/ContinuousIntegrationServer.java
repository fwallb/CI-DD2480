import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.ServletException;

import java.io.IOException;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;

import java.util.stream.Collectors;
import org.json.*;
import java.nio.file.*;
import java.io.File;
import java.io.BufferedReader;
import java.io.InputStreamReader;

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
        server.setHandler(new ContinuousIntegrationServer());
        server.start();
        server.join();
    }
}
