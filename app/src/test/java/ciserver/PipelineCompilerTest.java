/*
 * This Java source file was generated by the Gradle 'init' task.
 */
package ciserver;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

import org.apache.commons.io.FileUtils;
import org.junit.Test;

import com.google.gson.Gson;

public class PipelineCompilerTest {
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
