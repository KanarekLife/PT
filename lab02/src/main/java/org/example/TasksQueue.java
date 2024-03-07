package org.example;

import java.util.LinkedList;
import java.util.Optional;
import java.util.Queue;

public class TasksQueue {
    private final Queue<CalculatePiCommand> commands = new LinkedList<>();
    private int _lastTaskId = 0;

    public synchronized void schedulePiCalculation(long n) {
        commands.add(new CalculatePiCommand(_lastTaskId, n));
        _lastTaskId++;
        notify();
    }

    public synchronized Optional<CalculatePiCommand> getCalculatePiCommand() {
        while (commands.isEmpty()) {
            try {
                wait();
            } catch (InterruptedException ex) {
                Thread.currentThread().interrupt();
                return Optional.empty();
            }
        }
        return Optional.of(commands.poll());
    }
}
