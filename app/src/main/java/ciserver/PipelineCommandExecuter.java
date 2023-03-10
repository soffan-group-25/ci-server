package ciserver;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;

import org.apache.commons.io.IOUtils;

/**
 * this class is used for the compile stage of our CI pipeline
 */
public class PipelineCommandExecuter implements StageTask {
    private final ArrayList<String> commands = new ArrayList<>();

    /**
     * constructs a PipelineCompiler for running a certain set of commands
     *
     * @param commands is the command(s) to run
     */
    public PipelineCommandExecuter(String... commands) {
        this.commands.addAll(Arrays.asList(commands));
    }

	/**
	 * Run the specified `commands` in the folder of the pulled repository.
	 *
	 * Note: the PipelinePull component or a similar action must be run before this.
	 *
	 * @param pipelineDir the (grand)parent directory to run the commands in
	 * @param event       specifies a subdirectory based on the repo name and head
	 *                    commit id
	 *
	 * @return the status of the compilation action
	 */
	public PipelineResult execute(String pipelineDir, PushEvent event) {
		var path = String.format("%s/repos/%s/%s", pipelineDir, event.repository.name, event.headCommit.id);
		var directory = new File(path);
		directory.mkdirs(); // Make the directories recursively

		try {
			var process = new ProcessBuilder(commands)
					.directory(directory)
					.redirectErrorStream(true)
					.start();

			// Here we have access to the output of the build, which we can send to GitHub.
			String output = IOUtils.toString(process.getInputStream(), StandardCharsets.UTF_8);
			System.err.println(output);

			int code = process.waitFor();
			var status = PipelineStatus.Ok;

			if (code != 0) {
				status = PipelineStatus.Fail;
			}

			return new PipelineResult(status, output);

		} catch (IOException e) {
			e.printStackTrace();
			return new PipelineResult(PipelineStatus.Fail, e);
		} catch (InterruptedException e) {
			e.printStackTrace();
			return new PipelineResult(PipelineStatus.Fail, e);
		}
	}
}
