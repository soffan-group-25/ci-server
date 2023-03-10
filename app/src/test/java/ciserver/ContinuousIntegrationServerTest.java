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

    /*
     * Requires changing the URL builder function to public (didn't get reflection working)
    @Test
    public void testURLBuilder() throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        var server = new ContinuousIntegrationServer();
        var event = (new Gson()).fromJson(PushEventTest.TestData, PushEvent.class);
        String BASE_URL = System.getenv("BASE_URL");
        String expected =BASE_URL+"/log/ci-test/2023-02-03.13:48:12.2c8db6c6d079f23710da7b1a61d9cb04fb366e04.Ok.log";
        System.err.println(server.buildLogURL(event, PipelineStatus.Ok));
        System.err.println(expected);
        assert(server.buildLogURL(event, PipelineStatus.Ok).equals( expected));
}
        */

    @Test
    public void executePipelineWorks() throws NoSuchMethodException, SecurityException, IllegalAccessException,
            IllegalArgumentException, InvocationTargetException {
        var server = new ContinuousIntegrationServer();
        var method = server.getClass().getDeclaredMethod("executePipeline", PushEvent.class, Pipeline.class,
                PipelineObserver.class);
        method.setAccessible(true);

        var event = (new Gson()).fromJson(PushEventTest.TestData, PushEvent.class);
        var pipeline = new Pipeline(event, PipelineTest.PipelineTestingDirectory);
        pipeline.compiler = new PipelineCommandExecuter("touch", "test_file.txt");
        pipeline.tester = new PipelineCommandExecuter("touch", "test_file2.txt");

        var observer = new PipelineObserver() {
            ArrayList<Update> updates = new ArrayList<>();

            @Override
            public void update(TargetStage stage, PipelineStatus status) {
                updates.add(new Update(stage, status));
            }
        };

        var result = (PipelineResult) method.invoke(server, event, pipeline, observer);
        assertEquals(PipelineStatus.Ok, result.status);

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
