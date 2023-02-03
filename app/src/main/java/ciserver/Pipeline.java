package ciserver;

enum PipelineStatus {
	OK,
	NotStarted,
	InProgress
}

/**
 * A PipelineInstance is responsible for the whole CI-pipeline
 * for a specific commit.
 */
class Pipeline {
	String pipelineDir;
	Commit commit;
	PipelineStatus status;

	Pipeline(Commit commit, String pipelineDir) {
		this.commit = commit;
		this.pipelineDir = pipelineDir;
		this.status = PipelineStatus.NotStarted;
	}

	private PipelineStatus _start() {
		// The following pipeline process could
		// be refactored into something more ergonomic and elegant.
		// This will do for now.

		var status = pull();
		if (status != PipelineStatus.OK) {
			return status;
		}

		status = lint();
		if (status != PipelineStatus.OK) {
			return status;
		}

		status = compile();
		if (status != PipelineStatus.OK) {
			return status;
		}

		return PipelineStatus.OK;
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

	private PipelineStatus pull() {
		return PipelineStatus.OK;
	}

	private PipelineStatus lint() {
		return PipelineStatus.OK;
	}

	private PipelineStatus compile() {
		return PipelineStatus.OK;
	}
}