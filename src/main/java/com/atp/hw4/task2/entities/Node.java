package com.atp.hw4.task2.entities;

public interface Node {
    void setNextNodeInfo(WorkerNode nextNode);

    int getNodeId();

    int getProcessedNumber();

    int getTotalLatency();

    int getPackageLoss();

    void addToBufferStack(DataPackage dataPackage);
}
