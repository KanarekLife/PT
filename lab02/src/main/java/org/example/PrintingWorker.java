package org.example;

public class PrintingWorker implements Runnable {
    private final ResultsQueue resultsQueue;

    public PrintingWorker(ResultsQueue resultsQueue) {
        this.resultsQueue = resultsQueue;
    }

    @Override
    public void run() {
        while (true) {
            var result = resultsQueue.getResult();
            if (result.isEmpty()) {
                return;
            }
            System.out.println("[✔] Result for n = " + result.get().number() + " is " + (result.get().isPrime() ? "prime" : "not prime") + " (calculated by worker " + result.get().workerId() + ")");
        }
    }
}
