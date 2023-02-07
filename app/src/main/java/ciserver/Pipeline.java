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

interface PipelineStage {
	public PipelineStatus execute(String pipelineDir, PushEvent event);
}

/**
 * A PipelineInstance is responsible for the whole CI-pipeline
 * for a specific commit.
 */
class Pipeline {

	/*
	 * Continue with PipelineLinter, PipelineCompiler
	 * Reason for this abstraction is easier testability,
	 * As one can individually test the pull, lint, compile functionality,
	 * without having a bunch of public methods in the Pipeline class.
	 */
	private ArrayList<PipelineStage> stages = new ArrayList<>();
	private final String pipelineDir;
	private final PushEvent event;

	Pipeline(PushEvent event, String pipelineDir) {
		this.event = event;
		this.pipelineDir = pipelineDir;
	}

	/**
	 * Add stages to the Pipeline.
	 *
	 * @param stages the stages to add
	 * @return the pipeline
	 */
	public Pipeline withComponent(PipelineStage... stages) {
		this.stages.addAll(Arrays.asList(stages));

		return this;
	}

	/**
	 * Start the pipeline with the added stages.
	 *
	 * @return the status of the pipeline execution
	 */
	public PipelineStatus start() {
		for (var component : stages) {
			var status = component.execute(pipelineDir, event);
			if (status != PipelineStatus.Ok) {
				return status;
			}
		}

		return PipelineStatus.Ok;
	}
}