package org.example;

import java.util.Optional;
import java.util.random.RandomGenerator;

public class PiCalculationWorker implements Runnable {
    private final TasksQueue tasksQueue;
    private final ResultsQueue resultsQueue;

    public PiCalculationWorker(TasksQueue tasksQueue, ResultsQueue resultsQueue) {
        this.tasksQueue = tasksQueue;
        this.resultsQueue = resultsQueue;
    }

    @Override
    public void run() {
        while (true) {
            long i = 1;
            double result = 0;
            Optional<CalculatePiCommand> command = Optional.empty();
            try {
                command = tasksQueue.getCalculatePiCommand();
                if (command.isEmpty()) {
                    return;
                }

               // System.out.printf("[i] worker picked up taskId = %d%n", command.get().taskId());

                //Thread.sleep(RandomGenerator.getDefault().nextInt((7000 - 1500) + 1) + 1500);

                for (; i < command.get().n(); i++) {
                    result += Math.pow(-1, i-1) / ((2 * i) - 1);
                    Thread.sleep(200);
                }

                resultsQueue.addResult(CalculatePiCommandResult.FromSuccess(command.get().taskId(), result * 4));
            }catch (InterruptedException e) {
                var commandResult = CalculatePiCommandResult.FromTermination(command.get().taskId(), (long)(((double)i / command.get().n())*100), result * 4);
                resultsQueue.addResult(commandResult);
                Thread.currentThread().interrupt();
                break;
            }
        }
    }
}
