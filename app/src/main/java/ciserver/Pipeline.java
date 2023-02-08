package ciserver;

import java.util.ArrayList;
import java.util.List;

enum PipelineStatus {
	Ok,
	Fail,
	NotImplemented,
	NotStarted,
	InProgress
}

enum TargetStage {
	PULL,
	LINT,
	COMPILE,
	TESTING,
	ALL
}

interface PipelineObserver {
	public void update(TargetStage stage, PipelineStatus status);
}

interface StageTask {
	public PipelineStatus execute(String pipelineDir, PushEvent event);
}

/**
 * A PipelineInstance is responsible for the whole CI-pipeline
 * for a specific commit.
 */
class Pipeline {

	private final String pipelineDir;
	private final PushEvent event;
	private List<PipelineObserver> observers = new ArrayList<PipelineObserver>();
	PipelinePuller puller = new PipelinePuller();
	PipelineCompiler compiler = new PipelineCompiler("/bin/sh", "gradlew", "build", "-x", "test");

	Pipeline(PushEvent event, String pipelineDir) {
		this.event = event;
		this.pipelineDir = pipelineDir;
	}

	/**
	 * Start the pipeline.
	 *
	 * @param target is what stage the pipeline should target.
	 * @return the status of the executed pipeline. OK if everything went ok.
	 */
	public PipelineStatus start(TargetStage target) {

		// Pull
		var status = pull();
		notifyObservers(TargetStage.PULL, status);
		if (status != PipelineStatus.Ok || target == TargetStage.PULL) {
			return status;
		}

		// Lint
		status = lint();
		notifyObservers(TargetStage.LINT, status);
		if (status != PipelineStatus.Ok || target == TargetStage.LINT) {
			return status;
		}

		// Compile
		status = compile();
		notifyObservers(TargetStage.COMPILE, status);
		if (status != PipelineStatus.Ok || target == TargetStage.COMPILE) {
			return status;
		}

		// Test
		status = test();
		notifyObservers(TargetStage.TESTING, status);
		if (status != PipelineStatus.Ok || target == TargetStage.TESTING) {
			return status;
		}

		return PipelineStatus.Ok;
	}

	private PipelineStatus pull() {
		notifyObservers(TargetStage.PULL, PipelineStatus.InProgress);

		return puller.execute(pipelineDir, event);
	}

	private PipelineStatus lint() {
		notifyObservers(TargetStage.LINT, PipelineStatus.InProgress);

		return PipelineStatus.Ok; // todo: implement linting
	}

	private PipelineStatus compile() {
		notifyObservers(TargetStage.COMPILE, PipelineStatus.InProgress);

		return compiler.execute(pipelineDir, event);
	}

	private PipelineStatus test() {
		notifyObservers(TargetStage.TESTING, PipelineStatus.InProgress);

		return PipelineStatus.Ok; // todo: implement testing
	}

	public void addObserver(PipelineObserver observer) {
		observers.add(observer);
	}

	public void removeObserver(PipelineObserver observer) {
		observers.remove(observer);
	}

	public void notifyObservers(TargetStage stage, PipelineStatus status) {
		for (PipelineObserver observer : observers) {
			observer.update(stage, status);
		}
	}
}