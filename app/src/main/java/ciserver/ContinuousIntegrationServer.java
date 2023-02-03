package ciserver;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.ServletException;

import java.io.IOException;
import java.util.Optional;
import java.util.stream.Collectors;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

public class ContinuousIntegrationServer extends AbstractHandler {

    Gson gson = new Gson();

    private void handlePushEvent(PushEvent event) {
        System.err.printf("%s, %s", event.ref, event.headCommit.url);

        // Here you do all the continuous integration tasks
        // For example:
        // 1st clone your repository
        // 2nd compile the code
    }

    public void handle(String target,
            Request baseRequest,
            HttpServletRequest request,
            HttpServletResponse response)
            throws IOException, ServletException {
        response.setContentType("text/html;charset=utf-8");
        response.setStatus(HttpServletResponse.SC_OK);
        baseRequest.setHandled(true);

        var eventType = Optional.ofNullable(request.getHeader("X-GitHub-Event"));
        var body = request.getReader().lines().collect(Collectors.joining());

        try {
            if (!eventType.isPresent()) {
                throw new Exception("'X-GitHub-Event' header is not present.");
            }

            switch (eventType.get()) {
                case "push":
                    System.out.println(body);
                    handlePushEvent(gson.fromJson(body, PushEvent.class));
                    break;
                default: // Unimplemented, simply ignore
            }
        } catch (JsonSyntaxException e) {
            System.err.printf("There was an error parsing the JSON for event of type %s\n", eventType.get());
        } catch (Exception e) {
            System.err.println(e);
        }

        response.getWriter().println("CI job done");
    }

    // Used to start the CI server in command line
    public static void startServer() throws Exception {
        Server server = new Server(8080);
        server.setHandler(new ContinuousIntegrationServer());
        server.start();
        server.join();
    }
}
