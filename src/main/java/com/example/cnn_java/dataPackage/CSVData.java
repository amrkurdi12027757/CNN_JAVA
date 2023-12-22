package com.example.cnn_java.dataPackage;

import com.example.cnn_java.dataPackage.dataProcessor.Processor;
import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvException;
import lombok.Data;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;

@Data
public class CSVData {
    private List<String[]> rows;
    private int numRows;
    private int numCols;

    public CSVData(File file) {
        try (CSVReader csvReader = new CSVReader(new FileReader(file))) {
            List<String[]> rows = csvReader.readAll();
            this.rows = rows;
            this.numRows = rows.size();
            this.numCols = rows.get(0).length;
        } catch (IOException | CsvException | NumberFormatException e) {
            throw new RuntimeException(e);
        }
    }

    public InOut getTrainingData(Processor processor) {
        return new InOut(processor.getTrainInputs(), processor.getTrainOutputs());
    }

    public InOut getTestingData(Processor processor) {
        return new InOut(processor.getTestInputs(), processor.getTestOutputs());
    }
}
