package ciserver;

import java.util.ArrayList;

import com.google.gson.annotations.SerializedName;

class Repository {
	String name;
	String full_name;
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

public class PushEvent {
	String ref; // hold branch name
	String compare; // link to see changes

	Author pusher;
	Repository repository;
	ArrayList<Commit> commits;

	@SerializedName("head_commit")
	Commit headCommit;
}
