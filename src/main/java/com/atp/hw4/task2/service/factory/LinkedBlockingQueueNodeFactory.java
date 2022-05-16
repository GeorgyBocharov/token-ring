package com.atp.hw4.task2.service.factory;

import com.atp.hw4.task2.entities.DataPackage;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class LinkedBlockingQueueNodeFactory implements NodeFactory {

    @Override
    public BlockingQueue<DataPackage> getQueue() {
        return new LinkedBlockingQueue<>();
    }
}
