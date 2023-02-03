/*
 * This Java source file was generated by the Gradle 'init' task.
 */
package ciserver;

import org.junit.Test;
import static org.junit.Assert.*;

public class PipelineTest {
    @Test
    public void canExecutePipeline() {
        var event = new PushEvent();
        var pipeline = new Pipeline(event, "./pipeline");
        assertNotNull(pipeline);

        var status = pipeline.start();
        assertNotNull(status);
    }
}
