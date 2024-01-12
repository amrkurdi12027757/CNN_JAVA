package com.example.cnn_java.datapackage.dataprocessor;

public interface Processor {
    float[][] getTrainInputs();

    float[][] getTrainOutputs();

    float[][] getTestInputs();

    float[][] getTestOutputs();
}
