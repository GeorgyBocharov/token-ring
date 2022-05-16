package com.atp.hw4.task2.entities.node;

import com.atp.hw4.task2.entities.DataPackage;

public interface Node {
    void setNextNodeInfo(WorkerNode nextNode);

    int getProcessedNumber();

    int getTotalLatency();

    int getPackageLoss();

    int getBufferSize();

    void addToBufferStack(DataPackage dataPackage);

    void stopProcessing();
}
