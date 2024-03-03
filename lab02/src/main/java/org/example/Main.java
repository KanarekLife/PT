package org.example;

import java.util.Scanner;
import java.util.stream.IntStream;

public class Main {
    public static void main(String[] args) {
        var tasksQueue = new TasksQueue();
        var resultsQueue = new ResultsQueue();

        var numberOfWorkers = args.length > 0 ? Integer.parseInt(args[0]) : 5;
        var workers = IntStream.range(0, numberOfWorkers+1)
                .mapToObj(i -> new PrimerCheckerWorker(i, tasksQueue, resultsQueue))
                .map(Thread::new)
                .peek(Thread::start)
                .toArray(Thread[]::new);

        var printingWorker = new Thread(new PrintingWorker(resultsQueue));
        printingWorker.start();

        var isRunning = true;
        var scanner = new Scanner(System.in);

        // Schedule some random numbers to be calculated
        for (var number : new java.util.Random().ints(5, 2, 15001).toArray()) {
            tasksQueue.addNumberToCalculate(number);
            System.out.println("[+] Added " + number + " to the queue");
        }

        while(isRunning) {
            var command = scanner.nextLine();
            if (command.equals("exit")) {
                isRunning = false;
                for (var worker : workers) {
                    worker.interrupt();
                }
                printingWorker.interrupt();
            }else {
                try {
                    var number = Integer.parseInt(command);
                    tasksQueue.addNumberToCalculate(number);
                    System.out.println("[+] Added " + number + " to the queue");
                } catch (NumberFormatException e) {
                    System.out.println("[!] Please enter a valid number or 'exit'");
                }
            }
        }
    }
}