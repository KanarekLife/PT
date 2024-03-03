package org.example;

import java.util.random.RandomGenerator;

public class PrimerCheckerWorker implements Runnable {
    private final int workerId;
    private final TasksQueue tasksQueue;
    private final ResultsQueue resultsQueue;

    public PrimerCheckerWorker(int workerId, TasksQueue tasksQueue, ResultsQueue resultsQueue) {
        this.workerId = workerId;
        this.tasksQueue = tasksQueue;
        this.resultsQueue = resultsQueue;
    }

    @Override
    public void run() {
        while(true) {
            try {
                var task = tasksQueue.getNumberToCalculate();
                if (task.isEmpty()) {
                    return;
                }
                System.out.println("[i] Worker " + workerId + " is processing n = " + task.get());
                Thread.sleep(RandomGenerator.getDefault().nextInt((7000 - 1500) + 1) + 1500);
                var result = new Result(task.get(), isPrime(task.get()), workerId);
                resultsQueue.addResult(result);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
    }

    private boolean isPrime(int n) {
        if (n <= 1) {
            return false;
        }
        for (int i = 2; i <= Math.sqrt(n); i++) {
            if (n % i == 0) {
                return false;
            }
        }
        return true;
    }
}
