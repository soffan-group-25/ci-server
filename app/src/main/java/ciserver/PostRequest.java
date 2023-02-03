package ciserver;

enum CommitStatus {
    ERROR,
    FAILURE,
    PENDING,
    SUCCESS
}

// Path parameters
class PostRequestPath {
    String owner;
    String repository;
    String sha;

    PostRequestPath(String owner, String repo, String sha) {
        this.owner = owner;
        this.repository = repo;
        this.sha = sha;
    }
}

// Body parameters. If this class is needed for another
// type of post request later on, we could add more.
class PostRequestBody {
    String state;
    String target_url;
    String description;
    String context;

    PostRequestBody(String state, String url, String desc, String ctx) {
        this.state = state;
        this.target_url = url;
        this.description = desc;
        this.context = ctx;
    }
}

public class PostRequest {
    PostRequestPath path;
    PostRequestBody body;

    PostRequest(String owner, String repo, String sha, CommitStatus state, String url, String desc, String ctx) {
        this.path = new PostRequestPath(owner, repo, sha);
        String state_str = state.toString().toLowerCase();
        this.body = new PostRequestBody(state_str, url, desc, ctx);
    }
}
