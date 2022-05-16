package com.atp.hw4.task2.service.factory;

import com.atp.hw4.task2.entities.DataPackage;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class ArrayBlockingQueueFactory implements NodeFactory {

    @Override
    public BlockingQueue<DataPackage> getQueue() {
        return new ArrayBlockingQueue<>(300);
    }
}
