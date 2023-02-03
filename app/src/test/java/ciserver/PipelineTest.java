/*
 * This Java source file was generated by the Gradle 'init' task.
 */
package ciserver;

import org.junit.Test;
import static org.junit.Assert.*;

public class PipelineTest {
    @Test
    public void canExecutePipeline() {
        var pipeline = new Pipeline("./pipeline");
        var commit = new Commit();

        var instance = pipeline.create(commit);
        assertNotNull(instance);

        var status = instance.start();
        assertNotNull(status);
    }
}
