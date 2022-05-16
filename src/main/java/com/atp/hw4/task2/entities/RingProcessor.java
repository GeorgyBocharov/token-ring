package com.atp.hw4.task2.entities;

import com.atp.hw4.task2.entities.node.Node;
import com.atp.hw4.task2.entities.node.WorkerNode;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Slf4j
public class RingProcessor {
    private final int nodesAmount;
    private final int packagesPerNode;
    private final int processPerTime;
    private final List<WorkerNode> nodes;
    private long startTime;

    public RingProcessor(int nodesAmount, int packagesPerNode, int processPerTime, List<WorkerNode> nodes) {
        log.info("Creating processor with nodeNumber {}, packagesPerNode {}", nodesAmount, packagesPerNode);
        this.nodesAmount = nodesAmount;
        this.packagesPerNode = packagesPerNode;
        this.processPerTime = processPerTime;
        this.nodes = nodes;
    }

    public void startProcessing() {
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
        nodes.forEach(WorkerNode::stopProcessing);
        for (WorkerNode node: nodes) {
            try {
                node.join();
            } catch (InterruptedException ex) {
                log.error("Failed to join node {}", node, ex);
            }
        }
        Statistics finalStats = getStats();
        int size = nodes.stream().map(WorkerNode::getBufferSize).reduce(Integer::sum).orElse(-1);
        log.info("After stop packageLoss = {}, totalPackagesSize = {}", finalStats.getTotalLoss(), size);
    }

}
