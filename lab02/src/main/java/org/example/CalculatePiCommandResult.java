package org.example;

public class CalculatePiCommandResult {
    private final int taskId;
    private final long percentOfCompletion;
    private final boolean isCompleted;
    private final double value;


    private CalculatePiCommandResult(int taskId, long percentOfCompletion, double value) {
        this.taskId = taskId;
        this.isCompleted = percentOfCompletion == 100;
        this.percentOfCompletion = percentOfCompletion;
        this.value = value;
    }

    public static CalculatePiCommandResult FromSuccess(int taskId, double result) {
        return new CalculatePiCommandResult(taskId, 100, result);
    }

    public static CalculatePiCommandResult FromTermination(int taskId, long percentOfCompletion, double currentResult) {
        return new CalculatePiCommandResult(taskId, percentOfCompletion, currentResult);
    }

    @Override
    public String toString() {
        return "[%d] returned %f with %d percent completion (%b)".formatted(taskId, value, percentOfCompletion, isCompleted);
    }
}
