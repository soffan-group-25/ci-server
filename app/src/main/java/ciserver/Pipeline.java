package ciserver;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;

enum PipelineStatus {
	Ok,
	Fail,
	NotImplemented,
	NotStarted,
	InProgress
}

/**
 * A PipelineInstance is responsible for the whole CI-pipeline
 * for a specific commit.
 */
class Pipeline {

	/**
	 * Start the pipeline.
	 *
	 * @return the status of the executed pipeline. OK if everything went ok.
	 */
	public static PipelineStatus start(PushEvent event, String pipelineDir) {
		String directoryPath = String.format("%s/repositories/%s", pipelineDir, event.headCommit.id);

		var status = pull(event, directoryPath);
		if (status != PipelineStatus.Ok) {
			return status;
		}

		status = lint();
		if (status != PipelineStatus.Ok) {
			return status;
		}

		status = compile();
		if (status != PipelineStatus.Ok) {
			return status;
		}

		return PipelineStatus.Ok;
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
	private static PipelineStatus pull(PushEvent event, String directoryPath) {
		// Use the head_commit id as name for the repository directory
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

	private static PipelineStatus lint() {
		return PipelineStatus.NotImplemented;
	}

	private static PipelineStatus compile() {
		return PipelineStatus.NotImplemented;
	}
}