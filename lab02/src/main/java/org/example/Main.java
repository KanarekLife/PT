package org.example;

import java.util.Arrays;
import java.util.Scanner;
import java.util.stream.IntStream;

public class Main {
    public static void main(String[] args) {
        var tasksQueue = new TasksQueue();
        var resultsQueue = new ResultsQueue();

        var numberOfWorkers = args.length > 0 ? Integer.parseInt(args[0]) : 5;
        var workers = IntStream.range(0, numberOfWorkers+1)
                .mapToObj(i -> new PiCalculationWorker(tasksQueue, resultsQueue))
                .map(Thread::new)
                .peek(Thread::start)
                .toArray(Thread[]::new);

        var printingWorker = new Thread(new PrintingWorker(resultsQueue));
        printingWorker.start();

        var isRunning = true;
        var scanner = new Scanner(System.in);

        tasksQueue.schedulePiCalculation(45);
        System.out.println("[+] Added " + 900 + " to the queue");
        // Schedule some random numbers to be calculated
        for (var number : new java.util.Random().longs(5, 2, 500).toArray()) {
            tasksQueue.schedulePiCalculation(number);
            System.out.println("[+] Added " + number + " to the queue");
        }


        while(isRunning) {
            var command = scanner.nextLine();
            if (command.equals("exit")) {
                isRunning = false;
                for (var worker : workers) {
                    worker.interrupt();
                }
                while(Arrays.stream(workers).anyMatch(Thread::isAlive)) {
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
                printingWorker.interrupt();
            }else {
                try {
                    var number = Integer.parseInt(command);
                    tasksQueue.schedulePiCalculation(number);
                    System.out.println("[+] Added " + number + " to the queue");
                } catch (NumberFormatException e) {
                    System.out.println("[!] Please enter a valid number or 'exit'");
                }
            }
        }
    }
}