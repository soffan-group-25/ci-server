package ciserver;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.ServletException;

import java.io.IOException;
import java.net.http.HttpResponse;
import java.util.Optional;
import java.util.stream.Collectors;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

/**
 * This class is the core class for running the CI server.
 * The CI server compiles and tests Java code with Gradle, and can set commit
 * statuses in GitHub
 */
public class ContinuousIntegrationServer extends AbstractHandler {

    private final Gson gson = new Gson();
    private final String GH_ACCESS_TOKEN = System.getenv("GH_ACCESS_TOKEN");

    private void sendUpdateRequest(PushEvent event, CommitStatus status, String description, TargetStage failedOn) {
        // Build the response according to the pipeline's return status (dummy variables
        // used here as we have no "real" requests to start the pipeline with yet).
        String[] repoDetails = event.repository.full_name.split("/");
        var dto = new PipelineUpdateRequestDTO(repoDetails[0], repoDetails[1], event.headCommit.id, GH_ACCESS_TOKEN,
                status, "", description, "ci", failedOn);

        PipelineUpdateRequest pr = new PipelineUpdateRequest(dto);

        try {
            HttpResponse<String> res = pr.send();

            if (res.statusCode() != 201) {
                System.err.println("HTTP POST error: " + res);
            }
        } catch (Exception e) {
            System.err.println(e);
        }
    }

    private PipelineStatus executePipeline(PushEvent event, Pipeline pipeline, PipelineObserver observer) {
        pipeline.addObserver(observer);
        return pipeline.start(TargetStage.ALL);

    }

    private void handlePushEvent(PushEvent event) {
        System.err.printf("%s, %s", event.ref, event.headCommit.url);

        var pipeline = new Pipeline(event, "../pipeline");
        var status = executePipeline(event, pipeline, new PipelineObserver() {
            @Override
            public void update(TargetStage stage, PipelineStatus status) {
                System.err.printf("\tRunning stage: %s: %s\n", stage, status);

                if (status == PipelineStatus.Fail) {
                    sendUpdateRequest(event, CommitStatus.FAILURE,
                            String.format("Failed during stage %s with status %s.", stage, status), stage);
                }
            }
        });

        if (status == PipelineStatus.Ok) {
            sendUpdateRequest(event, CommitStatus.SUCCESS, "Pipeline succeeded", null);
        }

        System.out.println("Commit status update sent successfully.");
    }

    /**
     * Handle an HTTP request to the CI server. See
     * <a
     * href=https://www.eclipse.org/jetty/javadoc/jetty-9/org/eclipse/jetty/server/handler/AbstractHandler.html#handle(java.lang.String,org.eclipse.jetty.server.Request,javax.servlet.http.HttpServletRequest,javax.servlet.http.HttpServletResponse)>documentation
     * of the parent interface</a>
     * {@inheritDoc}
     */
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
                    handlePushEvent(gson.fromJson(body, PushEvent.class));
                    break;
                default: // Unimplemented, simply ignore
            }

        } catch (JsonSyntaxException e) {
            e.printStackTrace();
            System.err.printf("There was an error parsing the JSON for event of type %s\n", eventType.get());
        } catch (Exception e) {
            e.printStackTrace();
        }

        response.getWriter().println("CI job done");
    }

    /**
     * Used to start the CI server in command line
     * 
     * @throws Exception if the server cannot be started
     */
    public static void startServer() throws Exception {
        Server server = new Server(8080);
        server.setHandler(new ContinuousIntegrationServer());
        server.start();
        server.join();
    }
}
