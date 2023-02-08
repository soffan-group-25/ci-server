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
	NONE,
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
		// No target
		if (target == TargetStage.NONE) {
			return PipelineStatus.Ok;
		}

		// The reason status is redeclared in each stage is to prevent accidental usage
		// of a status from a different stage.

		// Pull
		{
			notifyObservers(TargetStage.PULL, PipelineStatus.InProgress);

			// Code goes here
			var status = puller.execute(pipelineDir, event);

			notifyObservers(TargetStage.PULL, status);
			if (status != PipelineStatus.Ok || target == TargetStage.PULL) {
				return status;
			}
		}

		// Lint
		{
			notifyObservers(TargetStage.LINT, PipelineStatus.InProgress);

			// Code goes here
			var status = PipelineStatus.Ok;

			notifyObservers(TargetStage.LINT, status);
			if (status != PipelineStatus.Ok || target == TargetStage.LINT) {
				return status;
			}
		}

		// Compile
		{
			notifyObservers(TargetStage.COMPILE, PipelineStatus.InProgress);

			// Code goes here
			var status = compiler.execute(pipelineDir, event);

			notifyObservers(TargetStage.COMPILE, status);
			if (status != PipelineStatus.Ok || target == TargetStage.COMPILE) {
				return status;
			}
		}

		// Test
		{
			notifyObservers(TargetStage.TESTING, PipelineStatus.InProgress);

			// Code goes here
			var status = PipelineStatus.NotImplemented;

			notifyObservers(TargetStage.TESTING, status);
			if (status != PipelineStatus.Ok || target == TargetStage.TESTING) {
				return status;
			}
		}

		// Notify
		{
			notifyObservers(TargetStage.NOTIFICATION, PipelineStatus.InProgress);

			// Code goes here
			var status = PipelineStatus.NotImplemented;

			notifyObservers(TargetStage.NOTIFICATION, status);
			if (status != PipelineStatus.Ok || target == TargetStage.NOTIFICATION) {
				return status;
			}
		}

		return PipelineStatus.Ok;
	}

	/**
	 * Adds an observer to the pipeline.
	 *
	 * @param observer is the observer to notify.
	 */
	public void addObserver(PipelineObserver observer) {
		observers.add(observer);
	}

	/**
	 * Unsubscribes the given observer.
	 *
	 * @param observer the observer to unsubscribe.
	 */
	public void removeObserver(PipelineObserver observer) {
		observers.remove(observer);
	}

	/**
	 * Notifies all the observers with a stage and status.
	 *
	 * @param stage  is the stage to notify about.
	 * @param status is the status to notify about.
	 */
	public void notifyObservers(TargetStage stage, PipelineStatus status) {
		for (PipelineObserver observer : observers) {
			observer.update(stage, status);
		}
	}
}