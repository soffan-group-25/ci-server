package ciserver;

import org.junit.Test;
import static org.junit.Assert.*;

import java.io.IOException;
import java.net.http.HttpResponse;
import java.util.NoSuchElementException;

public class PipelineUpdateRequestTest {
    private PipelineUpdateRequestDTO getPR() {
        return new PipelineUpdateRequestDTO("soffan-group-25", "ci-server", "123abc",
                "token", CommitStatus.SUCCESS, "www.learn-more-about.this-build.com", "Test passed!", "ci", null);
    }

    @Test
    public void PipelineUpdateRequestEnum() {
        var dto = getPR();
        var pr = new PipelineUpdateRequest(dto);
        assertEquals(pr.body.state, "success");
    }

    @Test
    public void PipeLineUpdateRequestPathParams() {
        var dto = getPR();
        var pr = new PipelineUpdateRequest(dto);
        assertEquals(pr.path.owner, "soffan-group-25");
        assertEquals(pr.path.repository, "ci-server");
    }

    @Test
    public void PipelineUpdateRequestFail() {
        var dto = getPR();
        dto.state = CommitStatus.FAILURE;
        dto.failedOn = PipelineStage.COMPILE;
        var pr = new PipelineUpdateRequest(dto);
        assert (!pr.failedOn.isEmpty());
        assertEquals(pr.failedOn.get(), PipelineStage.COMPILE);
    }

    @Test(expected = NoSuchElementException.class)
    public void PipelineUpdateRequestNoFail() {
        var dto = getPR();
        var pr = new PipelineUpdateRequest(dto);
        assert (pr.failedOn.isEmpty());

        // Throw
        pr.failedOn.get();
    }

    @Test
    public void TestInvalidToken() throws InterruptedException, IOException {
        var dto = getPR();
        var pr = new PipelineUpdateRequest(dto);
        HttpResponse<String> resp = pr.send();
        // API response code for "bad credentials"
        assert (resp.statusCode() == 401);
        // "message": "Bad credentials"
        assert (resp.body().contains("Bad credentials"));
    }
}
