package ciserver;

import java.io.PrintWriter;
import java.io.StringWriter;
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
    public PipelineResult execute(String pipelineDir, PushEvent event);
}

/**
 * a pipeline responsible for sending results for web loging functionality
 * 
 */
class PipelineResult {
    String output = "";
    PipelineStatus status = PipelineStatus.NotStarted;

    /**
     * status and exception to the web log in a readable format 
     *
     * @param status current status of the pipeline
     */
    PipelineResult(PipelineStatus status) {
        this.status = status;
    }

    /**
     * status and exception to the web log in a readable format 
     *
     * @param status current status of the pipeline
     * @param output a string of the current output of the pipeline
     */
    PipelineResult(PipelineStatus status, String output) {
        this.status = status;
        this.output = output;
    }

    /**
     * status and exception to the web log in a readable format 
     *
     * @param status current status of the pipeline
     * @param e the current exception that is thrown
     */
    PipelineResult(PipelineStatus status, Exception e) {
        var sw = new StringWriter();
        var pw = new PrintWriter(sw);
        e.printStackTrace(pw);

        this.status = status;
        this.output = sw.toString();
    }

    /**
     * appends two pipeline results
     *
     * @param header is the current header
     * @param result is a different pipeline result that is to be appended with the current one
     * @return the last pipeline result follorwed by the result of the curent pipeline result 
     */
    PipelineResult append(String header, PipelineResult result) {
        this.output += String.format("[%s]\n\n\t%s\n\n", header, result.output.replaceAll("\n", "\n\t"));
        this.status = result.status;

        return this;
    }
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
    PipelineCommandExecuter compiler = new PipelineCommandExecuter("/bin/sh", "gradlew", "build", "-x", "test");
    PipelineCommandExecuter tester = new PipelineCommandExecuter("/bin/sh", "gradlew", "test");

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
    public PipelineResult start(TargetStage target) {
        var result = new PipelineResult(PipelineStatus.NotImplemented);

        // No target
        if (target == TargetStage.NONE) {
            return result.append("NO TARGET", new PipelineResult(PipelineStatus.Ok));
        }

        // The reason status is redeclared in each stage is to prevent accidental usage
        // of a status from a different stage.

        // Pull
        {
            notifyObservers(TargetStage.PULL, PipelineStatus.InProgress);

            var nresult = puller.execute(pipelineDir, event);
            result = result.append("PULL", nresult);

            notifyObservers(TargetStage.PULL, result.status);
            if (result.status != PipelineStatus.Ok || target == TargetStage.PULL) {
                return result;
            }
        }

        // Lint
        {
            notifyObservers(TargetStage.LINT, PipelineStatus.InProgress);

            result = result.append("LINT", new PipelineResult(PipelineStatus.Ok));

            notifyObservers(TargetStage.LINT, result.status);
            if (result.status != PipelineStatus.Ok || target == TargetStage.LINT) {
                return result;
            }
        }

        // Compile
        {
            notifyObservers(TargetStage.COMPILE, PipelineStatus.InProgress);

            var nresult = compiler.execute(pipelineDir, event);
            result = result.append("COMPILE", nresult);

            notifyObservers(TargetStage.COMPILE, result.status);
            if (result.status != PipelineStatus.Ok || target == TargetStage.COMPILE) {
                return result;
            }
        }

        // Test
        {
            notifyObservers(TargetStage.TESTING, PipelineStatus.InProgress);

            var nresult = tester.execute(pipelineDir, event);
            result = result.append("TESTING", nresult);

            notifyObservers(TargetStage.TESTING, result.status);
            if (result.status != PipelineStatus.Ok || target == TargetStage.TESTING) {
                return result;
            }
        }

        return result;
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
