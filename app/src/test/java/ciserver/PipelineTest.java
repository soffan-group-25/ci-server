/*
 * This Java source file was generated by the Gradle 'init' task.
 */
package ciserver;

import static org.junit.Assert.*;

import org.junit.Test;

public class PipelineTest {

    public static final String PipelineTestingDirectory = "../pipeline_testing";
    
    @Test
    public void canExecutePipeline() {
        var commit = new Commit();
        var pipeline = new Pipeline(commit, "./pipeline");
        assertNotNull(pipeline);

        var status = pipeline.start(TargetStage.ALL);
        assertNotNull(status);
    }

    @Test
    public void pipelineObserverIsNotified() {
        var commit = new Commit();
        var pipeline = new Pipeline(commit, "./pipeline");
        var pipelineObserver = new PipelineObserver() {

            boolean observerIsNotified = false;

            @Override
            public void update(TargetStage stage, PipelineStatus status) {
                observerIsNotified = true;
            }
        };

        pipeline.addObserver(pipelineObserver);
        pipeline.start(TargetStage.ALL);

        assertTrue(pipelineObserver.observerIsNotified);
    }
}
