package com.atp.hw4.task2;

import com.atp.hw4.task2.entities.RingProcessor;
import com.atp.hw4.task2.service.CsvFileWriter;
import com.atp.hw4.task2.service.factory.LinkedBlockingQueueNodeFactory;
import com.atp.hw4.task2.service.factory.NodeFactory;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Slf4j
public class Main {

    public static void main(String[] args) {
        List<Integer> nodeAmounts = List.of(3, 5, 10);
        List<Integer> packagesNumber = List.of(5, 20, 60, 100);
        int measurementNumber = 10;
        NodeFactory nodeFactory = new LinkedBlockingQueueNodeFactory();
        warmup(nodeFactory, 1000, 50);
        startMeasurements("token-ring-linked-queue_", nodeAmounts, packagesNumber, nodeFactory, measurementNumber);
//        startMeasurements("token-ring-test_", nodeAmounts, packagesNumber, new ArrayBlockingQueueFactory());
    }

    private static void warmup(NodeFactory nodeFactory, int iterationsNumber, long runTimeMs) {
        int nodesAmount = 2;
        int packagesPerNode = 5;
        int processPerTime = 1;
        log.info("Starting warmup");
        try {
            for (int i = 0; i < iterationsNumber; i++) {
                RingProcessor ringProcessor = new RingProcessor(nodesAmount,
                        packagesPerNode,
                        processPerTime,
                        nodeFactory.createNodes(nodesAmount, packagesPerNode, processPerTime)
                );
                ringProcessor.startProcessing();
                Thread.sleep(runTimeMs);
                ringProcessor.stop();
            }
        } catch (InterruptedException ex) {
            log.info("Caught exception during sleep", ex);
        }
        log.info("Warmup finished");
    }

    private static void startMeasurements(String prefix, List<Integer> nodeAmounts,
                                          List<Integer> packagesNumber,
                                          NodeFactory nodeFactory,
                                          int measurementNumber) {
        for (Integer nodeAmount : nodeAmounts) {
            for (Integer packagesPerNode : packagesNumber) {
                for (int i = 0; i < measurementNumber; i++) {
                    measureStats((i+1) + "_" + prefix, nodeAmount, packagesPerNode, packagesPerNode, nodeFactory);
                }
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
        int totalProcessingTimeMs = 240_000;

        sleep(totalProcessingTimeMs / 4);
        ringProcessor.resetStats();

        ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
        executorService.scheduleWithFixedDelay(new Thread(() -> saveStats(writer, ringProcessor)), 10, 10, TimeUnit.SECONDS);

        sleep(totalProcessingTimeMs);
        ringProcessor.stop();
        executorService.shutdown();
    }

    private static void sleep(int millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException ex) {
            log.info("Caught exception during sleep", ex);
        }
    }

    private static void saveStats(CsvFileWriter writer, RingProcessor ringProcessor) {
        writer.writeToFile(ringProcessor.getStats());
    }
}
