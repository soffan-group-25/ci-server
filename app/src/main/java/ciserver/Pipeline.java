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
	NOTIFICATION,
	ALL
}

interface PipelineObserver {
	public void update(TargetStage stage, PipelineStatus status);
}

/**
 * A PipelineInstance is responsible for the whole CI-pipeline
 * for a specific commit.
 */
class Pipeline {

	String pipelineDir;
	Commit commit;
	List<PipelineObserver> observers = new ArrayList<PipelineObserver>();

	Pipeline(Commit commit, String pipelineDir) {
		this.commit = commit;
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

		// Notify
		status = nootify();
		notifyObservers(TargetStage.NOTIFICATION, status);
		if (status != PipelineStatus.Ok || target == TargetStage.NOTIFICATION) {
			return status;
		}

		return PipelineStatus.Ok;
	}

	private PipelineStatus pull() {
		notifyObservers(TargetStage.PULL, PipelineStatus.InProgress);
		var status = PipelineStatus.NotImplemented;

		// Code goes here

		return status;
	}

	private PipelineStatus lint() {
		notifyObservers(TargetStage.LINT, PipelineStatus.InProgress);
		var status = PipelineStatus.Ok;

		// Code goes here

		return status;
	}

	private PipelineStatus compile() {
		notifyObservers(TargetStage.COMPILE, PipelineStatus.InProgress);
		var status = PipelineStatus.NotImplemented;

		// Code goes here

		return status;
	}

	private PipelineStatus test() {
		notifyObservers(TargetStage.NOTIFICATION, PipelineStatus.InProgress);
		var status = PipelineStatus.NotImplemented;

		// Code goes here

		return status;
	}

	// "notify" conflicts with Object.notify(). Think of Pingu instead! Noot noot!
	private PipelineStatus nootify() {
		notifyObservers(TargetStage.NOTIFICATION, PipelineStatus.InProgress);
		var status = PipelineStatus.NotImplemented;

		// Code goes here

		return status;
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