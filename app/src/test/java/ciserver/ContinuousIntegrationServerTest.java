/*
 * This Java source file was generated by the Gradle 'init' task.
 */
package ciserver;

import org.junit.Test;

import com.google.gson.Gson;

import static org.junit.Assert.*;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;

public class ContinuousIntegrationServerTest {
    class Update {
        TargetStage stage;
        PipelineStatus status;

        Update(TargetStage stage, PipelineStatus status) {
            this.stage = stage;
            this.status = status;
        }
    }

    @Test
    public void executePipelineWorks() throws NoSuchMethodException, SecurityException, IllegalAccessException,
            IllegalArgumentException, InvocationTargetException {
        var server = new ContinuousIntegrationServer();
        var method = server.getClass().getDeclaredMethod("executePipeline", PushEvent.class, Pipeline.class,
                PipelineObserver.class);
        method.setAccessible(true);

        var event = (new Gson()).fromJson(PushEventTest.TestData, PushEvent.class);
        var pipeline = new Pipeline(event, PipelineTest.PipelineTestingDirectory);
        pipeline.compiler = new PipelineCompiler("touch", "test_file.txt");

        var observer = new PipelineObserver() {
            ArrayList<Update> updates = new ArrayList<>();

            @Override
            public void update(TargetStage stage, PipelineStatus status) {
                updates.add(new Update(stage, status));
            }
        };

        var status = method.invoke(server, event, pipeline, observer);
        assertEquals(status, PipelineStatus.Ok);

        var expectedUpdates = new ArrayList<Update>();
        expectedUpdates.add(new Update(TargetStage.PULL, PipelineStatus.InProgress));
        expectedUpdates.add(new Update(TargetStage.PULL, PipelineStatus.Ok));
        expectedUpdates.add(new Update(TargetStage.LINT, PipelineStatus.InProgress));
        expectedUpdates.add(new Update(TargetStage.LINT, PipelineStatus.Ok));
        expectedUpdates.add(new Update(TargetStage.COMPILE, PipelineStatus.InProgress));
        expectedUpdates.add(new Update(TargetStage.COMPILE, PipelineStatus.Ok));
        expectedUpdates.add(new Update(TargetStage.TESTING, PipelineStatus.InProgress));
        expectedUpdates.add(new Update(TargetStage.TESTING, PipelineStatus.Ok));

        for (int i = 0; i < expectedUpdates.size(); i++) {
            var expected = expectedUpdates.get(i);
            var got = observer.updates.get(i);
            
            assertNotNull(got);
            assertEquals(expected.stage, got.stage);
            assertEquals(expected.status, got.status);
        }
    }
}
