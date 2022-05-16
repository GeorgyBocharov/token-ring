package com.atp.hw4.task2.service.factory;

import com.atp.hw4.task2.entities.DataPackage;
import com.atp.hw4.task2.entities.node.WorkerNode;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;

public interface NodeFactory {
    default List<WorkerNode> createNodes(int number, int packages, int processingPerTime) {
        List<WorkerNode> nodes = new ArrayList<>();
        for (int i = 0; i < number; i++) {
            nodes.add(createNode(i, packages, processingPerTime));
        }
        for (int i = 0; i < number - 1; i++) {
            nodes.get(i).setNextNodeInfo(nodes.get(i + 1));
        }
        nodes.get(number - 1).setNextNodeInfo(nodes.get(0));
        return nodes;
    }

    default WorkerNode createNode(int number, int packages, int processingPerTime) {
        return new WorkerNode(number, packages, processingPerTime, getQueue());
    }

    BlockingQueue<DataPackage> getQueue();
}
