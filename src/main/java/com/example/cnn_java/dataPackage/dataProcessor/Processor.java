package com.example.cnn_java.dataPackage.dataProcessor;

public interface Processor {
    float[][] getTrainInputs();

    float[][] getTrainOutputs();

    float[][] getTestInputs();

    float[][] getTestOutputs();
}
