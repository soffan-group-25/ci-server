package ciserver;

import java.util.ArrayList;
import java.util.Arrays;

enum PipelineStatus {
	Ok,
	Fail,
	NotImplemented,
	NotStarted,
	InProgress
}

interface PipelineComponent {
	public PipelineStatus execute(String pipelineDir, PushEvent event);
}

/**
 * A PipelineInstance is responsible for the whole CI-pipeline
 * for a specific commit.
 */
class Pipeline {
	final String pipelineDir;
	final PushEvent event;
	PipelineStatus status;

	/*
	 * Continue with PipelineLinter, PipelineCompiler
	 * Reason for this abstraction is easier testability,
	 * As one can individually test the pull, lint, compile functionality,
	 * without having a bunch of public methods in the Pipeline class.
	 */
	ArrayList<PipelineComponent> components = new ArrayList<>();

	Pipeline(PushEvent event, String pipelineDir) {
		this.event = event;
		this.pipelineDir = pipelineDir;
	}

	/**
	 * Add components to the Pipeline.
	 * 
	 * @param components the components to add
	 * @return the pipeline
	 */
	public Pipeline withComponent(PipelineComponent... components) {
		this.components.addAll(Arrays.asList(components));

		return this;
	}

	private PipelineStatus _start() {
		for (var component : components) {
			var status = component.execute(pipelineDir, event);
			if (status != PipelineStatus.Ok) {
				return status;
			}
		}

		return PipelineStatus.Ok;
	}

	/**
	 * Start the pipeline with the added components.
	 * 
	 * @return the status of the pipeline execution
	 */
	public PipelineStatus start() {
		this.status = PipelineStatus.InProgress;
		this.status = _start();

		return this.status;
	}
}