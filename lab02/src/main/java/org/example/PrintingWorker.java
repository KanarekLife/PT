package org.example;

public class PrintingWorker implements Runnable {
    private final ResultsQueue resultsQueue;

    public PrintingWorker(ResultsQueue resultsQueue) {
        this.resultsQueue = resultsQueue;
    }

    @Override
    public void run() {
        while (true) {
            try {
                Thread.sleep(1);
                var result = resultsQueue.getResult();
                if (result.isEmpty()) {
                    continue;
                }
                System.out.println(result.get().toString());
            } catch(InterruptedException ex) {
                while (resultsQueue.any()) {
                    var result = resultsQueue.getResult();
                    if (result.isEmpty()) {
                        System.out.println("Exiting printer worker");
                        Thread.currentThread().interrupt();
                        break;
                    }
                    System.out.println(result.get().toString());
                }
                break;
            }
        }
    }
}
