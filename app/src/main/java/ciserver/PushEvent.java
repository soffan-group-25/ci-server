package ciserver;

import java.util.ArrayList;

import com.google.gson.annotations.SerializedName;

class Repository {
    String name;
    String full_name;

    @SerializedName("clone_url")
    String cloneUrl;
}

class Author {
    String name;
    String email;
    String username;
}

class Commit {
    String id;
    String url;

    Author author;

    ArrayList<String> added; // added files
    ArrayList<String> removed; // removed files
    ArrayList<String> modified; // modified files
}

/**
 * This class represents the relevant information from a Push Event to GitHub.
 *
 * This class is used by Gson to parse the JSON payload of the GitHub push
 * webhook.
 * Gson will parse the parts of the JSON payload that match the fields of this
 * class. See <a
 * href=https://docs.github.com/developers/webhooks-and-events/webhooks/webhook-events-and-payloads#push>the
 * documentation on GitHub</a> for information about the class fields/JSON
 * payload.
 */
public class PushEvent {
    String ref; // hold branch name
    String compare; // link to see changes

    Author pusher;
    Repository repository;
    ArrayList<Commit> commits;

    @SerializedName("head_commit")
    Commit headCommit;
}
