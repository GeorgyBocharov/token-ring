package com.atp.hw4.task2.entities;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
public class WorkerNode extends Thread implements Node {
    public static final int POLL_TIMEOUT_MS = 500;

    private final int nodeId;
    private final String logPrefix;
    private final AtomicBoolean processingFlag;
    private final BlockingQueue<DataPackage> bufferStack;

    private final AtomicInteger processedPackages = new AtomicInteger(0);
    private final AtomicInteger packageTotalLatency = new AtomicInteger(0);
    private final AtomicInteger packageLoss = new AtomicInteger(0);

    private final int processingPerTime;

    private WorkerNode nextNode;

    WorkerNode(int nodeId, AtomicBoolean processingFlag, int packageNumber, int processingPerTime, BlockingQueue<DataPackage> stack) {
        this.nodeId = nodeId;
        this.bufferStack = stack;
        this.processingFlag = processingFlag;
        this.processingPerTime = processingPerTime;
        for (int i = 0; i < packageNumber; i++) {
            bufferStack.add(new DataPackage());
        }
        this.logPrefix = String.format("Node[%d]", nodeId);
    }

    @Override
    public void setNextNodeInfo(WorkerNode nextNode) {
        this.nextNode = nextNode;
    }

    @Override
    public int getNodeId() {
        return nodeId;
    }

    @Override
    public void addToBufferStack(DataPackage dataPackage) {
        log.debug("Saving data {} in bufferStack {} of {}", dataPackage, bufferStack, logPrefix);
        boolean result = bufferStack.offer(dataPackage);
        if (!result) {
            packageLoss.incrementAndGet();
            log.error(
                    "Failed to add data, buffer stack overflowed. Sending data to coordinator: {}",
                    dataPackage
            );
        }
    }

    @Override
    public int getProcessedNumber() {
        return processedPackages.get();
    }

    @Override
    public int getTotalLatency() {
        return packageTotalLatency.get();
    }

    @Override
    public int getPackageLoss() {
        return packageLoss.get();
    }

    @Override
    public void run() {
        while (processingFlag.get()) {
            process();
        }
    }

    private void process() {
        DataPackage dataPackage;
        try {
            dataPackage = bufferStack.poll(POLL_TIMEOUT_MS, TimeUnit.MILLISECONDS);
        } catch (InterruptedException ex) {
            log.error("{} Caught interrupted exception during taking from blocking queue", logPrefix, ex);
            return;
        }
        if (dataPackage == null) {
            return;
        }
        sendToNextNode(dataPackage);
        for (int i = 1; i < processingPerTime; i++) {
            dataPackage = bufferStack.poll();
            if (dataPackage == null) {
                return;
            }
            sendToNextNode(dataPackage);
        }
    }

    private void sendToNextNode(DataPackage dataPackage) {
        if (dataPackage.getTransitionStartTime() != 0) {
            packageTotalLatency.addAndGet((int) (System.currentTimeMillis() - dataPackage.getTransitionStartTime()));
        }
        dataPackage.transit();
        log.debug("{} Forwarding data {} to node[{}]", logPrefix, dataPackage, nextNode.getNodeId());
        processedPackages.incrementAndGet();
        nextNode.addToBufferStack(dataPackage);
    }
}