package ciserver;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Optional;
import com.google.gson.Gson;

enum CommitStatus {
    ERROR,
    FAILURE,
    PENDING,
    SUCCESS
}

enum PipelineStage {
    PULL,
    LINT,
    COMPILE
}

// Path parameters
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

// Body parameters. If this class is needed for another
// type of post request later on, we could add more.
class RequestBody {
    String state;
    String target_url;
    String description;
    String context;

    RequestBody(String state, String url, String desc, String ctx) {
        this.state = state;
        this.target_url = url;
        this.description = desc;
        this.context = ctx;
    }
}

public class PipelineUpdateRequest {
    static final String API_VERSION = "2022-11-28";

    RequestPath path;
    RequestBody body;
    Optional<PipelineStage> failedOn;
    String auth_tkn;

    PipelineUpdateRequest(String owner, String repo, String sha, String token, CommitStatus state, String url,
            String desc, String ctx, PipelineStage failedOn) {
        this.path = new RequestPath(owner, repo, sha);
        String state_str = state.toString().toLowerCase();
        this.body = new RequestBody(state_str, url, desc, ctx);
        this.failedOn = Optional.ofNullable(failedOn);
        this.auth_tkn = token;
    }

    public int send() throws InterruptedException, IOException {
        Gson gson = new Gson();
        String request_body = gson.toJson(this.body);

        // Build the actual request
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest req = HttpRequest.newBuilder()
                .uri(URI.create("https://api.github.com/repos/" + this.path.owner + "/" + this.path.repository
                        + "/statuses/" + this.path.sha))
                .POST(HttpRequest.BodyPublishers.ofString(request_body))
                .header("Accept", "application/vnd.github+json")
                .header("Authorization", "Bearer " + this.auth_tkn)
                .header("X-GitHub-Api-Version", API_VERSION)
                .build();

        // May throw InterruptedException or IOException.
        // Note that it does NOT throw on bad status codes, only internal errors.
        HttpResponse<String> resp = client.send(req, HttpResponse.BodyHandlers.ofString());
        return resp.statusCode();
    }
}
