package org.example;

import java.util.Optional;
import java.util.Queue;
import java.util.LinkedList;

public class ResultsQueue {
    private final Queue<Result> results = new LinkedList<>();

    public synchronized void addResult(Result result) {
        results.add(result);
        notify();
    }

    public synchronized Optional<Result> getResult() {
        while (results.isEmpty()) {
            try {
                wait();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return Optional.empty();
            }
        }
        return Optional.of(results.poll());
    }
}
