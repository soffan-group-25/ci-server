package ciserver;

import org.junit.Test;
import static org.junit.Assert.*;

public class PostRequestTest {
    @Test
    public void PostRequestEnumConversion() {
        PostRequest pr = new PostRequest("soffan-group-25", "ci-server", "123abc", CommitStatus.SUCCESS, "www.learn-more-about.this-build.com", "Test passed!", "ci");
        assertEquals(pr.body.state, "success");
    }
    @Test
    public void PostRequestPathParams() {
        PostRequest pr = new PostRequest("soffan-group-25", "ci-server", "123abc", CommitStatus.SUCCESS, "www.learn-more-about.this-build.com", "Test passed!", "ci");
        assertEquals(pr.path.owner, "soffan-group-25");
        assertEquals(pr.path.repository, "ci-server");
    }
}
