package com.atp.hw4.task2.entities;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.util.List;

@Builder
@Getter
@ToString
public class Statistics {
    private final int nodeNumber;
    private final int packagesPerNode;
    private final int processedPackages;
    private final int processPerTime;
    private final int totalLoss;
    private final long totalLatencyMs;
    private final long passedTimeMs;
    private final double averageLatency;
    private final double throughput;

    public List<String> toCsvData() {
        return List.of(
                Long.toString(passedTimeMs),
                Long.toString(processedPackages),
                Integer.toString(totalLoss),
                Long.toString(totalLatencyMs),
                Double.toString(averageLatency),
                Double.toString(throughput)
        );
    }
}
