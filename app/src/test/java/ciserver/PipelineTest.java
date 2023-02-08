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
    public void canCreatePipeline() {
        var event = new PushEvent();

        // The relative directory is in the app folder, use `..` to go up
        var pipeline = new Pipeline(event, "../pipeline");
        assertNotNull(pipeline);
    }

    @Test
    public void canPull() throws IOException {
        var gson = new Gson();
        var event = gson.fromJson(PushEventTest.TestData, PushEvent.class);
        var pipeline = new Pipeline(event, PipelineTest.PipelineTestingDirectory);

        var status = pipeline.start(Target.PULL);
        assertEquals(status, PipelineStatus.Ok);

        File directory = new File(
                String.format("%s/%s/%s", PipelineTest.PipelineTestingDirectory, event.repository.name,
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

        var status = pipeline.start(Target.COMPILE);
        assertEquals(status, PipelineStatus.Ok);

        File directory = new File(
                String.format("%s/repositories/%s", PipelineTest.PipelineTestingDirectory,
                        event.headCommit.id));
        assertTrue(directory.isDirectory());

        var fileExists = Arrays.stream(directory.listFiles())
                .anyMatch(file -> file.getName().equals(fileName));
        assertTrue("File was created by compiler component", fileExists);

        FileUtils.deleteDirectory(directory);
    }
}
