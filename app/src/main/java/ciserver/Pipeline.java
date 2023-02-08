package ciserver;

enum PipelineStatus {
	Ok,
	Fail,
	NotImplemented,
	NotStarted,
	InProgress
}

enum Target {
	PULL,
	LINT,
	COMPILE,
	TESTING,
	NOTIFICATION,
	ALL
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

	/**
	 * Start the pipeline.
	 *
	 * @return the status of the executed pipeline. OK if everything went ok.
	 */
	public PipelineStatus start(Target target) {

		// Pull
		var status = pull();
		if (status != PipelineStatus.Ok || target == Target.PULL) {
			return status;
		}

		// Lint
		status = lint();
		if (status != PipelineStatus.Ok || target == Target.LINT) {
			return status;
		}

		// Compile
		status = compile();
		if (status != PipelineStatus.Ok || target == Target.COMPILE) {
			return status;
		}

		// Test
		status = test();
		if (status != PipelineStatus.Ok || target == Target.TESTING) {
			return status;
		}

		// Notify
		status = nootify();
		if (status != PipelineStatus.Ok || target == Target.NOTIFICATION) {
			return status;
		}

		return PipelineStatus.Ok;
	}

	private PipelineStatus pull() {
		return PipelineStatus.NotImplemented;
	}

	private PipelineStatus lint() {
		return PipelineStatus.NotImplemented;
	}

	private PipelineStatus compile() {
		return PipelineStatus.NotImplemented;
	}

	private PipelineStatus test() {
		return PipelineStatus.NotImplemented;
	}

	// "notify" conflicts with Object.notify(). Think of Pingu instead! Noot noot!
	private PipelineStatus nootify() {
		return PipelineStatus.NotImplemented;
	}
}