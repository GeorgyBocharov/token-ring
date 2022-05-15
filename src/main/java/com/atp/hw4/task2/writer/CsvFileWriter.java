package com.atp.hw4.task2.writer;

import com.atp.hw4.task2.entities.Statistics;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;

import java.io.FileWriter;
import java.io.IOException;

@RequiredArgsConstructor
@Slf4j
public class CsvFileWriter {

    private final String prefix;

    public void writeToFile(Statistics statistics) {
        String fileName = getFileName(statistics);
        log.info("Writing to file {} statistics: {}", fileName, statistics);
        try (CSVPrinter csvPrinter = createFileWriter(fileName) ) {
            csvPrinter.printRecord(statistics.toCsvData());
        } catch (IOException ex) {
            log.error("Failed to write to file {}", fileName, ex);
        }
    }

    private String getFileName(Statistics statistics) {
        return prefix +
                statistics.getNodeNumber() +
                "_" +
                statistics.getPackagesPerNode() +
                "_" +
                statistics.getProcessPerTime() +
                ".csv";
    }

    private CSVPrinter createFileWriter(String csvFileName) throws IOException{
        FileWriter writer = new FileWriter(csvFileName, true);
        return new CSVPrinter(writer, CSVFormat.DEFAULT);
    }
}
