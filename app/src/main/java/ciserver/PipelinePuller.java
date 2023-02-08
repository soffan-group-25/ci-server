package ciserver;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;

public class PipelinePuller implements TargetStage {

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
		// Use the head_commit id as name for the repository directory
		String directoryPath = String.format("%s/%s/%s", pipelineDir, event.repository.name, event.headCommit.id);

		try {
			File directory = new File(directoryPath);
			directory.mkdirs(); // Make the directories recursively
			FileUtils.deleteDirectory(directory); // If there already is a repository, delete it

			Git.cloneRepository()
					.setURI(event.repository.cloneUrl)
					.setDirectory(directory)
					.setBranch(event.ref)
					.call() // Clone the repository
					.checkout()
					.setName(event.headCommit.id)
					.call(); // Checkout the commit

		} catch (GitAPIException e) {
			e.printStackTrace();
			return PipelineStatus.Fail;
		} catch (IOException e) {
			e.printStackTrace();
			return PipelineStatus.Fail;
		}

		return PipelineStatus.Ok;
	}
}
