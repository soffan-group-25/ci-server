package ciserver;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;

import org.apache.commons.io.IOUtils;

public class PipelineCompiler implements PipelineComponent {
	public final ArrayList<String> commands = new ArrayList<>();

	PipelineCompiler(String... commands) {
		this.commands.addAll(Arrays.asList(commands));
	}

	/**
	 * Pulls the repository and checks out the head_commit
	 * 
	 * Inspiration from:
	 * https://github.com/centic9/jgit-cookbook/blob/master/src/main/java/org/dstadler/jgit/unfinished/PullRemoteRepository.java
	 * Can only pull from public repositories.
	 * 
	 * @return the status of the pull action
	 */
	public PipelineStatus execute(String pipelineDir, PushEvent event) {
		var path = String.format("%s/repositories/%s", pipelineDir, event.headCommit.id);
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

			process.waitFor();
		} catch (IOException e) {
			e.printStackTrace();
			return PipelineStatus.Fail;
		} catch (InterruptedException e) {
			e.printStackTrace();
			return PipelineStatus.Fail;
		}

		return PipelineStatus.Ok;
	}
}
