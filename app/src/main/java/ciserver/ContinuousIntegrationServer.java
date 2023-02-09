package ciserver;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.ServletException;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Optional;
import java.util.stream.Collectors;

import org.eclipse.jetty.server.Server;
import org.apache.commons.io.FileUtils;
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
    private final String pipelineDir = "../pipeline";

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

    private PipelineResult executePipeline(PushEvent event, Pipeline pipeline, PipelineObserver observer) {
        pipeline.addObserver(observer);
        return pipeline.start(TargetStage.ALL);

    }

    private void logBuild(PushEvent event, PipelineResult result) {
        try {
            var fileName = String.format("%s-%s-%s.%s:%s:%s.%s.log", Calendar.getInstance().get(Calendar.YEAR),
                    Calendar.getInstance().get(Calendar.MONTH), Calendar.getInstance().get(Calendar.DAY_OF_MONTH),
                    Calendar.getInstance().get(Calendar.HOUR), Calendar.getInstance().get(Calendar.MINUTE),
                    Calendar.getInstance().get(Calendar.SECOND), event.headCommit.id);
            File dir = new File(String.format("%s/logs/%s", pipelineDir, event.repository.name));
            dir.mkdirs();
            File file = new File(String.format("%s/logs/%s/%s", pipelineDir, event.repository.name, fileName));
            file.createNewFile();

            var writer = new BufferedWriter(new FileWriter(file));
            writer.write(result.output);
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void handlePushEvent(PushEvent event) {
        System.err.printf("%s, %s", event.ref, event.headCommit.url);

        var pipeline = new Pipeline(event, pipelineDir);
        var result = executePipeline(event, pipeline, new PipelineObserver() {
            @Override
            public void update(TargetStage stage, PipelineStatus status) {
                System.err.printf("\tRunning stage: %s: %s\n", stage, status);

                if (status == PipelineStatus.Fail) {
                    sendUpdateRequest(event, CommitStatus.FAILURE,
                            String.format("Failed during stage %s with status %s.", stage, status), stage);
                } else if (status == PipelineStatus.InProgress) {
                    sendUpdateRequest(event, CommitStatus.PENDING,
                            String.format("Doing stage: %s", stage), stage);
                }

                // Add sleeping to every step so we have a chance to show
                // the grading assistant that this works.
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        if (result.status != PipelineStatus.Fail) {
            sendUpdateRequest(event, CommitStatus.SUCCESS, "Pipeline succeeded", null);
        }

        System.out.println("Commit status update sent successfully.");

        logBuild(event, result);
    }

    private void handleSpecificLogRequest(ArrayList<String> url, HttpServletResponse response) throws IOException {
        File file = new File(String.format("%s/logs/%s", pipelineDir, url.get(2)));
        if (!file.exists()) {
            return;
        }

        var contents = FileUtils.readFileToString(file, "UTF-8");
        var writer = response.getWriter();

        writer.println(contents);

    }

    private void handleLogRequest(ArrayList<String> url, HttpServletResponse response) throws IOException {
        // We are requesting a specific log
        if (url.size() >= 3) {
            handleSpecificLogRequest(url, response);
            return;
        }

        File dir = new File(String.format("%s/logs", pipelineDir));
        dir.mkdirs();

        var writer = response.getWriter();

        writer.printf("<ul>");
        for (var project : dir.listFiles()) {
            var projectPath = project.getPath().split("/");
            var projectName = projectPath[projectPath.length - 1];

            writer.printf("<li>");
            writer.printf("<h5>%s</h5>", projectName);
            writer.printf("<ul>");
            for (var logFile : project.listFiles()) {
                var logPath = logFile.getPath().split("/");
                var logName = logPath[logPath.length - 1];

                writer.printf("<li>");
                writer.printf("<a href=\"%s/%s\"><h6>%s</h6></a>", projectName, logName, logName);
                writer.printf("</li>");
            }
            writer.printf("</ul>");
            writer.printf("</li>");
        }
        writer.printf("</ul>");
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

        var url = new ArrayList<>(Arrays.asList(request.getRequestURI().split("/")));

        if (url.size() >= 1 && "log".equals(url.get(1))) {
            handleLogRequest(url, response);
            return;
        }

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
