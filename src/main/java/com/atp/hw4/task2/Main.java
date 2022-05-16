package com.atp.hw4.task2;

import com.atp.hw4.task2.entities.RingProcessor;
import com.atp.hw4.task2.service.CsvFileWriter;
import com.atp.hw4.task2.service.factory.ArrayBlockingQueueFactory;
import com.atp.hw4.task2.service.factory.LinkedBlockingQueueNodeFactory;
import com.atp.hw4.task2.service.factory.NodeFactory;

import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Main {

    public static void main(String[] args) {
        List<Integer> nodeAmounts = List.of(3, 5, 10);
        List<Integer> packagesNumber = List.of(5, 20, 60, 100);
        startMeasurements("token-ring-test_", nodeAmounts, packagesNumber, new ArrayBlockingQueueFactory());
        startMeasurements("token-ring-linked-queue_", nodeAmounts, packagesNumber, new LinkedBlockingQueueNodeFactory());
    }

    private static void startMeasurements(String prefix, List<Integer> nodeAmounts,
                                          List<Integer> packagesNumber,
                                          NodeFactory nodeFactory) {
        for (Integer nodeAmount : nodeAmounts) {
            for (Integer packagesPerNode : packagesNumber) {
                measureStats(prefix, nodeAmount, packagesPerNode, packagesPerNode, nodeFactory);
            }
        }
    }

    private static void measureStats(String prefix, int nodesAmount, int packagesPerNode, int processPerTime, NodeFactory nodeFactory) {
        CsvFileWriter writer = new CsvFileWriter(prefix);
        RingProcessor ringProcessor = new RingProcessor(nodesAmount,
                packagesPerNode,
                processPerTime,
                nodeFactory.createNodes(nodesAmount, packagesPerNode, processPerTime)
        );
        ringProcessor.startProcessing();

        ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
        executorService.scheduleWithFixedDelay(new Thread(() -> saveStats(writer, ringProcessor)), 20, 10, TimeUnit.SECONDS);

        try {
            Thread.sleep(75_000);
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
