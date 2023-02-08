package ciserver;

import org.junit.Test;
import static org.junit.Assert.*;

import java.util.NoSuchElementException;

public class PipelineUpdateRequestTest {
    @Test
    public void PipelineUpdateRequestEnum() {
        PipelineUpdateRequest pr = new PipelineUpdateRequest("soffan-group-25", "ci-server", "123abc", CommitStatus.SUCCESS, "www.learn-more-about.this-build.com", "Test passed!", "ci", null);
        assertEquals(pr.body.state, "success");
    }
    @Test
    public void PipeLineUpdateRequestPathParams() {
        PipelineUpdateRequest pr = new PipelineUpdateRequest("soffan-group-25", "ci-server", "123abc", CommitStatus.SUCCESS, "www.learn-more-about.this-build.com", "Test passed!", "ci", null);
        assertEquals(pr.path.owner, "soffan-group-25");
        assertEquals(pr.path.repository, "ci-server");
    }

    @Test
    public void PipelineUpdateRequestFail() {
        PipelineUpdateRequest pr = new PipelineUpdateRequest("soffan-group-25", "ci-server", "123abc", CommitStatus.FAILURE, "www.learn-more-about.this-build.com", "Test passed!", "ci", PipelineStage.COMPILE);
        assert(!pr.failedOn.isEmpty());
        assertEquals(pr.failedOn.get(), PipelineStage.COMPILE);
    }

    @Test(expected=NoSuchElementException.class)
    public void PipelineUpdateRequestNoFail() {
        PipelineUpdateRequest pr = new PipelineUpdateRequest("soffan-group-25", "ci-server", "123abc", CommitStatus.SUCCESS, "www.learn-more-about.this-build.com", "Test passed!", "ci", null);
        assert(pr.failedOn.isEmpty());

        // Throw
        pr.failedOn.get();
    }
}

