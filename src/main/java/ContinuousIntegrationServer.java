import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.ServletException;

import java.io.IOException;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;

import java.util.stream.Collectors;
import org.json.*;

/**
 Skeleton of a ContinuousIntegrationServer which acts as webhook
 See the Jetty documentation for API documentation of those classes.
*/
public class ContinuousIntegrationServer extends AbstractHandler
{
    public static String processWebhookCommit(String requestBody) {
        JSONObject requestBodyJson = new JSONObject(requestBody);
        if (requestBodyJson.has("head_commit")) {
            String headCommitId = requestBodyJson.getJSONObject("head_commit").getString("id");
            String repoUrl = requestBodyJson.getJSONObject("repository").getString("clone_url");
            System.out.println("extracted commit and repo: " + headCommitId + " " + repoUrl);
        }

        return "BUILD SUCCESS";
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
