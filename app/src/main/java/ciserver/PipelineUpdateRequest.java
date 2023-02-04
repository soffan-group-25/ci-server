package ciserver;

import java.util.Optional;

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
    RequestPath path;
    RequestBody body;
    Optional<PipelineStage> failedOn;

    PipelineUpdateRequest(String owner, String repo, String sha, CommitStatus state, String url, String desc,
            String ctx, PipelineStage failedOn) {
        this.path = new RequestPath(owner, repo, sha);
        String state_str = state.toString().toLowerCase();
        this.body = new RequestBody(state_str, url, desc, ctx);
        this.failedOn = Optional.ofNullable(failedOn);
    }
}
