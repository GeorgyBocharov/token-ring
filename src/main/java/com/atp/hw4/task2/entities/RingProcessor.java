package com.atp.hw4.task2.entities;

import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

@Slf4j
public class RingProcessor {
    private final int nodesAmount;
    private final int packagesPerNode;
    private final int processPerTime;
    private final List<WorkerNode> nodes = new ArrayList<>();
    private final AtomicBoolean processingFlag = new AtomicBoolean(false);
    private long startTime;

    public RingProcessor(int nodesAmount, int packagesPerNode, int processPerTime, BlockingQueue<DataPackage> stack) {
        this.nodesAmount = nodesAmount;
        this.packagesPerNode = packagesPerNode;
        this.processPerTime = processPerTime;
        for (int i = 0; i < nodesAmount; i++) {
            nodes.add(new WorkerNode(i, processingFlag, packagesPerNode, processPerTime, stack));
        }
        for (int i = 0; i < nodesAmount - 1; i++) {
            nodes.get(i).setNextNodeInfo(nodes.get(i + 1));
        }
        nodes.get(nodesAmount - 1).setNextNodeInfo(nodes.get(0));
    }

    public void startProcessing() {
        processingFlag.set(true);
        startTime = System.currentTimeMillis();
        nodes.forEach(Thread::start);
    }



    public Statistics getStats() {
        int totalLatency = 0;
        int totalProcessedPackages = 0;
        int totalLoss = 0;
        double totalThroughput = 0;
        for (Node node: nodes) {
            totalProcessedPackages +=  node.getProcessedNumber();
            totalLatency += node.getTotalLatency();
            totalLoss += node.getPackageLoss();
            totalThroughput += (double) node.getProcessedNumber() / (System.currentTimeMillis() - startTime);
        }
        long passedTime = System.currentTimeMillis() - startTime;
        return Statistics.builder()
                .nodeNumber(nodesAmount)
                .packagesPerNode(packagesPerNode)
                .processPerTime(processPerTime)
                .totalLatencyMs(totalLatency)
                .processedPackages(totalProcessedPackages)
                .totalLoss(totalLoss)
                .passedTimeMs(passedTime)
                .averageLatency(totalLatency / (double) totalProcessedPackages)
                .throughput(totalThroughput / nodesAmount)
                .build();
    }

    public void stop() {
        processingFlag.set(false);
        for (WorkerNode node: nodes) {
            try {
                node.join();
            } catch (InterruptedException ex) {
                log.error("Failed to join node {}", node, ex);
            }
        }
    }

}
