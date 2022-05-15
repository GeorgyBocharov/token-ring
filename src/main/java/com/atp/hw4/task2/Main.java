package com.atp.hw4.task2;

import com.atp.hw4.task2.entities.DataPackage;
import com.atp.hw4.task2.entities.RingProcessor;
import com.atp.hw4.task2.writer.CsvFileWriter;

import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Main {

    public static void main(String[] args) {
        List<Integer> nodeAmounts = List.of(3, 10, 20);
        List<Integer> packagesNumber = List.of(5, 50, 100);
        List<Integer> processPerTimeList = List.of(30, 80);
//        startMeasurements("token-ring-test_", nodeAmounts, packagesNumber, processPerTimeList, new ArrayBlockingQueue<>(300));
        startMeasurements("token-ring-linked-queue_", nodeAmounts, packagesNumber, processPerTimeList, new LinkedBlockingQueue<>());
    }

    private static void startMeasurements(String prefix, List<Integer> nodeAmounts,
                                          List<Integer> packagesNumber, List<Integer> processPerTimeList,
                                          BlockingQueue<DataPackage> stack) {
        CsvFileWriter writer = new CsvFileWriter(prefix);
        for (Integer nodeAmount : nodeAmounts) {
            for (Integer packagesPerNode : packagesNumber) {
                measureStats(writer, nodeAmount, packagesPerNode, 1, stack);
            }
        }
        for (Integer processPerTime : processPerTimeList) {
            measureStats(writer, 10, 100, processPerTime, stack);
        }
    }

    private static void measureStats(CsvFileWriter writer, int nodesAmount, int packagesPerNode, int processPerTime, BlockingQueue<DataPackage> stack) {
        RingProcessor ringProcessor = new RingProcessor(nodesAmount, packagesPerNode, processPerTime, stack);
        ringProcessor.startProcessing();

        ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
        executorService.scheduleWithFixedDelay(new Thread(() -> saveStats(writer, ringProcessor)), 20, 10, TimeUnit.SECONDS);

        try {
            Thread.sleep(150_000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        ringProcessor.stop();
        executorService.shutdown();
    }

    private static void saveStats(CsvFileWriter writer, RingProcessor ringProcessor) {
        writer.writeToFile(ringProcessor.getStats());
    }
}
