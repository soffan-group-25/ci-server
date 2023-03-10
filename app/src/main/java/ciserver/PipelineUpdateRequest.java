package ciserver;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Optional;
import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

enum CommitStatus {
    ERROR,
    FAILURE,
    PENDING,
    SUCCESS
}

/**
 * The RequestPath contains the path parameters of the PipelineUpdateRequest.
 **/
class RequestPath {
    String owner;
    String repository;
    String sha;

    RequestPath(String owner, String repo, String sha) {
        this.owner = owner;
        this.repository = repo;
        this.sha = sha;
    }
}

/**
 * The RequestBody contains the body parameters of the PipelineUpdateRequest.
 **/
class RequestBody {
    String state;

    @SerializedName("target_url")
    String targetUrl;
    String description;
    String context;

    RequestBody(String state, String url, String desc, String ctx) {
        this.state = state;
        this.targetUrl = url;
        this.description = desc;
        this.context = ctx;
    }
}

class PipelineUpdateRequestDTO {

    String owner;
    String repo;
    String sha;
    String token;
    CommitStatus state;
    String url;
    String desc;
    String ctx;
    TargetStage failedOn;

    /**
     * Creates a new PipelineUpdateRequest object.
     *
     * @param owner    The owner of the repository containing the relevant commit.
     * @param repo     The name of the repository containing the relevant commit.
     * @param sha      The SHA (hash) of the relevant commit.
     * @param token    An API authentication token used by the CI server to access
     *                 the repository and set the commit status.
     * @param state    The state to set the commit status to.
     * @param url      A link to view build logs and other output from the server.
     * @param desc     A short description of the state.
     * @param ctx      A (case insensitive) label used to differentiate this state
     *                 from a state set by another system.
     * @param failedOn The stage where the Pipeline failed, or null if it succeeded.
     **/
    PipelineUpdateRequestDTO(String owner, String repo, String sha,
            String token, CommitStatus state, String url,
            String desc, String ctx, TargetStage failedOn) {
        this.owner = owner;
        this.repo = repo;
        this.sha = sha;
        this.token = token;
        this.state = state;
        this.url = url;
        this.desc = desc;
        this.ctx = ctx;
        this.failedOn = failedOn;
    }
}

/**
 * A PipelineUpdateRequest is a class used to update the status of a commit on
 * GitHub.
 * It is indended to be built using the results of a CI Pipeline execution.
 *
 * Currently, the GitHub API version used is hard-coded to 2022-11-28.
 **/
public class PipelineUpdateRequest {
    static final String API_VERSION = "2022-11-28";

    RequestPath path;
    RequestBody body;
    Optional<TargetStage> failedOn;
    String authTkn;

    /**
     * Creates a new PipelineUpdateRequest object.
     *
     * @param DTO An object containing all relevant data.
     **/
    PipelineUpdateRequest(PipelineUpdateRequestDTO dto) {
        this.path = new RequestPath(dto.owner, dto.repo, dto.sha);
        String stateStr = dto.state.toString().toLowerCase();
        this.body = new RequestBody(stateStr, dto.url, dto.desc, dto.ctx);
        this.failedOn = Optional.ofNullable(dto.failedOn);
        this.authTkn = dto.token;
    }

    /**
     * Sends an HTTP POST request to the repository specified in the
     * PipelineUpdateRequest path,
     * containing information about the success status of a certain build.
     *
     * @return the HTTP response formatted as a String.
     *
     * @throws InterruptedException {@link java.net.http.HttpClient#send(HttpRequest, HttpResponse.BodyHandler)
     *                              if the operation is interrupted}
     * @throws IOException          {@link java.net.http.HttpClient#send(HttpRequest, HttpResponse.BodyHandler)
     *                              if an I/O error occurs when sending or
     *                              receiving}
     *
     **/
    public HttpResponse<String> send() throws InterruptedException, IOException {
        Gson gson = new Gson();
        String requestBody = gson.toJson(this.body);

        // Build the actual request
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest req = HttpRequest.newBuilder()
                .uri(URI.create("https://api.github.com/repos/" + this.path.owner + "/" + this.path.repository
                        + "/statuses/" + this.path.sha))
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .header("Accept", "application/vnd.github+json")
                .header("Authorization", "Bearer " + this.authTkn)
                .header("X-GitHub-Api-Version", API_VERSION)
                .build();

        // May throw InterruptedException or IOException.
        // Note that it does NOT throw on bad status codes, only internal errors.
        HttpResponse<String> resp = client.send(req, HttpResponse.BodyHandlers.ofString());
        return resp;
    }
}
