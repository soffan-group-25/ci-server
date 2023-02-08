/*
 * This Java source file was generated by the Gradle 'init' task.
 */
package ciserver;

import static org.junit.Assert.*;

import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.eclipse.jgit.api.Git;
import org.junit.Test;

import com.google.gson.Gson;

import java.io.File;
import java.util.Arrays;

public class PipelineTest {

    public static final String PipelineTestingDirectory = "../pipeline_testing";

    @Test
    public void canExecutePipeline() {
        var gson = new Gson();
        var event = gson.fromJson(PushEventTest.TestData, PushEvent.class);

        var pipeline = new Pipeline(event, PipelineTestingDirectory);
        assertNotNull(pipeline);

        var status = pipeline.start(TargetStage.ALL);
        assertNotNull(status);
    }

    @Test
    public void pipelineObserverIsNotified() {
        var gson = new Gson();
        var event = gson.fromJson(PushEventTest.TestData, PushEvent.class);

        var pipeline = new Pipeline(event, PipelineTestingDirectory);
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

    @Test
    public void canPull() throws IOException {
        var gson = new Gson();
        var event = gson.fromJson(PushEventTest.TestData, PushEvent.class);
        var pipeline = new Pipeline(event, PipelineTestingDirectory);

        var status = pipeline.start(TargetStage.PULL);
        assertEquals(status, PipelineStatus.Ok);

        File directory = new File(
                String.format("%s/%s/%s", PipelineTestingDirectory, event.repository.name,
                        event.headCommit.id));
        assertTrue(directory.isDirectory());

        try {
            var repo = Git.open(directory);
            assertEquals(event.headCommit.id, repo.getRepository().getBranch());
        } catch (IOException e) {
            assertTrue("Could not open git project", false);
        } finally {
            FileUtils.deleteDirectory(directory);
        }
    }

    /**
     * Test the compiling capabilities of the `PipelineCompiler` component.`
     * The test is done by executing a simple `touch` command to see if it has any
     * effect in the folder.
     * 
     * Note that this test requires that `PipelinePuller works`.
     * 
     * @throws IOException
     */
    @Test
    public void canCompile() throws IOException {
        var fileName = "test_file.txt";
        var gson = new Gson();
        var event = gson.fromJson(PushEventTest.TestData, PushEvent.class);
        var pipeline = new Pipeline(event, PipelineTest.PipelineTestingDirectory);
        pipeline.compiler = new PipelineCommandExecuter("touch", fileName);

        var status = pipeline.start(TargetStage.COMPILE);
        assertEquals(status, PipelineStatus.Ok);

        File directory = new File(
                String.format("%s/%s/%s", PipelineTest.PipelineTestingDirectory, event.repository.name,
                        event.headCommit.id));
        assertTrue(directory.isDirectory());

        var fileExists = Arrays.stream(directory.listFiles())
                .anyMatch(file -> file.getName().equals(fileName));
        assertTrue("File was created by compiler component", fileExists);

        FileUtils.deleteDirectory(directory);
    }
}
