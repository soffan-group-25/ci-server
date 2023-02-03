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
	final String pipelineDir;
	final PushEvent event;
	PipelineStatus status;

	Pipeline(PushEvent event, String pipelineDir) {
		this.event = event;
		this.pipelineDir = pipelineDir;
		this.status = PipelineStatus.NotStarted;
	}

	private PipelineStatus _start() {
		// The following pipeline process could
		// be refactored into something more ergonomic and elegant.
		// This will do for now.

		var status = pull();
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
	 * Start the pipeline.
	 * 
	 * @return the status of the executed pipeline. OK if everything went ok.
	 */
	public PipelineStatus start() {
		this.status = PipelineStatus.InProgress;
		this.status = _start();

		return this.status;
	}

	/**
	 * Pulls the repository and checks out the head_commit
	 * 
	 * Inspiration from:
	 * https://github.com/centic9/jgit-cookbook/blob/master/src/main/java/org/dstadler/jgit/unfinished/PullRemoteRepository.java
	 * Can only pull from public repositories.
	 * 
	 * @return
	 */
	private PipelineStatus pull() {
		// Use the head_commit id as name for the repository directory
		String directoryPath = String.format("%s/repositories/%s", pipelineDir, event.headCommit.id);

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

	private PipelineStatus lint() {
		return PipelineStatus.NotImplemented;
	}

	private PipelineStatus compile() {
		return PipelineStatus.NotImplemented;
	}
}