package ciserver;

import org.junit.Test;
import com.google.gson.Gson;
import static org.junit.Assert.*;

public class PushEventTest {
    // Typical push event
    public static final String TestData = "{	\"ref\": \"refs/heads/main\",	\"before\": \"e52bb2c1c1e7f09422d7eacf022afa61d7dce0da\",	\"after\": \"2c8db6c6d079f23710da7b1a61d9cb04fb366e04\",	\"repository\": {		\"id\": 596590165,		\"node_id\": \"R_kgDOI48-VQ\",		\"name\": \"ci-test\",		\"full_name\": \"didrikmunther/ci-test\",		\"private\": true,		\"owner\": {			\"name\": \"didrikmunther\",			\"email\": \"5240046+didrikmunther@users.noreply.github.com\",			\"login\": \"didrikmunther\",			\"id\": 5240046,			\"node_id\": \"MDQ6VXNlcjUyNDAwNDY=\",			\"avatar_url\": \"https://avatars.githubusercontent.com/u/5240046?v=4\",			\"gravatar_id\": \"\",			\"url\": \"https://api.github.com/users/didrikmunther\",			\"html_url\": \"https://github.com/didrikmunther\",			\"followers_url\": \"https://api.github.com/users/didrikmunther/followers\",			\"following_url\": \"https://api.github.com/users/didrikmunther/following{/other_user}\",			\"gists_url\": \"https://api.github.com/users/didrikmunther/gists{/gist_id}\",			\"starred_url\": \"https://api.github.com/users/didrikmunther/starred{/owner}{/repo}\",			\"subscriptions_url\": \"https://api.github.com/users/didrikmunther/subscriptions\",			\"organizations_url\": \"https://api.github.com/users/didrikmunther/orgs\",			\"repos_url\": \"https://api.github.com/users/didrikmunther/repos\",			\"events_url\": \"https://api.github.com/users/didrikmunther/events{/privacy}\",			\"received_events_url\": \"https://api.github.com/users/didrikmunther/received_events\",			\"type\": \"User\",			\"site_admin\": false		},		\"html_url\": \"https://github.com/didrikmunther/ci-test\",		\"description\": null,		\"fork\": false,		\"url\": \"https://github.com/didrikmunther/ci-test\",		\"forks_url\": \"https://api.github.com/repos/didrikmunther/ci-test/forks\",		\"keys_url\": \"https://api.github.com/repos/didrikmunther/ci-test/keys{/key_id}\",		\"collaborators_url\": \"https://api.github.com/repos/didrikmunther/ci-test/collaborators{/collaborator}\",		\"teams_url\": \"https://api.github.com/repos/didrikmunther/ci-test/teams\",		\"hooks_url\": \"https://api.github.com/repos/didrikmunther/ci-test/hooks\",		\"issue_events_url\": \"https://api.github.com/repos/didrikmunther/ci-test/issues/events{/number}\",		\"events_url\": \"https://api.github.com/repos/didrikmunther/ci-test/events\",		\"assignees_url\": \"https://api.github.com/repos/didrikmunther/ci-test/assignees{/user}\",		\"branches_url\": \"https://api.github.com/repos/didrikmunther/ci-test/branches{/branch}\",		\"tags_url\": \"https://api.github.com/repos/didrikmunther/ci-test/tags\",		\"blobs_url\": \"https://api.github.com/repos/didrikmunther/ci-test/git/blobs{/sha}\",		\"git_tags_url\": \"https://api.github.com/repos/didrikmunther/ci-test/git/tags{/sha}\",		\"git_refs_url\": \"https://api.github.com/repos/didrikmunther/ci-test/git/refs{/sha}\",		\"trees_url\": \"https://api.github.com/repos/didrikmunther/ci-test/git/trees{/sha}\",		\"statuses_url\": \"https://api.github.com/repos/didrikmunther/ci-test/statuses/{sha}\",		\"languages_url\": \"https://api.github.com/repos/didrikmunther/ci-test/languages\",		\"stargazers_url\": \"https://api.github.com/repos/didrikmunther/ci-test/stargazers\",		\"contributors_url\": \"https://api.github.com/repos/didrikmunther/ci-test/contributors\",		\"subscribers_url\": \"https://api.github.com/repos/didrikmunther/ci-test/subscribers\",		\"subscription_url\": \"https://api.github.com/repos/didrikmunther/ci-test/subscription\",		\"commits_url\": \"https://api.github.com/repos/didrikmunther/ci-test/commits{/sha}\",		\"git_commits_url\": \"https://api.github.com/repos/didrikmunther/ci-test/git/commits{/sha}\",		\"comments_url\": \"https://api.github.com/repos/didrikmunther/ci-test/comments{/number}\",		\"issue_comment_url\": \"https://api.github.com/repos/didrikmunther/ci-test/issues/comments{/number}\",		\"contents_url\": \"https://api.github.com/repos/didrikmunther/ci-test/contents/{+path}\",		\"compare_url\": \"https://api.github.com/repos/didrikmunther/ci-test/compare/{base}...{head}\",		\"merges_url\": \"https://api.github.com/repos/didrikmunther/ci-test/merges\",		\"archive_url\": \"https://api.github.com/repos/didrikmunther/ci-test/{archive_format}{/ref}\",		\"downloads_url\": \"https://api.github.com/repos/didrikmunther/ci-test/downloads\",		\"issues_url\": \"https://api.github.com/repos/didrikmunther/ci-test/issues{/number}\",		\"pulls_url\": \"https://api.github.com/repos/didrikmunther/ci-test/pulls{/number}\",		\"milestones_url\": \"https://api.github.com/repos/didrikmunther/ci-test/milestones{/number}\",		\"notifications_url\": \"https://api.github.com/repos/didrikmunther/ci-test/notifications{?since,all,participating}\",		\"labels_url\": \"https://api.github.com/repos/didrikmunther/ci-test/labels{/name}\",		\"releases_url\": \"https://api.github.com/repos/didrikmunther/ci-test/releases{/id}\",		\"deployments_url\": \"https://api.github.com/repos/didrikmunther/ci-test/deployments\",		\"created_at\": 1675347426,		\"updated_at\": \"2023-02-02T14:17:06Z\",		\"pushed_at\": 1675428495,		\"git_url\": \"git://github.com/didrikmunther/ci-test.git\",		\"ssh_url\": \"git@github.com:didrikmunther/ci-test.git\",		\"clone_url\": \"https://github.com/didrikmunther/ci-test.git\",		\"svn_url\": \"https://github.com/didrikmunther/ci-test\",		\"homepage\": null,		\"size\": 0,		\"stargazers_count\": 0,		\"watchers_count\": 0,		\"language\": null,		\"has_issues\": true,		\"has_projects\": true,		\"has_downloads\": true,		\"has_wiki\": true,		\"has_pages\": false,		\"has_discussions\": false,		\"forks_count\": 0,		\"mirror_url\": null,		\"archived\": false,		\"disabled\": false,		\"open_issues_count\": 2,		\"license\": null,		\"allow_forking\": true,		\"is_template\": false,		\"web_commit_signoff_required\": false,		\"topics\": [],		\"visibility\": \"private\",		\"forks\": 0,		\"open_issues\": 2,		\"watchers\": 0,		\"default_branch\": \"main\",		\"stargazers\": 0,		\"master_branch\": \"main\"	},	\"pusher\": {		\"name\": \"didrikmunther\",		\"email\": \"5240046+didrikmunther@users.noreply.github.com\"	},	\"sender\": {		\"login\": \"didrikmunther\",		\"id\": 5240046,		\"node_id\": \"MDQ6VXNlcjUyNDAwNDY=\",		\"avatar_url\": \"https://avatars.githubusercontent.com/u/5240046?v=4\",		\"gravatar_id\": \"\",		\"url\": \"https://api.github.com/users/didrikmunther\",		\"html_url\": \"https://github.com/didrikmunther\",		\"followers_url\": \"https://api.github.com/users/didrikmunther/followers\",		\"following_url\": \"https://api.github.com/users/didrikmunther/following{/other_user}\",		\"gists_url\": \"https://api.github.com/users/didrikmunther/gists{/gist_id}\",		\"starred_url\": \"https://api.github.com/users/didrikmunther/starred{/owner}{/repo}\",		\"subscriptions_url\": \"https://api.github.com/users/didrikmunther/subscriptions\",		\"organizations_url\": \"https://api.github.com/users/didrikmunther/orgs\",		\"repos_url\": \"https://api.github.com/users/didrikmunther/repos\",		\"events_url\": \"https://api.github.com/users/didrikmunther/events{/privacy}\",		\"received_events_url\": \"https://api.github.com/users/didrikmunther/received_events\",		\"type\": \"User\",		\"site_admin\": false	},	\"created\": false,	\"deleted\": false,	\"forced\": false,	\"base_ref\": null,	\"compare\": \"https://github.com/didrikmunther/ci-test/compare/e52bb2c1c1e7...2c8db6c6d079\",	\"commits\": [{		\"id\": \"2c8db6c6d079f23710da7b1a61d9cb04fb366e04\",		\"tree_id\": \"5dab785edbd133e97e3b93384fccd7de903d51db\",		\"distinct\": true,		\"message\": \"bbb\",		\"timestamp\": \"2023-02-03T13:48:12+01:00\",		\"url\": \"https://github.com/didrikmunther/ci-test/commit/2c8db6c6d079f23710da7b1a61d9cb04fb366e04\",		\"author\": {			\"name\": \"Didrik Munther\",			\"email\": \"dmu0817@gmail.com\",			\"username\": \"didrikmunther\"		},		\"committer\": {			\"name\": \"Didrik Munther\",			\"email\": \"dmu0817@gmail.com\",			\"username\": \"didrikmunther\"		},		\"added\": [],		\"removed\": [],		\"modified\": [\"README.md\"]	}],	\"head_commit\": {		\"id\": \"2c8db6c6d079f23710da7b1a61d9cb04fb366e04\",		\"tree_id\": \"5dab785edbd133e97e3b93384fccd7de903d51db\",		\"distinct\": true,		\"message\": \"bbb\",		\"timestamp\": \"2023-02-03T13:48:12+01:00\",		\"url\": \"https://github.com/didrikmunther/ci-test/commit/2c8db6c6d079f23710da7b1a61d9cb04fb366e04\",		\"author\": {			\"name\": \"Didrik Munther\",			\"email\": \"dmu0817@gmail.com\",			\"username\": \"didrikmunther\"		},		\"committer\": {			\"name\": \"Didrik Munther\",			\"email\": \"dmu0817@gmail.com\",			\"username\": \"didrikmunther\"		},		\"added\": [],		\"removed\": [],		\"modified\": [\"README.md\"]	}}";

    Gson gson = new Gson();

    @Test
    public void parsesPushEvent() {
        var e = gson.fromJson(TestData, PushEvent.class);

        var head = e.headCommit;
        assertEquals(head.id, "2c8db6c6d079f23710da7b1a61d9cb04fb366e04");
        assertEquals(e.commits.size(), 1);
        assertEquals(e.pusher.name, "didrikmunther");
        assertEquals(e.ref, "refs/heads/main");
    }
}
