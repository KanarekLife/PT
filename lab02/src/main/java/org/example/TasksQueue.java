package org.example;

import java.util.LinkedList;
import java.util.Optional;
import java.util.Queue;

public class TasksQueue {
    private final Queue<Integer> numbersToCalculate = new LinkedList<>();

    public synchronized void addNumberToCalculate(int n) {
        numbersToCalculate.add(n);
        notify();
    }

    public synchronized Optional<Integer> getNumberToCalculate() {
        while (numbersToCalculate.isEmpty()) {
            try {
                wait();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return Optional.empty();
            }
        }
        return Optional.of(numbersToCalculate.poll());
    }
}
